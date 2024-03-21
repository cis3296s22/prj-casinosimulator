import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class TempleAlertFetch {
    public static void main(String[] args) throws Exception {
        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://www.reddit.com/r/Temple/new.json?limit=1"))
                .header("User-Agent", "YourApp/0.1 by YourRedditUsername")
                .GET()
                .build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
        System.out.println(httpResponse.body());
    }
}

