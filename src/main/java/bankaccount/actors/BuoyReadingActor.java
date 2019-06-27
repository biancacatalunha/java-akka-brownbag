package bankaccount.actors;

import akka.actor.PoisonPill;
import akka.actor.ReceiveTimeout;
import akka.cluster.sharding.ShardRegion;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.persistence.AbstractPersistentActor;
import akka.persistence.RecoveryCompleted;
import bankaccount.domain.siterep.*;

import java.time.Duration;
import java.util.ArrayList;

public class BuoyReadingActor extends AbstractPersistentActor {

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private int noMessagesSinceLastSnapshot = 0;
    private BuoyReadingState state = new BuoyReadingState(new ArrayList<>(), "none");

    private String getId() {
        return self().path().name();
    }

    static public ShardRegion.MessageExtractor shardRegionMessageExtractor =
            new ShardRegion.MessageExtractor() {
                @Override
                public String shardId(Object message) {
                    int numOfShards = 100;

                    if(message instanceof BuoyReading) {
                        int locationId = ((BuoyReading) message).getSiteRep().getDV().getLocation().getI();
                        return String.valueOf(locationId % numOfShards);
                    } else if(message instanceof ShardRegion.StartEntity) {
                        String id = ((ShardRegion.StartEntity) message).entityId();
                        return String.valueOf(Long.valueOf(id) % numOfShards);
                    }
                    return null;
                }

                @Override
                public String entityId(Object message) {
                    if(message instanceof BuoyReading) {
                        return ((BuoyReading) message).getSiteRep().getDV().getLocation().getI().toString();
                    }
                    return null;
                }

                @Override
                public Object entityMessage(Object message) {
                    return message;
                }
            };

    @Override
    public void preStart() throws Exception {
        getContext().setReceiveTimeout(Duration.ofSeconds(10));
        super.preStart();
    }

    @Override
    public void postStop() {
        log.info("Buoy Reading: " + getId() + " [stopped]");
    }

    @Override
    public Receive createReceiveRecover() {
        return receiveBuilder()
                .match(BuoyReadingEvent.class, this::applyReading)
                .match(RecoveryCompleted.class, (RecoveryCompleted c) -> {
                    logState();
                    getContext().setReceiveTimeout(Duration.ofSeconds(4));
                })
                .build();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BuoyReadingCommand.class, s -> {
                    final BuoyReadingEvent  buoyReadingEvent = s.toEvent();
                    persist(buoyReadingEvent, this::applyReading);
                    log.info("Persisting reading " + buoyReadingEvent.getSiteRep().getDV().getDataDate());
                })
                .match(ReceiveTimeout.class, r -> {
                    log.info("Buoy Reading: " + getId() + " - ReceiveTimeout");
                    getContext().getParent().tell(new ShardRegion.Passivate(PoisonPill.getInstance()), getSelf());
                })
                .match(PoisonPill.class, pp -> getContext().stop(getSelf()))
                .build();
    }

    @Override
    public String persistenceId() {
        return "BuoyReadingActor-" + self().path().name();
    }

    private void applyReading(BuoyReading buoyReading){
        state.apply(buoyReading);
    }

    private void logState() {
        log.info("Latest reading at : " + state.getLastReadingAt() + " no readings: " + state.getSize());
    }
}
