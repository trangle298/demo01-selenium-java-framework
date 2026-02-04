package api.services;

import api.ApiClient;
import api.ApiConfig;
import api.ApiConstants;
import io.restassured.common.mapper.TypeRef;
import model.api.response.Movie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieService {

    private final ApiClient apiClient;

    public MovieService() {
        this.apiClient = new ApiClient(ApiConfig.getBaseUri());
    }

    public List<Movie> getMovieList() {
        return this.apiClient.withQueryParam("maNhom", "GP09")
                .getAndDeserialize(ApiConstants.MOVIE_LIST_ENDPOINT, new TypeRef<>() {
                });
    }

    public Movie getMovieInfoById(String movieId) {
        return this.apiClient
                .withQueryParam("movieId", movieId)
                .getAndDeserialize(ApiConstants.MOVIE_LIST_ENDPOINT, Movie.class);
    }

    // ===== Data Transformation Utilities =====

    /**
     * Get all movies as a map of movie ID to movie title.
     * Useful for comparing with UI dropdowns or quick lookups.
     *
     * @return Map where key is movie ID (maPhim) and value is movie title (tenPhim)
     */
    public Map<String, String> getMovieIdToTitleMap() {
        List<Movie> movies = this.getMovieList();
        Map<String, String> movieTitles = new HashMap<>();
        movies.forEach(movie -> {
            movieTitles.put(movie.getMaPhim(), movie.getTenPhim());
        });
        return movieTitles;
    }
}
