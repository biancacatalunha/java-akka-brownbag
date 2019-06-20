package bankaccount.actors;

import akka.actor.AbstractActor;
import akka.actor.PoisonPill;
import akka.actor.ReceiveTimeout;
import akka.cluster.sharding.ShardRegion;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.persistence.AbstractPersistentActor;
import akka.persistence.RecoveryCompleted;
import akka.persistence.SaveSnapshotSuccess;
import akka.persistence.SnapshotOffer;
import bankaccount.domain.*;

import java.time.Duration;
import java.util.ArrayList;

public class PersistentCurrentAccount extends AbstractPersistentActor {

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private int noMessagesSinceLastSnapshot = 0;
    private AccountState accountState = new AccountState(new ArrayList<>(), 0.0, Long.valueOf(getId()));

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
        log.info("Persistence Current Account : " + getId() + " [stopped]");
    }

    @Override
    public Receive createReceiveRecover() {
        return receiveBuilder()
                .match(Transaction.class, this::applyTransaction)
                .match(SnapshotOffer.class, this::applySnapshot)
                .match(RecoveryCompleted.class, (RecoveryCompleted c) -> {
                    logBalance();
                    getContext().setReceiveTimeout(Duration.ofSeconds(4));
                })
                .build();
    }

    private void applySnapshot(SnapshotOffer snapshotOffer) {
        if(snapshotOffer.snapshot() instanceof AccountState) {
            log.info("Applying snapshot");
            accountState = (AccountState) snapshotOffer.snapshot();
        }
    }

    //Mandatory method to define the type of messages accepted and how to process them COMMANDS
    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(Deposit.class, d -> {
                    final Deposited deposited = new Deposited(d.getAmount(), d.getAccountNo(), d.getTransactionMadeBy(), d.getTransactionDate());
                    persist(deposited, this::applyTransaction);
                    //When persisting events with persist it is guaranteed that the persistent actor will not receive
                    //further commands between the persist call and the execution(s) of the associated event handler.
                })
                .match(Withdraw.class, w -> {
                    final Withdrawn withdrawn = new Withdrawn(w.getAmount(), w.getAccountNo(), w.getTransactionMadeBy(), w.getTransactionDate());
                    persist(withdrawn, this::applyTransaction);
                })
                //in clustering we should use passivation
                .match(ReceiveTimeout.class, r -> {
                    log.info("Persistent Current Account : " + getId() + " - ReceiveTimeout");
                    getContext().getParent().tell(new ShardRegion.Passivate(PoisonPill.getInstance()), getSelf());
                })
                .match(SaveSnapshotSuccess.class, ss -> noMessagesSinceLastSnapshot = 0)
                .match(PoisonPill.class, pp -> getContext().stop(getSelf()))
                .build();
    }

    private void applyTransaction(Transaction transaction) {
        noMessagesSinceLastSnapshot ++;

        accountState.apply(transaction);
        log.info("Account number: " + transaction.getAccountNo() + " -> " + transaction.getAmount() + " " + transaction.getClass().getSimpleName());
        logBalance();

        if(noMessagesSinceLastSnapshot >= 5) {
            log.info("Saving snapshot");
            saveSnapshot(accountState);
        }
    }

    @Override
    public String persistenceId() {
        return "PersistentCurrentAccount-" + self().path().name();
    }

    private void logBalance() {
        log.info("Persistent ID = " + getId() + " seqNo = " + noMessagesSinceLastSnapshot +" new balance = " + accountState.getBalance());
    }
}
