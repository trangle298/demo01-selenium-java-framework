package api.showtime;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.ApiUrls;
import model.Showtime;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * HTTP client for showtime-related API endpoints.
 * Fetches showtime details including seat availability and theater information.
 */
public class ShowtimeApiClient {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Fetches showtime data for a specific showtime by its ID.
     *
     * @param showtimeId The ID of the showtime.
     * @return Showtime object containing showtime details.
     * @throws Exception if an error occurs during the HTTP request or JSON parsing.
     */
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
