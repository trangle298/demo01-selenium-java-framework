package api.Showtime;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.ApiUrls;
import model.Showtime;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ShowtimeApiClient {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Showtime fetchShowtimeData(String showtimeId) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiUrls.showtimeDataByShowtimeId(showtimeId)))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        return mapper.readValue(
                response.body(),
                new TypeReference<>() {
                }
        );
    }

}
