package bankaccount;

import akka.Done;
import akka.NotUsed;
import akka.actor.*;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.sharding.ClusterShardingSettings;
import akka.stream.ActorMaterializer;
import akka.stream.ClosedShape;
import akka.stream.Materializer;
import akka.stream.SinkShape;
import akka.stream.javadsl.*;
import bankaccount.actors.BuoyReadingActor;
import bankaccount.domain.siterep.BuoyConfig;
import bankaccount.domain.siterep.BuoyConfigObject;
import bankaccount.domain.siterep.BuoyReadingCommand;
import bankaccount.domain.siterep.Location;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class BuoyReadingsApp {

    private final static HttpClient client = HttpClient.newBuilder()
            .proxy(ProxySelector.of(new InetSocketAddress("hqproxy.corp.ppbplc.com", 8080)))
            .build();

    public static void main(String[] args){

        final ActorSystem system = ActorSystem.create("accountApp");
        ClusterShardingSettings settings = ClusterShardingSettings.create(system);
        ActorRef buoyReadingActorRegion = ClusterSharding.get(system)
                .start("BuoyReadingActor",
                        Props.create(BuoyReadingActor.class).withDispatcher("actor-dispatcher"),
                        settings,
                        BuoyReadingActor.shardRegionMessageExtractor);
        final Materializer materializer = ActorMaterializer.create(system);



        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        BuoyConfig buoyConfig = new BuoyConfig();
        BuoyConfigObject config = buoyConfig.getConfig();

        Source<List<Location>, Cancellable> source = Source.tick(Duration.ZERO, Duration.ofHours(1), config.getLocation());
        Sink<BuoyReadingCommand, NotUsed> actorSink = Sink.actorRef(buoyReadingActorRegion, Status.Success.class);

//        final RunnableGraph<NotUsed> result = RunnableGraph.fromGraph(
//                GraphDSL.create(
//                        actorSink,
//                        (GraphDSL.Builder<CompletionStage<Done>> builder, SinkShape sink) -> {
//                            builder.from(builder.add(source))
//                                    .via(builder.add(Flow.of(Location.class).map(e -> getHttpRequest(e, config))))
//                                    .via(builder.add(Flow.of(HttpRequest.class).map(BuoyReadingsApp::getHttpResponse)))
//                                    .via(builder.add(Flow.of(String.class).map(r -> mapper.readValue(r, BuoyReadingCommand.class))))
//                                    .to(sink);
//                            return ClosedShape.getInstance();
//                        }));
//        result.run(materializer);

        Source.from(config.getLocation())
                .via(Flow.of(Location.class).map(e -> getHttpRequest(e, config)))
                .via(Flow.of(HttpRequest.class).map(BuoyReadingsApp::getHttpResponse))
                .via(Flow.of(String.class).map(r -> mapper.readValue(r, BuoyReadingCommand.class)))
                .log("Error logging")
                .to(Sink.actorRef(buoyReadingActorRegion, Status.Success.class))
                .run(materializer);
//
//        config.getLocation().forEach(k -> {
//
//            URI url = URI.create("http://datapoint.metoffice.gov.uk/public/data/val/wxmarineobs/all/json/" + k.getI() + "?res=daily&key=" + config.getKey());
//            HttpRequest request = HttpRequest.newBuilder().uri(url).build();
//
//            String siteRepString = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                    .thenApply(HttpResponse::body)
//                    .join();
//
//            try {
//                BuoyReadingCommand buoyReading = mapper.readValue(siteRepString, BuoyReadingCommand.class);
//                buoyReadingActorRegion.tell(buoyReading, ActorRef.noSender());
//            } catch (Exception e) {
//                System.out.print("Unable to convert json to object" +  e);
//            }
//        });
    }

    private static HttpRequest getHttpRequest(Location k, BuoyConfigObject config) {
        URI url = URI.create("http://datapoint.metoffice.gov.uk/public/data/val/wxmarineobs/all/json/" + k.getI() + "?res=daily&key=" + config.getKey());
        return HttpRequest.newBuilder().uri(url).build();
    }

    private static String getHttpResponse(HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .join();
    }
}
