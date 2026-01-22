package testcases.browsing;

import base.BaseTest;
import helpers.providers.ShowtimeSampleProvider;
import helpers.providers.MessagesProvider;
import model.ui.MovieDropdownFields;
import model.api.response.ShowtimeBooking;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.HomePage;
import reports.ExtentReportManager;

import static model.ui.MovieDropdownFields.MOVIE;

public class MovieDropdownsFilterTest extends BaseTest {

    HomePage homePage;

    @BeforeMethod(alwaysRun = true)
    public void setUpMethod() {
        homePage = new HomePage(getDriver());
        ExtentReportManager.info("Navigate to Home Page and wait for dropdowns to load");
        homePage.navigateToHomePage();
        homePage.showtimeFilterDropdowns.waitForDropdownsToLoad();
    }

    @Test(groups = {"integration", "browsing", "dropdowns", "smoke"})
    public void testSuccessfulRedirectWithValidFilters() throws Exception {

        ExtentReportManager.info("Get sample showtime filter options for testing");

        ShowtimeBooking showtimeWithSeats = ShowtimeSampleProvider.getShowtimeWithAvailableSeats(5, 1).get(0);

        String movieOption = showtimeWithSeats.getMovieName();
        String cinemaOption = showtimeWithSeats.getCinemaBranchName();
        String showtimeOption = showtimeWithSeats.getShowtimeId();

        ExtentReportManager.info("Apply showtime filters: Movie='" + movieOption + "', Cinema='" + cinemaOption + "'" +
                ", Showtime ID='" + showtimeOption + "'");
        homePage.showtimeFilterDropdowns.selectAllFiltersAndConfirm(
                movieOption,
                cinemaOption,
                showtimeOption
        );

        ExtentReportManager.info("Verify successful redirection to selected showtime page");
        Assert.assertTrue(homePage.isOnShowtimePage(showtimeOption),
                "Not redirected to selected showtime page after applying filters");
        ExtentReportManager.pass("Redirection verified successfully");
    }

    @DataProvider(name = "missingFilterScenarios")
    public Object[][] missingFilterScenarios() {
        MovieDropdownFields movieFilter = MOVIE;
        MovieDropdownFields cinemaFilter = MovieDropdownFields.CINEMA;
        MovieDropdownFields showtimeFilter = MovieDropdownFields.SHOWTIME;

        return new Object[][]{
                {movieFilter, MessagesProvider.getMissingFilterError(movieFilter)}, // Missing Movie
                {cinemaFilter, MessagesProvider.getMissingFilterError(cinemaFilter)}, // Missing Cinema
                {showtimeFilter, MessagesProvider.getMissingFilterError(showtimeFilter)}, // Missing Showtime
        };
    }
    @Test(dataProvider = "missingFilterScenarios", groups = {"component", "browsing", "dropdowns", "negative"})
    public void testMissingFilterTriggersAlert(MovieDropdownFields missingFilter, String expectedAlertText) throws Exception {

        ExtentReportManager.info("Get sample showtime filter options for testing");
        ShowtimeBooking randomShowtime = ShowtimeSampleProvider.getRandomShowtime();

        String movie = randomShowtime.getMovieName();
        String cinema = randomShowtime.getCinemaBranchName();

        ExtentReportManager.info("Testing error alert for missing filter: " + missingFilter);
        homePage.showtimeFilterDropdowns.triggerMissingFilterAlert(missingFilter, movie, cinema);
        verifyMissingFilterAlert(expectedAlertText);
    }

    // ---- Helper methods for verification ----
    private void verifyMissingFilterAlert(String expectedAlertText) {
        // Use hard assertions here (TestNG Assert) because:
        // 1. Only checking 2 things (alert visible + text correct)
        // 2. If alert isn't visible, checking text is meaningless
        // 3. Test should fail fast if alert doesn't appear
        Assert.assertTrue(
                homePage.showtimeFilterDropdowns.isMissingFilterAlertVisible(),
                "Missing filter alert is not displayed"
        );

        String actualAlertText = homePage.showtimeFilterDropdowns.getMissingFilterAlertText();
        Assert.assertEquals(actualAlertText, expectedAlertText,
                "Missing filter alert text does not match expected text. Actual: " + actualAlertText +
                        ", Expected: " + expectedAlertText);

        ExtentReportManager.pass("Missing filter alert verified successfully");
    }
}