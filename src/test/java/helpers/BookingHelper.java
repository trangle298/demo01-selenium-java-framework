package helpers;

import model.Showtime;
import model.TestUser;
import model.TestUserType;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.ShowtimePage;
import reports.ExtentReportManager;

import java.util.List;

import static helpers.SoftAssertionHelper.verifySoftEquals;
import static helpers.SoftAssertionHelper.verifySoftFalse;
import static helpers.SoftAssertionHelper.verifySoftTrue;

/**
 * Helper class for common booking-related actions and verifications.
 * Provides reusable methods for booking flows used across component and E2E tests.
 *
 * <p>Contains two main categories:
 * <ul>
 *   <li>Actions - Login, navigate to showtimes, select seats</li>
 *   <li>Verifications - Booking success, blocked bookings, error messages</li>
 * </ul>
 */
public class BookingHelper {

    // ---- Actions ----

    /**
     * Logs in using a predefined booking user.
     *
     * @param loginPage The LoginPage instance
     */
    public static void loginAsBookingUser(LoginPage loginPage) {
        TestUser testUser = TestUserProvider.getUser(TestUserType.bookingUser);

        loginPage.navigateToLoginPage();
        loginPage.fillLoginFormAndSubmit(testUser.getUsername(), testUser.getPassword());
        loginPage.topBarNavigation.waitForUserProfileLink();
    }

    /**
     * Navigates to a random showtime page that has available seats.
     *
     * @param showtimePage The ShowtimePage instance
     * @throws Exception if no showtimes with available seats are found
     */
    public static void navigateToAvailableShowtimePage(ShowtimePage showtimePage) throws Exception {
        Showtime showtime = BookingSamplesProvider.getShowtimesWithAvailableSeats(5, 1).getFirst();
        String showtimeId = api.showtime.ShowtimeDataExtractor.getShowtimeId(showtime);

        showtimePage.navigateToShowtimePage(showtimeId);
    }

    /**
     * Selects random available seats on the showtime page.
     *
     * @param showtimePage The ShowtimePage instance
     * @param numberOfSeats Number of seats to select
     * @return List of selected seat numbers
     */
    public static List<String> selectAvailableSeats(ShowtimePage showtimePage, int numberOfSeats) {
        List<String> availableSeats = showtimePage.getAvailableSeatNumbers();
        List<String> seatsToSelect = BookingSamplesProvider.getSampleSeats(availableSeats, numberOfSeats);

        showtimePage.selectAvailableSeats(seatsToSelect);
        return seatsToSelect;
    }

    // ---- Verifications ----
    /**
     * Verify booking success - message displayed, seats no longer available after booking.
     * Uses SoftAssertionHelper to automatically capture screenshots on each failed soft assertion.
     *
     * @param showtimePage The ShowtimePage instance
     * @param selectedSeats List of seat numbers that were booked
     * @param driver WebDriver instance (needed for screenshot capture)
     * @param softAssert The SoftAssert instance for accumulating assertions
     */
    public static void verifyBookingSuccess( ShowtimePage showtimePage, List<String> selectedSeats, WebDriver driver, SoftAssert softAssert) {
        // Verify success by checking alert message and seat availability, current website did not implement payment flow
        String expectedMsg = Messages.getBookingSuccessMessage();
        String actualMsg = showtimePage.getBookingAlertText();
        verifySoftEquals(actualMsg, expectedMsg, "Booking success message text", driver, softAssert);

        showtimePage.refreshPage();
        showtimePage.waitForPageToLoad();

        verifySoftFalse(showtimePage.areSeatsAvailable(selectedSeats),
                "Booked seats are no longer available", driver, softAssert);
    }

    /**
     * Verify booking blocked for guest user - login required message displayed, seats remains available after attempt.
     * Uses SoftAssertionHelper to automatically capture screenshots on each failed soft assertion.
     *
     * @param showtimePage The ShowtimePage instance
     * @param selectedSeats List of seat numbers that were selected
     * @param driver WebDriver instance (needed for screenshot capture)
     * @param softAssert The SoftAssert instance for accumulating assertions
     */
    public static void verifyBookingBlockedForGuest(ShowtimePage showtimePage, List<String> selectedSeats, WebDriver driver, SoftAssert softAssert) {
        String expectedMsg = Messages.getUnauthenticatedBookingError();
        String actualMsg = showtimePage.getBookingAlertText();
        verifySoftEquals(actualMsg, expectedMsg, "Unauthenticated booking error text", driver, softAssert);

        showtimePage.refreshPage();
        showtimePage.waitForPageToLoad();

        verifySoftTrue(showtimePage.areSeatsAvailable(selectedSeats),
                "Selected seats are still available after failed booking attempt by guest",
                driver, softAssert);
    }

    /**
     * Verify "no seat selected" error message is displayed.
     * Uses hard assertion since this is typically the main assertion of the test.
     *
     * @param showtimePage The ShowtimePage instance
     */
    public static void verifyNoSeatSelectedError(ShowtimePage showtimePage) {
        String expectedMsg = Messages.getNoSeatSelectedError();
        String actualMsg = showtimePage.getBookingAlertText();
        Assert.assertEquals(actualMsg, expectedMsg, "Booking error for no seat selection");
        ExtentReportManager.pass("No seat selection error displayed correctly: " + actualMsg);
    }
}
