package bankaccount;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.sharding.ClusterShardingSettings;
import bankaccount.actors.BuoyReadingActor;
import bankaccount.domain.siterep.BuoyReading;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class BuoyReadingsApp {
//    Map(Name -> LocationId)
//    Map("Gascoigne" -> "162001", "K1" -> "162029", "K7" -> "164046", "Brittany" -> "162163")
    //http://datapoint.metoffice.gov.uk/public/data/val/wxmarineobs/all/json/162001?res=daily&key=62d4967f-7932-4e40-805c-e5ace42531ee

    public static void main(String[] args){

        final ActorSystem system = ActorSystem.create("accountApp");
        ClusterShardingSettings settings = ClusterShardingSettings.create(system);
        ActorRef buoyReadingActorRegion = ClusterSharding.get(system)
                .start("BuoyReadingActor",
                        Props.create(BuoyReadingActor.class).withDispatcher("actor-dispatcher"),
                        settings,
                        BuoyReadingActor.shardRegionMessageExtractor);

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        HttpClient client = HttpClient.newBuilder()
                .proxy(ProxySelector.of(new InetSocketAddress("hqproxy.corp.ppbplc.com", 8080)))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://datapoint.metoffice.gov.uk/public/data/val/wxmarineobs/all/json/162001?res=daily&key=62d4967f-7932-4e40-805c-e5ace42531ee"))
                .build();

        String siteRepString = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .join();

        try {
            BuoyReading buoyReading = mapper.readValue(siteRepString, BuoyReading.class);
            buoyReadingActorRegion.tell(buoyReading, ActorRef.noSender());
        } catch (Exception e) {
            System.out.print("Unable to convert json to object" +  e);
        }
    }

}
