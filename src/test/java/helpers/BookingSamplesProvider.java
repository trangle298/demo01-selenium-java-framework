package helpers;

import model.Showtime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static api.movie.MovieDataExtractor.extractAllMovieShowtimeIds;
import static api.showtime.ShowtimeApiClient.fetchShowtimeData;
import static api.showtime.ShowtimeDataExtractor.getAvailableSeats;
import static api.showtime.ShowtimeDataExtractor.getReservedSeats;

/**
 * Provides sample showtime and seat data for booking tests.
 * Fetches real data from API and filters showtimes based on seat availability.
 *
 * <p>Use this to get random test data for:
 * <ul>
 *   <li>Showtimes with available seats</li>
 *   <li>Showtimes with no available seats (fully booked)</li>
 *   <li>Random seat selections from available seats</li>
 * </ul>
 */
public class BookingSamplesProvider {

    private static final Logger LOG = LogManager.getLogger(BookingSamplesProvider.class);

    private interface ShowtimeFilterFn {
        boolean test(String showtimeId, int seatQuantity) throws Exception;
    }

    private static List<Showtime> getShowtimesByFilter(
            ShowtimeFilterFn filterFn,
            Integer seatQuantity,
            Integer sampleSize
    ) throws Exception {

        int seats = seatQuantity != null ? seatQuantity : 1;
        int size = sampleSize != null ? sampleSize : 2;

        List<String> allShowtimeIds = extractAllMovieShowtimeIds();
        Collections.shuffle(allShowtimeIds);

        size = Math.min(size, allShowtimeIds.size());

        List<Showtime> sampleShowtimes = new ArrayList<>();

        for (String id : allShowtimeIds) {

            if (filterFn.test(id, seats)) {
                sampleShowtimes.add(fetchShowtimeData(id));
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

    public static List<Showtime> getShowtimesWithAvailableSeats(
            Integer seatQuantity,
            Integer sampleSize
    ) throws Exception {

        List<Showtime> sampleShowtimes;

        sampleShowtimes = getShowtimesByFilter(
                (showtimeId, seats) -> {
                    long availableSeats = getAvailableSeats(showtimeId).size();
                    return availableSeats >= seats;
                },
                seatQuantity,
                sampleSize
        );

        if (sampleShowtimes.isEmpty() && seatQuantity > 1) {
            LOG.info("No showtimes found with" + seatQuantity + "available seats." + "Retrying with seat quantity of 1.");
            sampleShowtimes = getShowtimesByFilter(
                    (showtimeId, seats) -> {
                        long availableSeats = getAvailableSeats(showtimeId).size();
                        return availableSeats >= seats;
                    },
                    1,
                    sampleSize
            );

            if (sampleShowtimes.isEmpty()) {
                throw new Error ("No showtimes found with available seats.");
            }

        }
        return sampleShowtimes;
    }

    // To be used in upcoming test cases
    public static List<Showtime> getShowtimesWithReservedSeats(
            Integer seatQuantity,
            Integer sampleSize
    ) throws Exception {

        List<Showtime> sampleShowtimes;

        sampleShowtimes = getShowtimesByFilter(
                (showtimeId, seats) -> {
                    long reservedSeats = getReservedSeats(showtimeId).size();
                    return reservedSeats >= seats;
                },
                seatQuantity,
                sampleSize
        );

        if (sampleShowtimes.isEmpty() && seatQuantity > 1) {
            LOG.info("No showtimes found with " + seatQuantity + " reserved seats. Retrying with seat quantity of 1.");
            sampleShowtimes = getShowtimesByFilter(
                    (showtimeId, seats) -> {
                        long reservedSeats = getReservedSeats(showtimeId).size();
                        return reservedSeats >= seats;
                    },
                    1,
                    sampleSize
            );
        }
        return sampleShowtimes;
    }

    public static List<String> getSampleSeats(List<String> seats, int sampleSize) {
        int actualSize = Math.min(sampleSize, seats.size());

        if (actualSize == seats.size()) {
            LOG.info("Requested sample size ({}) >= available seats ({}). Returning all available seats.", sampleSize, seats.size());
            return new ArrayList<>(seats);
        }

        Random random = new Random();
        List<String> sample = new ArrayList<>();
        List<String> pool = new ArrayList<>(seats);

        for (int i = 0; i < actualSize; i++) {
            int randomIndex = random.nextInt(pool.size());
            sample.add(pool.remove(randomIndex));
        }

        LOG.info("Selected {} random seats from {} available: {}", actualSize, seats.size(), sample);
        return sample;
    }

}
