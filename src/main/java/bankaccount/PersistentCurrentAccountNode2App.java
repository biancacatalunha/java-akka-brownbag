package bankaccount;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.sharding.ClusterShardingSettings;
import bankaccount.actors.PersistentCurrentAccount;
import bankaccount.domain.Deposit;
import bankaccount.domain.Withdraw;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class PersistentCurrentAccountNode2App {

    public static void main(String [] args) {
        Config nodeConfig = ConfigFactory.parseString("akka.remote.netty.tcp.port=2552");
        Config config = nodeConfig.withFallback(ConfigFactory.defaultApplication());
        final ActorSystem system = ActorSystem.create("accountApp", config);
        ClusterShardingSettings settings = ClusterShardingSettings.create(system);

        ActorRef persistentCurrentAccountRegion = ClusterSharding.get(system).start(
                "PersistCurrentAccount",
                Props.create(PersistentCurrentAccount.class).withDispatcher("actor-dispatcher"),
                settings,
                PersistentCurrentAccount.shardRegionMessageExtractor
        );

        persistentCurrentAccountRegion.tell(new Deposit(100.00, 123422226, "Node 2", System.currentTimeMillis()), ActorRef.noSender());
        persistentCurrentAccountRegion.tell(new Withdraw(15.00, 123422226, "Node 2", System.currentTimeMillis()), ActorRef.noSender());
        persistentCurrentAccountRegion.tell(new Deposit(200.00, 123422227, "Node 2", System.currentTimeMillis()), ActorRef.noSender());
        persistentCurrentAccountRegion.tell(new Withdraw(100.00, 123422227, "Node 2", System.currentTimeMillis()), ActorRef.noSender());
        persistentCurrentAccountRegion.tell(new Deposit(3.00, 123422226, "Node 2", System.currentTimeMillis()), ActorRef.noSender());
        persistentCurrentAccountRegion.tell(new Deposit(5.00, 123422226, "Node 2", System.currentTimeMillis()), ActorRef.noSender());
        persistentCurrentAccountRegion.tell(new Withdraw(10.00, 123422226, "Node 2", System.currentTimeMillis()), ActorRef.noSender());
    }
}
