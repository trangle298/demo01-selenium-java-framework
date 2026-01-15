package api.Movie;

import model.Movie;
import model.ShowtimeDetails;
import model.ShowtimesByMovie;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MovieDataExtractor {


    public static List<String> extractAllMovieTitles() throws Exception {
        List<Movie> movies = MovieApiClient.fetchMoviesList();
        List<String> movieTitles = new ArrayList<>();
        for (Movie movie : movies) {
            movieTitles.add(movie.getTenPhim());
        }
        return movieTitles;
    }

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
