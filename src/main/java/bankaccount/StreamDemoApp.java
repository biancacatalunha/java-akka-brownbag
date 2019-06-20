package bankaccount;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Source;
import akka.util.ByteString;

import java.math.BigInteger;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.CompletionStage;

public class StreamDemoApp {
    public static void main(String[] args) {

        final ActorSystem system = ActorSystem.create("StreamDemo");
        final Materializer materializer = ActorMaterializer.create(system);
        final Source<Integer, NotUsed> source = Source.range(1, 100); //source is a description of what we want to run
        final CompletionStage<Done> done = source.runForeach(System.out::println, materializer); //runForeach returns when the stream is finished

        final Source<BigInteger, NotUsed> factorials = source.scan(BigInteger.ONE, (acc, next) ->
                acc.multiply(BigInteger.valueOf(next)));
        factorials
                .map(num -> ByteString.fromString(num.toString() + " \n"))
                .runWith(FileIO.toPath(Paths.get("factorials.txt")), materializer);

        final CompletionStage<Done> result = factorials
        .zipWith(Source.range(0, 99), (num, idx) -> String.format("%d! = %s", idx, num))
        .throttle(1, Duration.ofSeconds(1))
        .runForeach(System.out::println, materializer);

        result.thenRun(system::terminate); //stops the app when the stream is finished
    }
}
