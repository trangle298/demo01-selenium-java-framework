package api.Movie;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.ApiUrls;
import model.Movie;
import model.ShowtimesByCinema;
import model.ShowtimesByMovie;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class MovieApiClient {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

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


