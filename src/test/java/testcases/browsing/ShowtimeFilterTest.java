package testcases.browsing;

import api.Movie.MovieDataExtractor;
import base.BaseTest;
import helpers.Messages;
import model.FilterDropdownOptions;
import model.FilterType;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.HomePage;
import reports.ExtentReportManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static helpers.ShowtimeFilterSamplesProvider.getSampleShowtimeFilterOptions;
import static model.FilterType.cinema;
import static model.FilterType.movie;

public class ShowtimeFilterTest extends BaseTest {

    HomePage homePage;

    @BeforeMethod(alwaysRun = true)
    public void setUpMethod() {
        homePage = new HomePage(getDriver());
        ExtentReportManager.info("Navigate to Home Page and wait for dropdowns to load");
        homePage.navigateToHomePage();
        homePage.showtimeFilterDropdowns.waitForDropdownsToLoad();
    }

    @Test
    public void testApiDataConsistency_MovieDropdown() throws Exception {
        ExtentReportManager.info("Fetch movie titles from API");
        List<String> moviesFromAPI = MovieDataExtractor.extractAllMovieTitles();

        if (moviesFromAPI.isEmpty()) {
            throw new SkipException("Skipping test as no movies found in API to test movie dropdown options.");
        }

        ExtentReportManager.info("Fetch movie titles from UI dropdown");
        List<String> moviesFromUI = homePage.showtimeFilterDropdowns.getMovieOptionsText();

        ExtentReportManager.info("Verify movie dropdown options match API data");
        verifyApiAndUiDataConsistency(moviesFromAPI, moviesFromUI);
    }

    @Test
    public void testApiDataConsistency_CinemaDropdown() throws Exception {

        SoftAssert softAssert = new SoftAssert();

        ExtentReportManager.info("Fetch movie titles from UI dropdown");
        List<String> moviesFromUI = homePage.showtimeFilterDropdowns.getMovieOptionsText();

        if (moviesFromUI.isEmpty()) {
            throw new SkipException("Skipping test as no movies found in UI dropdown to test cinema dropdown options.");
        }

        for (String movie : moviesFromUI) {
            homePage.refreshPage();

            ExtentReportManager.info("Fetch cinema names from API for movie: " + movie);
            List<String> cinemasFromAPI = MovieDataExtractor.extractAllCinemaLocationsByMovieTitle(movie);

            if (cinemasFromAPI.isEmpty()) {
                ExtentReportManager.info("No cinemas found in API for movie: " + movie + ". Movie should be removed from UI dropdown options.");
                softAssert.fail("No cinemas found in API for movie: " + movie + ". Movie should be removed from UI dropdown options.");
                continue;
            }

            ExtentReportManager.info("Fetch cinema names from UI dropdown for movie: " + movie);
            homePage.showtimeFilterDropdowns.selectMovieByMovieTitle(movie);
            List<String> cinemasFromUI = homePage.showtimeFilterDropdowns.getCinemaLocationOptionsText();

            System.out.println(movie + " -> " + cinemasFromUI);

            ExtentReportManager.info("Verify cinema dropdown options match API data for movie: " + movie);
            try {
                verifyApiAndUiDataConsistency(cinemasFromAPI, cinemasFromUI);
            } catch (AssertionError e) {
                softAssert.fail("Cinema dropdown options do not match API data for movie: " + movie + ". ");
            }
        }
    }


    @Test(groups = {"component", "browsing", "showtimeFilters"})
    public void testShowtimeFilterDefaultSelection() {
        // Test implementation goes here
    }

    @Test(groups = {"integration", "browsing", "showtimeFilters", "smoke"})
    public void testValidShowtimeFilterSelection() throws Exception {

        ExtentReportManager.info("Get sample showtime filter options for testing");
        FilterDropdownOptions options = getSampleShowtimeFilterOptions();

        String movie = options.getMovieTitle();
        String cinema = options.getCinemaName();
        String showtimeId = options.getShowtimeId();

        ExtentReportManager.info("Apply showtime filters: Movie='" + movie + "', Cinema='" + cinema + "'" +
                ", Showtime ID='" + showtimeId + "'");
        ;
        homePage.showtimeFilterDropdowns.applyFiltersAndFindTickets(
                movie,
                cinema,
                showtimeId
        );

        ExtentReportManager.info("Verify successful redirection to selected showtime page");
        Assert.assertTrue(homePage.isRedirectedToShowtimePage(showtimeId),
                "Not redirected to selected showtime page after applying filters");
        ExtentReportManager.pass("Redirection verified successfully");
    }

    @DataProvider(name = "missingFilterScenarios")
    public Object[][] missingFilterScenarios() {
        FilterType movieFilter = movie;
        FilterType cinemaFilter = FilterType.cinema;
        FilterType showtimeFilter = FilterType.showtime;

        return new Object[][]{
                {movieFilter, Messages.getMissingFilterError(movieFilter)}, // Missing Movie
                {cinemaFilter, Messages.getMissingFilterError(cinemaFilter)}, // Missing Cinema
                {showtimeFilter, Messages.getMissingFilterError(showtimeFilter)}, // Missing Showtime
        };
    }

    @Test(dataProvider = "missingFilterScenarios", groups = {"component", "browsing", "showtimeFilters", "negative"})
    public void testMissingFilterAlert(FilterType missingFilter, String expectedAlertText) throws Exception {

        ExtentReportManager.info("Get sample showtime filter options for testing");
        FilterDropdownOptions options = getSampleShowtimeFilterOptions();

        String movie = options.getMovieTitle();
        String cinema = options.getCinemaName();

        ExtentReportManager.info("Testing error alert for missing filter: " + missingFilter);
        homePage.showtimeFilterDropdowns.triggerMissingFilterAlert(missingFilter, movie, cinema);
        verifyMissingFilterAlert(expectedAlertText);
    };

    // --------------------------
    // Helper methods for verification
    // --------------------------
    private void verifyMissingFilterAlert(String expectedAlertText) {
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

    private void verifyApiAndUiDataConsistency(List<String> APIdata, List<String> UIdata) {

        if (APIdata.size() != UIdata.size()) {
            String message = "Movie filter dropdown options count does not match API data. " +
                    "API count: " + APIdata.size() + ", UI count: " + UIdata.size();
            ExtentReportManager.fail(message);
            Assert.fail(message);
        }

        List<String> sortedAPI = new ArrayList<>(APIdata);
        List<String> sortedUI = new ArrayList<>(UIdata);
        Collections.sort(sortedAPI);
        Collections.sort(sortedUI);

        Assert.assertEquals(sortedAPI, sortedUI,
                "Movie filter dropdown options do not match API data. API = " + sortedAPI + ", UI = " + sortedUI);
        ExtentReportManager.pass("Movie filter dropdown options match API data");
    }
}
