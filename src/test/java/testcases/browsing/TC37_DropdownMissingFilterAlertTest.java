package testcases.browsing;

import base.BaseTest;
import helpers.providers.BookingSampleProvider;
import helpers.providers.MessagesProvider;
import model.enums.MovieDropdownField;
import model.api.response.ShowtimeBooking;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.HomePage;
import reports.ExtentReportManager;

import static model.enums.MovieDropdownField.MOVIE;

public class TC37_DropdownMissingFilterAlertTest extends BaseTest {

    @DataProvider(name = "missingFilterScenarios")
    public Object[][] missingFilterScenarios() {
        MovieDropdownField movieFilter = MOVIE;
        MovieDropdownField cinemaFilter = MovieDropdownField.CINEMA;
        MovieDropdownField showtimeFilter = MovieDropdownField.SHOWTIME;

        return new Object[][]{
                {movieFilter, MessagesProvider.getMissingFilterError(movieFilter)}, // Missing Movie
                {cinemaFilter, MessagesProvider.getMissingFilterError(cinemaFilter)}, // Missing Cinema
                {showtimeFilter, MessagesProvider.getMissingFilterError(showtimeFilter)}, // Missing Showtime
        };
    }

    @Test(dataProvider = "missingFilterScenarios")
    public void testMissingFilterTriggersAlert(MovieDropdownField missingFilter, String expectedAlertText) throws Exception {
        // Navigate to Homepage and wait for dropdowns to load
        ExtentReportManager.info("Navigate to Home Page and wait for dropdowns to load");
        HomePage homePage = new HomePage(getDriver());
        homePage.navigateToHomePage();
        homePage.showtimeFilterDropdowns.waitForDropdownsToLoad();

        // Find a random showtime and get its details: movie name, cinema branch to apply in test
        ExtentReportManager.info("Get sample showtime filter options for testing");
        ShowtimeBooking randomShowtime = BookingSampleProvider.getRandomShowtime();

        String movie = randomShowtime.getMovieName();
        String cinema = randomShowtime.getCinemaBranchName();

        // Trigger missing filter alert by clicking Find Ticket button with one or more missing filters
        ExtentReportManager.info("Testing error alert for missing filter: " + missingFilter);
        triggerMissingFilterAlert(homePage, missingFilter, movie, cinema);

        // Verify alert is displayed
        Assert.assertTrue(
                homePage.showtimeFilterDropdowns.isMissingFilterAlertVisible(),
                "Missing filter alert is not displayed"
        );

        // Verify alert has correct text
        String actualAlertText = homePage.showtimeFilterDropdowns.getMissingFilterAlertText();
        Assert.assertEquals(actualAlertText, expectedAlertText,
                "Missing filter alert text does not match expected text. Actual: " + actualAlertText +
                        ", Expected: " + expectedAlertText);
    }

    // ---- Private Helper ---

    /**
     * Trigger missing filter alert by selecting filters before the specified missing one.
     * Uses ordinal-based logic: movie (0) &lt; cinema (1) &lt; showtime (2).
     *
     * @param missingFilter The filter that should be missing (not selected)
     * @param movieTitle Movie title to select (if needed)
     * @param cinemaLocation Cinema location to select (if needed)
     */
    private static void triggerMissingFilterAlert(HomePage homePage, MovieDropdownField missingFilter, String movieTitle, String cinemaLocation) {
        switch (missingFilter) {
            case MOVIE:
                homePage.showtimeFilterDropdowns.clickApplyFilterBtn();
                break;
            case CINEMA:
                homePage.showtimeFilterDropdowns.selectMovieByMovieTitle(movieTitle);
                homePage.showtimeFilterDropdowns.clickApplyFilterBtn();
                break;
            case SHOWTIME:
                homePage.showtimeFilterDropdowns.selectMovieByMovieTitle(movieTitle);
                homePage.showtimeFilterDropdowns.selectCinemaBranchByName(cinemaLocation);
                homePage.showtimeFilterDropdowns.clickApplyFilterBtn();
                break;
            default:
                throw new IllegalArgumentException("Unknown missing filter: " + missingFilter);
        }
    }
}