package api.Cinema;

import model.Cinema;

import java.util.ArrayList;
import java.util.List;

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
