package bankaccount;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.Flow;


import java.util.Arrays;
import java.util.concurrent.CompletionStage;

public class FlowDemoApp {
    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("FlowDemo");
        final Materializer materializer = ActorMaterializer.create(system);

//        final Source<Integer, NotUsed> source = Source.from(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
//        final Sink<Integer, CompletionStage<Integer>> sink = Sink.<Integer, Integer>fold(0, (aggr, next) -> aggr + next);
//        final RunnableGraph<CompletionStage<Integer>> runnable = source.toMat(sink, Keep.right());
//        final CompletionStage<Integer> sum = runnable.run(materializer);
//        sum.thenAccept(System.out::println);

        Source.from(Arrays.asList(1, 2, 3, 4))
                .via(Flow.of(Integer.class).map(elem -> elem * 2))
                .to(Sink.foreach(System.out::println)).run(materializer);
    }
}
