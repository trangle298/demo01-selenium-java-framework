package helpers;

import model.FilterDropdownOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static api.Movie.MovieDataExtractor.extractAllMovieShowtimeIds;
import static api.Showtime.ShowtimeDataExtractor.getCinemaLocationOfShowtime;
import static api.Showtime.ShowtimeDataExtractor.getMovieTitleOfShowtime;
import static helpers.SharedHelpers.pickRandomFromList;

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

        return new FilterDropdownOptions(movieTitle, cinemaName, sampleShowtimeId);
    }

}
