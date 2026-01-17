package api.movie;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.ApiUrls;
import model.Movie;
import model.ShowtimesByMovie;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * HTTP client for movie-related API endpoints.
 * Fetches movie data and showtimes per movie from the backend API.
 */
public class MovieApiClient {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Fetches the list of movies from the API.
     *
     * @return List of Movie objects.
     * @throws Exception if an error occurs during the HTTP request or JSON parsing.
     */
    public static List<Movie> fetchMoviesList() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiUrls.MOVIES))
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
     * Fetches showtime data for a specific movie by its ID.
     *
     * @param movieId The ID of the movie.
     * @return ShowtimesByMovie object containing showtime details.
     * @throws Exception if an error occurs during the HTTP request or JSON parsing.
     */
    public static ShowtimesByMovie fetchShowtimesByMovie(String movieId) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiUrls.showtimesByMovieId(movieId)))
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


