package api.cinema;

import model.Cinema;

import java.util.ArrayList;
import java.util.List;

/**
 * Extracts and transforms cinema data from API responses.
 * Provides methods to get cinema IDs and related information.
 */

// To be used in upcoming test cases
public class CinemaDataExtractor {

    public static List<String> extractCinemaIds(String jsonResponse) throws Exception {
        List<String> cinemaIds = new ArrayList<>();
        List<Cinema> cinemas = CinemaApiClient.fetchCinemasList();

        for (Cinema cinema : cinemas) {
            cinemaIds.add(cinema.getMaHeThongRap());
        }
        return cinemaIds;
    }
}
