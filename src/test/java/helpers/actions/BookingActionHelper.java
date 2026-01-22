package helpers.actions;

import helpers.providers.ShowtimeSampleProvider;
import helpers.providers.RandomSampleProvider;
import helpers.providers.TestUserProvider;
import model.api.response.ShowtimeBooking;
import model.TestUser;
import model.TestUserType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pages.LoginPage;
import pages.BookingPage;
import reports.ExtentReportManager;

import java.util.List;

/**
 * Helper class for common booking-related actions.
 * Provides reusable methods for booking flows used across component and E2E tests.
 */
public class BookingActionHelper {

    private static final Logger LOG = LogManager.getLogger(BookingActionHelper.class);

    public static void loginAsBookingUser(LoginPage loginPage) {
        TestUser testUser = TestUserProvider.getUser(TestUserType.USER_BOOKING);
        LOG.info("Logging in as booking test user: " + testUser.getUsername());

        loginPage.navigateToLoginPage();
        loginPage.fillLoginFormThenSubmit(testUser.getUsername(), testUser.getPassword());
        loginPage.topBarNavigation.waitForUserProfileLink();
    }

    /**
     * Navigates to a random showtime page that has available seats (at least 5 seats available if not specified).
     *
     * @param bookingPage The ShowtimePage instance
     * @throws Exception if no showtimes with available seats are found
     */
    public static void navigateToSampleShowtimePageWithAvailability(BookingPage bookingPage) throws Exception {
        ShowtimeBooking showtime = ShowtimeSampleProvider.getShowtimeWithAvailableSeats();
        String showtimeId = showtime.getShowtimeId();

        LOG.info("Navigating to showtime page with ID: " + showtimeId);
        bookingPage.navigateToShowtimePage(showtimeId);
    }

    /**
     * Selects a specified number of available seats on the showtime page and books them.
     * Seats are selected randomly from the available seats.
     * Fallback to all available seats if sampleSize exceeds availability.
     *
     * @param bookingPage The ShowtimePage instance
     * @param sampleSize Number of seats to select and book
     */
    public static void selectSampleSeatsAndBook(BookingPage bookingPage, int sampleSize) {
        List<String> availableSeats = bookingPage.getAvailableSeatNumbers();
        List<String> seatsToBook = RandomSampleProvider.getRandomSamplesFromList(availableSeats, sampleSize);

        ExtentReportManager.info("Selecting " + seatsToBook.size() + " seats to book: " + seatsToBook);
        bookingPage.selectSeatsBySeatNumbers(seatsToBook);
        bookingPage.clickBookTicketsButton();
    }

    /**
     * Selects between 1 and 5 available seats on the showtime page by default and books them.
     * (When no fixed sample size is specified)
     *
     * @param bookingPage The ShowtimePage instance
     */
    public static List<String> selectSampleSeatsAndBook(BookingPage bookingPage) {
        List<String> availableSeats = bookingPage.getAvailableSeatNumbers();
        List<String> seatsToBook = RandomSampleProvider.getRandomSamplesFromList(availableSeats, 1, 5);

        ExtentReportManager.info("Selecting of " + seatsToBook.size() + " seats to book: " + seatsToBook);
        bookingPage.selectSeatsBySeatNumbers(seatsToBook);
        bookingPage.clickBookTicketsButton();
        return seatsToBook;
    }
}
