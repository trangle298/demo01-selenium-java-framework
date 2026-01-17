package api.movie;

import model.Movie;
import model.ShowtimeDetails;
import model.ShowtimesByMovie;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import api.movie.MovieApiClient;

/**
 * Extracts and transforms movie data from API responses.
 * Provides methods to get movie titles, IDs, and showtime information.
 */
public class MovieDataExtractor {

    /**
     * Extracts all movie titles from the API response.
     *
     * @return List of movie titles.
     * @throws Exception if an error occurs during data fetching.
     */
    public static List<String> extractAllMovieTitles() throws Exception {
        List<Movie> movies = MovieApiClient.fetchMoviesList();
        List<String> movieTitles = new ArrayList<>();
        for (Movie movie : movies) {
            movieTitles.add(movie.getTenPhim());
        }
        return movieTitles;
    }

    /**
     * Extracts all unique showtime IDs across all movies from the API response.
     *
     * @return List of unique showtime IDs.
     * @throws Exception if an error occurs during data fetching.
     */
    public static List<String> extractAllMovieShowtimeIds() throws Exception {

        Set<String> showtimeIds = new HashSet<>();
        List<Movie> movies = MovieApiClient.fetchMoviesList();

        for (Movie movie : movies) {
            String maPhim = movie.getMaPhim();
            List<String> movieShowtimeIds = extractShowtimeIdsForMovie(maPhim);
            showtimeIds.addAll(movieShowtimeIds);
        }

        return new ArrayList<>(showtimeIds);
    }

    /**
     * Retrieves the movie ID for a given movie title.
     *
     * @param movieTitle The title of the movie.
     * @return The movie ID.
     * @throws Exception if the movie is not found or multiple IDs are found.
     */
    public static String getMovieIdByTitle(String movieTitle) throws Exception {
        List<Movie> movies = MovieApiClient.fetchMoviesList();
        Set<String> movieIds = new HashSet<>();

        for (Movie movie : movies) {
            if (movie.getTenPhim().equalsIgnoreCase(movieTitle)) {
                movieIds.add(movie.getMaPhim());
            }
        }

        if (movieIds.isEmpty()) {
            throw new Exception("Movie with title '" + movieTitle + "' not found.");
        }
        else if (movieIds.size() > 1) {
            throw new Exception("Multiple movie Ids found with title '" + movieTitle + "'. Ids = " + movieIds);
        }

        return movieIds.iterator().next();
    }

    /**
     * Extracts all unique cinema locations showing a specific movie by its title.
     *
     * @param movieTitle The title of the movie.
     * @return List of unique cinema locations.
     * @throws Exception if an error occurs during data fetching.
     */
    public static List<String> extractAllCinemaLocationsByMovieTitle(String movieTitle) throws Exception {

        Set<String> cinemaLocations = new HashSet<>();
        String movieId = getMovieIdByTitle(movieTitle);

        ShowtimesByMovie showtimesByMovies = MovieApiClient.fetchShowtimesByMovie(movieId);

        for (ShowtimesByMovie.ShowtimesPerCinema cinema : showtimesByMovies.getHeThongRapChieu()) {
            for (ShowtimesByMovie.ShowtimesPerBranch branch : cinema.getCumRapChieu()) {
                cinemaLocations.add(branch.getTenCumRap());
            }
        }
        return new ArrayList<>(cinemaLocations);
    }

    /**
     * Extracts all unique showtime IDs for a specific movie by its ID.
     *
     * @param movieId The ID of the movie.
     * @return List of unique showtime IDs.
     * @throws Exception if an error occurs during data fetching.
     */
    public static List<String> extractShowtimeIdsForMovie(String movieId) throws Exception {

        Set<String> showtimeIds = new HashSet<>();
        ShowtimesByMovie showtimesByMovies = MovieApiClient.fetchShowtimesByMovie(movieId);

        for (ShowtimesByMovie.ShowtimesPerCinema cinema : showtimesByMovies.getHeThongRapChieu()) {
            for (ShowtimesByMovie.ShowtimesPerBranch branch : cinema.getCumRapChieu()) {
                for (ShowtimeDetails showtime : branch.getLichChieuPhim()) {
                    showtimeIds.add(showtime.getMaLichChieu());
                }
            }
        }
        return new ArrayList<>(showtimeIds);
    }
}
