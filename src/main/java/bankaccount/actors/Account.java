package bankaccount.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.ReceiveTimeout;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import bankaccount.domain.Deposit;
import bankaccount.domain.Transaction;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/*
actor is a queue, only processes one message at a time
CRDT for clustering
d data balances actors across nodes with persistence
Shard region used for clustering. Will you accountNo for it
@override shardId
Event sourcing -> Taking something that happened and storing it and then when the actor comes back alive, replay all the messages
snapshoting helps mitigating the price of replaying events

Time series stores a sequence of data that led to it's current state


Whenever you're sending data through nodes, you need to serialize data. Proto buff for prod and java serialization for dev

Thread pool - dispatchers. 1 for akka system, 1 for the cluster (gossip to keep the cluster), one for actors

 */
public class Account extends AbstractActor {

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    static public Props props(String accountNo) { //creates an instance of an actor
        return Props.create(Account.class, accountNo);
    }

    private List<Transaction> transactionsList = new ArrayList<>();

    private String accountNo;
    private Double balance;
    private int seqNo;

    public Account(String accountNo) {
        this.accountNo = accountNo;
        this.balance = 100.0;
        this.seqNo = 0;
    }

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
    public Receive createReceive() {
        return receiveBuilder()
                .match(Transaction.class, this::transactionMade)
                //in clustering we should use passivation
                .match(ReceiveTimeout.class, rt -> getContext().stop(this.getSelf()))//this will stop the actor
                .build();
    }

    private void transactionMade(Transaction d) {
        seqNo++;
        log.info(d.getLog(accountNo));
        transactionsList.add(d);
        balance = d.calculateBalance(balance);
        log.info("seqNo = " + seqNo +" new balance = " + balance);

        for(Transaction t : transactionsList) {
            log.info("Type " + t.getClass().toString() + " Amount " + t.getAmount());
        }
    }
}
