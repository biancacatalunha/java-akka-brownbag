package bankaccount;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.sharding.ClusterShardingSettings;
import bankaccount.actors.PersistentCurrentAccount;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class PersistentCurrentAccountNode1App {

    public static void main(String [] args) {
        Config nodeConfig = ConfigFactory.parseString("akka.remote.netty.tcp.port=2551");
        Config config = nodeConfig.withFallback(ConfigFactory.defaultApplication());
        final ActorSystem system = ActorSystem.create("accountApp", config);
        ClusterShardingSettings settings = ClusterShardingSettings.create(system);

        ClusterSharding.get(system).start(
                "PersistCurrentAccount",
                Props.create(PersistentCurrentAccount.class).withDispatcher("actor-dispatcher"),
                settings,
                PersistentCurrentAccount.shardRegionMessageExtractor
        );
    }
}
