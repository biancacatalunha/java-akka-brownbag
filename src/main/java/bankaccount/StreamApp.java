package bankaccount;

import akka.NotUsed;
import akka.actor.*;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.sharding.ClusterShardingSettings;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import bankaccount.actors.PersistentCurrentAccount;
import bankaccount.domain.AccountState;

import java.time.Duration;

public class StreamApp {
    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("accountApp");
        ClusterShardingSettings settings = ClusterShardingSettings.create(system);
        ActorRef persistCurrentAccountRegion = ClusterSharding.get(system)
                .start("PersistCurrentAccount",
                        Props.create(PersistentCurrentAccount.class).withDispatcher("actor-dispatcher"),
                        settings,
                        PersistentCurrentAccount.shardRegionMessageExtractor);
        //The Materializer is a factory for stream execution engines, it is the thing that makes streams run
        final Materializer materializer = ActorMaterializer.create(system);

        Source<String, Cancellable> transactionSrc = Source.tick(Duration.ofSeconds(1), Duration.ofSeconds(10), "1230000");
        Sink<AccountState, NotUsed> actorSink = Sink.actorRef(persistCurrentAccountRegion, Status.Success.class);

    }
}
