package helpers;

import model.Showtime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static api.Movie.MovieDataExtractor.extractAllMovieShowtimeIds;
import static api.Showtime.ShowtimeApiClient.fetchShowtimeData;
import static api.Showtime.ShowtimeDataExtractor.getAvailableSeats;
import static api.Showtime.ShowtimeDataExtractor.getReservedSeats;

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

        if (sampleShowtimes.size() == 0) {
            System.out.println("No showtimes found matching criteria.");
        } else if (sampleShowtimes.size() < size) {
            System.out.println("Only found " + sampleShowtimes.size() + "showtimes matching criteria.");
        }
        return sampleShowtimes;
    }

    public static List<Showtime> getShowtimesWithAvailableSeats(
            Integer seatQuantity,
            Integer sampleSize
    ) throws Exception {

        List<Showtime> sampleShowtimes = new ArrayList<>();

        sampleShowtimes = getShowtimesByFilter(
                (showtimeId, seats) -> {
                    Showtime showtime = fetchShowtimeData(showtimeId);
                    long availableSeats = getAvailableSeats(showtimeId).size();
                    return availableSeats >= seats;
                },
                seatQuantity,
                sampleSize
        );

        if (sampleShowtimes.size() == 0 && seatQuantity > 1) {

            LOG.info("No showtimes found with" + seatQuantity + "available seats." + "Retrying with seat quantity of 1.");

            sampleShowtimes = getShowtimesByFilter(
                    (showtimeId, seats) -> {
                        Showtime showtime = fetchShowtimeData(showtimeId);
                        long availableSeats = getAvailableSeats(showtimeId).size();
                        return availableSeats >= seats;
                    },
                    1,
                    sampleSize
            );

            if (sampleShowtimes.size() == 0) {
                throw new Error ("No showtimes found with available seats.");
            }

        }
        return sampleShowtimes;
    }

    public static List<Showtime> getShowtimesWithReservedSeats(
            Integer seatQuantity,
            Integer sampleSize
    ) throws Exception {

        List<Showtime> sampleShowtimes = new ArrayList<>();

        sampleShowtimes = getShowtimesByFilter(
                (showtimeId, seats) -> {
                    Showtime showtime = fetchShowtimeData(showtimeId);
                    long reservedSeats = getReservedSeats(showtimeId).size();
                    return reservedSeats >= seats;
                },
                seatQuantity,
                sampleSize
        );

        if (sampleShowtimes.size() == 0 && seatQuantity > 1) {

            System.out.println("No showtimes found with" + seatQuantity + "reserved seats." + "Retrying with seat quantity of 1.");

            sampleShowtimes = getShowtimesByFilter(
                    (showtimeId, seats) -> {
                        Showtime showtime = fetchShowtimeData(showtimeId);
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
