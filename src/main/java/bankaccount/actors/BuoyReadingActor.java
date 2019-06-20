package bankaccount.actors;

import akka.cluster.sharding.ShardRegion;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.persistence.AbstractPersistentActor;
import bankaccount.domain.siterep.BuoyReading;
import bankaccount.domain.siterep.BuoyReadingCommand;

public class BuoyReadingActor extends AbstractPersistentActor {

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

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
    public Receive createReceiveRecover() {
        return null;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BuoyReadingCommand.class, s -> log.info(s.getSiteRep().getDV().getDataDate()))
                .build();
    }

    @Override
    public String persistenceId() {
        return "BuoyReadingActor-" + self().path().name();
    }
}
