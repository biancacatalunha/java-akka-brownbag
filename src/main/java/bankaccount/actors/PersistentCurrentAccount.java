package bankaccount.actors;

import akka.actor.AbstractActor;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.ReceiveTimeout;
import akka.cluster.sharding.ShardRegion;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.persistence.AbstractPersistentActor;
import akka.persistence.SaveSnapshotSuccess;
import akka.persistence.SnapshotOffer;
import bankaccount.domain.Transaction;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class PersistentCurrentAccount extends AbstractPersistentActor {
    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private List<Transaction> transactionsList = new ArrayList<>();

    private String accountNo;
    private Double balance;
    private int noMessagesSinceLastSnapshot;

    //Props is a configuration class to specify options for the creation of actors
    static public Props props(Long accountNo) { //creates an instance of an actor
        return Props.create(Account.class, accountNo);
    }

    public PersistentCurrentAccount(String accountNo) {
        this.accountNo = accountNo;
        this.balance = 100.0;
        this.noMessagesSinceLastSnapshot = 0;
    }

    private String getId() {
        return self().path().name();
    }

    static public ShardRegion.MessageExtractor shardRegionMessageExtractor =
            new ShardRegion.MessageExtractor() {
                @Override
                public String shardId(Object message) {
                    int numberOfShards = 100;

                    if(message instanceof Transaction) {
                        long id = ((Transaction) message).getAccountNo().hashCode();
                        return String.valueOf(id % numberOfShards);
                    } else if (message instanceof ShardRegion.StartEntity) {  //????????
                        String id = ((ShardRegion.StartEntity) message).entityId();
                        return String.valueOf(Long.valueOf(id) % numberOfShards);
                    }
                    return null;
                }

                @Override
                public String entityId(Object message) {
                    if(message instanceof Transaction) {
                        return ((Transaction) message).getAccountNo().toString();
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
        //timeout for when the actor didn't receive a message
        getContext().setReceiveTimeout(Duration.ofSeconds(10));
        super.preStart();
    }

    @Override
    public void postStop() {
        log.info("Account " + accountNo + " stopped");
    }

    @Override
    public void aroundPostStop() {
        log.info("Persistence Current Account : " + getId() + " [stopped]");
        super.aroundPostStop();
    }

    @Override
    public Receive createReceiveRecover() {
        return receiveBuilder()
                .match(Transaction.class, this::transactionMade)
                .match(SnapshotOffer.class, this::applySnapshot)
                .build();
    }

    private void applySnapshot(SnapshotOffer snapshotOffer) {
    }

    //Mandatory method to define the type of messages accepted and how to process them
    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(Transaction.class, this::transactionMade)
                //in clustering we should use passivation
                .match(ReceiveTimeout.class, rt -> getContext().stop(this.getSelf()))//this will stop the actor
                .match(SaveSnapshotSuccess.class, ss -> noMessagesSinceLastSnapshot = 0)
                .match(PoisonPill.class, pp -> getContext().stop(getSelf()))
                .build();
    }

    private void transactionMade(Transaction d) {
        noMessagesSinceLastSnapshot ++;

        if(noMessagesSinceLastSnapshot == 5) {
            saveSnapshot("accountNo: " + accountNo + ", balance:" + balance);
        }

        log.info(d.getLog());
        transactionsList.add(d);
        balance = d.calculateBalance(balance);
        log.info("Persistent ID = " + getId() + " seqNo = " + noMessagesSinceLastSnapshot +" new balance = " + balance);
    }

    @Override
    public String persistenceId() {
        return "PersistentCurrentAccount-" + self().path().name();
    }
}
