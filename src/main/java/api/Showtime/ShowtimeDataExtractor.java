package api.Showtime;

import model.Showtime;

import java.util.List;

public class ShowtimeDataExtractor {

    public static String getMovieTitleOfShowtime(String showtimeId) throws Exception {
        Showtime showtime = ShowtimeApiClient.fetchShowtimeData(showtimeId);
        return showtime.getThongTinPhim().getTenPhim();
    }

    public static String getCinemaLocationOfShowtime(String showtimeId) throws Exception {
        Showtime showtime = ShowtimeApiClient.fetchShowtimeData(showtimeId);
        return showtime.getThongTinPhim().getTenCumRap();
    }


    public static List<String> getAvailableSeats(String showtimeId) throws Exception {

        Showtime showtime = ShowtimeApiClient.fetchShowtimeData(showtimeId);

        List<String> availableSeats = showtime.getDanhSachGhe().stream()
                .filter(seat -> !seat.isDaDat())
                .map(seat -> seat.getMaGhe())
                .toList();

        return availableSeats;
    }

    public static List<String> getReservedSeats(String showtimeId) throws Exception {

        Showtime showtime = ShowtimeApiClient.fetchShowtimeData(showtimeId);

        List<String> reservedSeats = showtime.getDanhSachGhe().stream()
                .filter(seat -> seat.isDaDat())
                .map(seat -> seat.getMaGhe())
                .toList();

        return reservedSeats;
    }

}
