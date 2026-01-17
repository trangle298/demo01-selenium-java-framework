package api.cinema;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.ApiUrls;
import model.Cinema;
import model.ShowtimesByCinema;

/**
 * HTTP client for cinema-related API endpoints.
 * Fetches cinema system data and showtime per cinema from the backend API.
 */
public class CinemaApiClient {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Fetches the list of cinemas from the API.
     *
     * @return List of Cinema objects.
     * @throws Exception if an error occurs during the HTTP request or JSON parsing.
     */
    public static List<Cinema> fetchCinemasList() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiUrls.CINEMAS))
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

    /**
     * Fetches showtime data for a specific cinema by its ID.
     *
     * @param cinemaId The ID of the cinema.
     * @return List of ShowtimesByCinema objects.
     * @throws Exception if an error occurs during the HTTP request or JSON parsing.
     */
    public static List<ShowtimesByCinema> fetchShowtimeDataByCinemaId(String cinemaId)
            throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiUrls.showtimesByCinemaId(cinemaId)))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        return mapper.readValue(
                response.body(),
                new TypeReference<>() {}
        );
    }
}
