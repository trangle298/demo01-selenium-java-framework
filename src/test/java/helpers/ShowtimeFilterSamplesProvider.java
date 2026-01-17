package helpers;

import model.FilterDropdownOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static api.movie.MovieDataExtractor.extractAllMovieShowtimeIds;
import static api.showtime.ShowtimeDataExtractor.getCinemaLocationOfShowtime;
import static api.showtime.ShowtimeDataExtractor.getMovieTitleOfShowtime;

/**
 * Provides sample filter dropdown options for showtime filtering tests.
 * Fetches real data from API to get valid movie, cinema, and showtime combinations.
 *
 * <p>Used for testing chained dropdown behavior on the homepage.
 */
public class ShowtimeFilterSamplesProvider {

    private static final Logger LOG = LogManager.getLogger(ShowtimeFilterSamplesProvider.class);

    public static FilterDropdownOptions getSampleShowtimeFilterOptions() throws Exception {

        List<String> allShowtimeIds = extractAllMovieShowtimeIds();
        if (allShowtimeIds.isEmpty()) {
            throw new Exception("No showtime IDs found from Movie domain API.");
        }

        String sampleShowtimeId = pickRandomFromList(allShowtimeIds);
        String cinemaName = getCinemaLocationOfShowtime(sampleShowtimeId);
        String movieTitle = getMovieTitleOfShowtime(sampleShowtimeId);

        LOG.info("Selected sample showtime ID: " + sampleShowtimeId +
                ", Movie Title: " + movieTitle +
                ", Cinema Name: " + cinemaName);
        return new FilterDropdownOptions(movieTitle, cinemaName, sampleShowtimeId);
    }

    private static String pickRandomFromList(List<String> list) {
        int randomIndex = (int) (Math.random() * list.size());
        return list.get(randomIndex);
    }

}
