package api.showtime;

import model.Showtime;

import java.util.List;

/**
 * Extracts and transforms showtime data from API responses.
 * Provides methods to get seat availability, movie details, and cinema information.
 */
public class ShowtimeDataExtractor {

    /**
     * Extracts the showtime ID from a Showtime object.
     *
     * @param showtime The showtime object
     * @return The showtime ID as a String
     */
    public static String getShowtimeId(Showtime showtime) {
        return showtime.getThongTinPhim().getMaLichChieu().toString();
    }

    /**
     * Retrieves the movie title for a given showtime ID.
     *
     * @param showtimeId The showtime ID
     * @return The movie title as a String
     * @throws Exception If there is an error fetching the showtime data
     */
    public static String getMovieTitleOfShowtime(String showtimeId) throws Exception {
        Showtime showtime = ShowtimeApiClient.fetchShowtimeData(showtimeId);
        return showtime.getThongTinPhim().getTenPhim();
    }

    /**
     * Retrieves the cinema location for a given showtime ID.
     *
     * @param showtimeId The showtime ID
     * @return The cinema location as a String
     * @throws Exception If there is an error fetching the showtime data
     */
    public static String getCinemaLocationOfShowtime(String showtimeId) throws Exception {
        Showtime showtime = ShowtimeApiClient.fetchShowtimeData(showtimeId);
        return showtime.getThongTinPhim().getTenCumRap();
    }

    /**
     * Retrieves a list of available seat IDs for a given showtime ID.
     *
     * @param showtimeId The showtime ID
     * @return A list of available seat IDs as Strings
     * @throws Exception If there is an error fetching the showtime data
     */
    public static List<String> getAvailableSeats(String showtimeId) throws Exception {

        Showtime showtime = ShowtimeApiClient.fetchShowtimeData(showtimeId);

        List<String> availableSeats = showtime.getDanhSachGhe().stream()
                .filter(seat -> !seat.isDaDat())
                .map(seat -> seat.getMaGhe())
                .toList();

        return availableSeats;
    }

    /**
     * Retrieves a list of reserved seat IDs for a given showtime ID.
     *
     * @param showtimeId The showtime ID
     * @return A list of reserved seat IDs as Strings
     * @throws Exception If there is an error fetching the showtime data
     */
    public static List<String> getReservedSeats(String showtimeId) throws Exception {

        Showtime showtime = ShowtimeApiClient.fetchShowtimeData(showtimeId);

        List<String> reservedSeats = showtime.getDanhSachGhe().stream()
                .filter(seat -> seat.isDaDat())
                .map(seat -> seat.getMaGhe())
                .toList();

        return reservedSeats;
    }

}
