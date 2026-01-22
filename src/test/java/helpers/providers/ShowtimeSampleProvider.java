package helpers.providers;

import api.services.CinemaService;
import api.services.MovieService;
import api.services.BookingService;
import model.api.response.Movie;
import model.api.response.ShowtimeBooking;
import model.api.response.MovieSchedule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Provides sample showtime(s) - random or filtered based on seat availability.
 * Fetches real data from API and for filtering.
 */
public class ShowtimeSampleProvider {

    private static final Logger LOG = LogManager.getLogger(ShowtimeSampleProvider.class);

    public static ShowtimeBooking getRandomShowtime() throws Exception {
        List<String> allShowtimeIds = getAllMovieShowtimeIds();

        if (allShowtimeIds.isEmpty()) {
            throw new Exception("No showtime IDs found from Movie domain API.");
        }

        String randomShowtimeId = RandomSampleProvider.getRandomSampleFromList(allShowtimeIds);

        LOG.info("Selected random showtime ID: " + randomShowtimeId);
        BookingService bookingService = new BookingService();
        return bookingService.getShowtimeBookingData(randomShowtimeId);
    }

    /**
     * Gets list of showtimes with at least the specified number of available seats.
     *
     * @param seatQuantity Minimum number of available seats required
     * @param sampleSize Number of showtimes to return
     * @return List of sample showtime details with available seats
     * @throws Exception if API calls fail
     */
    public static List<ShowtimeBooking> getShowtimeWithAvailableSeats(Integer seatQuantity, Integer sampleSize) throws Exception {

        List<ShowtimeBooking> sampleShowtimes;

        sampleShowtimes = getShowtimesByFilter(
                (showtimeId, seats) -> {
                    BookingService bookingService = new BookingService();
                    ShowtimeBooking showtime = bookingService.getShowtimeBookingData(showtimeId);

                    long availableSeats = showtime.getAvailableSeatsCount();
                    return availableSeats >= seats;
                },
                seatQuantity,
                sampleSize
        );

        if (sampleShowtimes.isEmpty() && seatQuantity > 1) {
            LOG.info("No showtimes found with" + seatQuantity + "available seats." + "Retrying with seat quantity of 1.");
            sampleShowtimes = getShowtimesByFilter(
                    (showtimeId, seats) -> {
                        BookingService bookingService = new BookingService();
                        ShowtimeBooking showtime = bookingService.getShowtimeBookingData(showtimeId);

                        long availableSeats = showtime.getAvailableSeatsCount();
                        return availableSeats >= seats;
                    },
                    1,
                    sampleSize
            );

            if (sampleShowtimes.isEmpty()) {
                throw new Error ("No showtimes found with available seats.");
            }

        }
        LOG.info("Showtimes found: " + sampleShowtimes);
        return sampleShowtimes;
    }

    /**
     * Gets one showtime with at least the specified number of available seats.
     *
     * @param seatQuantity Minimum number of available seats required
     * @return Sample showtime details with available seats
     * @throws Exception if API calls fail
     */
    public static ShowtimeBooking getShowtimeWithAvailableSeats(Integer seatQuantity) throws Exception {
        return getShowtimeWithAvailableSeats(seatQuantity, 1).get(0);
    }

    /**
     * Gets one showtime with at least 5 available seats.
     *
     * @return Sample showtime details with available seats
     * @throws Exception if API calls fail
     */
    public static ShowtimeBooking getShowtimeWithAvailableSeats() throws Exception {
        return getShowtimeWithAvailableSeats(5);
    }

    /**
     * Gets list of showtimes with at least the specified number of reserved seats.
     *
     * @param seatQuantity Minimum number of reserved seats required
     * @param sampleSize Number of showtimes to return
     * @return List of sample showtime details with reserved seats
     * @throws Exception if API calls fail
     */
    public static List<ShowtimeBooking> getShowtimeWithReservedSeats(Integer seatQuantity, Integer sampleSize) throws Exception {

        List<ShowtimeBooking> sampleShowtimes;

        sampleShowtimes = getShowtimesByFilter(
                (showtimeId, seats) -> {
                    BookingService bookingService = new BookingService();
                    ShowtimeBooking showtime = bookingService.getShowtimeBookingData(showtimeId);

                    long reservedSeats = showtime.getReservedSeatsCount();
                    return reservedSeats >= seats;
                },
                seatQuantity,
                sampleSize
        );

        if (sampleShowtimes.isEmpty() && seatQuantity > 1) {
            LOG.info("No showtimes found with " + seatQuantity + " reserved seats. Retrying with seat quantity of 1.");
            sampleShowtimes = getShowtimesByFilter(
                    (showtimeId, seats) -> {
                        BookingService bookingService = new BookingService();
                        ShowtimeBooking showtime = bookingService.getShowtimeBookingData(showtimeId);

                        long reservedSeats = showtime.getReservedSeatsCount();
                        return reservedSeats >= seats;
                    },
                    1,
                    sampleSize
            );
        }
        return sampleShowtimes;
    }

    // ========================================================================
    // Private helper methods
    // ========================================================================
    /**
     * Get all showtime IDs across all movies from the Movie domain API.
     *
     * @return List of all showtime IDs
     */
    private static List<String> getAllMovieShowtimeIds() {
        MovieService movieService = new MovieService();
        CinemaService cinemaService = new CinemaService();

        List<Movie> movies = movieService.getMovieList();
        Set<String> allShowtimeIds = new HashSet<>();

        for (Movie movie : movies) {
            MovieSchedule moviesSchedule = cinemaService.getShowtimesForMovie(movie.getMaPhim());
            Set<String> ids = moviesSchedule.getShowtimeIds();
            allShowtimeIds.addAll(ids);
        }
        return new ArrayList<>(allShowtimeIds);
    }

    /**
     *  Functional interface for filtering showtimes based on custom criteria.
     */
    private interface ShowtimeFilterFn {
        boolean test(String showtimeId, int seatQuantity) throws Exception;
    }

    /**
     * Generic method to get showtimes filtered by a custom criteria.
     *
     * @param filterFn Function defining the filter criteria
     * @param seatQuantity Minimum number of seats required
     * @param sampleSize Number of showtimes to return
     * @return List of showtime details matching the filter criteria
     * @throws Exception if API calls fail
     */
    private static List<ShowtimeBooking> getShowtimesByFilter(ShowtimeFilterFn filterFn, Integer seatQuantity, Integer sampleSize) throws Exception {

        int seats = seatQuantity != null ? seatQuantity : 1;
        int size = sampleSize != null ? sampleSize : 2;

        List<String> allShowtimeIds = getAllMovieShowtimeIds();

        Collections.shuffle(allShowtimeIds);

        size = Math.min(size, allShowtimeIds.size());

        List<ShowtimeBooking> sampleShowtimes = new ArrayList<>();

        for (String id : allShowtimeIds) {

            if (filterFn.test(id, seats)) {
                BookingService bookingService = new BookingService();
                sampleShowtimes.add(bookingService.getShowtimeBookingData(id));
            }

            if (sampleShowtimes.size() >= size) break;
        }

        if (sampleShowtimes.isEmpty()) {
            LOG.info("No showtimes found matching criteria.");
        } else if (sampleShowtimes.size() < size) {
            LOG.info("Only found " + sampleShowtimes.size() + " showtimes matching criteria.");
        }
        return sampleShowtimes;
    }
}