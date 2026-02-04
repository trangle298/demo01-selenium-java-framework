package helpers.verifications;

import helpers.providers.MessagesProvider;
import model.api.response.ShowtimeBooking;
import model.ui.OrderEntry;
import model.ui.ShowtimeDetails;
import org.openqa.selenium.WebDriver;
import org.testng.asserts.SoftAssert;
import pages.AccountPage;
import pages.BookingPage;

import java.util.List;

import static helpers.verifications.SoftAssertionHelper.*;

/**
 * Helper class for booking-related verifications.
 * Handles verification of booking success, blocked bookings for unauthenticated users,
 * and error messages related to seat selection.
 *
 * <p>Uses soft assertions for multiple related checks and E2E test compatibility.
 */
public class BookingVerificationHelper {

    public static void verifyBookingPageDisplaysCorrectDetails(BookingPage bookingPage, ShowtimeBooking showtime, WebDriver driver, SoftAssert softAssert) {
        ShowtimeDetails displayedDetails = bookingPage.getShowtimeDetailsFromSummary();
        ShowtimeDetails expectedDetails = new ShowtimeDetails();

        expectedDetails.setCinemaBranchName(showtime.getCinemaBranchName())
                .setCinemaAddress(showtime.getCinemaAddress())
                .setTheaterName(showtime.getTheaterName())
                .setMovieName(showtime.getMovieName())
                .setShowtimeDateTime(showtime.getShowingDateTimeNormalized());

        verifySoftEquals(displayedDetails, expectedDetails,
                "Showtime details displayed on booking page", driver, softAssert);
    }

    /**
     * Verify booking success - dialog displayed with correct text, seats no longer available after booking.
     * Uses SoftAssertionHelper to automatically capture screenshots on each failed soft assertion.
     * NOTE: Current website does not implement a payment step, so booking is confirmed immediately after seat selection.
     *
     * @param bookingPage The ShowtimePage instance
     * @param selectedSeats List of seat numbers that were booked
     * @param driver WebDriver instance (needed for screenshot capture)
     * @param softAssert The SoftAssert instance for accumulating assertions
     */
    public static void verifyBookingSuccess(BookingPage bookingPage, List<String> selectedSeats, WebDriver driver, SoftAssert softAssert) throws InterruptedException {
        boolean isDialogDisplayed = bookingPage.isBookingDialogDisplayed();
        verifySoftTrue(isDialogDisplayed,
                "Booking dialog is displayed", driver, softAssert);

        if (isDialogDisplayed) {
            String expectedMsg = MessagesProvider.getBookingSuccessMessage();
            String actualMsg = bookingPage.getBookingDialogHeader();
            verifySoftEquals(actualMsg, expectedMsg, "Dialog text for successful booking", driver, softAssert);

            // Close the alert to proceed
            bookingPage.confirmAndCloseDialog();
        }

        // Refresh page to ensure seat map is updated before verifying seats are no longer available
        bookingPage.refreshPage();
//        bookingPage.waitForSeatMapToLoad();

        verifySoftFalse(bookingPage.areSeatsAvailable(selectedSeats),
                "Booked seats: " + selectedSeats + " are no longer available" , driver, softAssert);
    }

    /**
     * Verify booking blocked for guest user - login required dialog displayed, seats remains available after attempt.
     * Uses SoftAssertionHelper to automatically capture screenshots on each failed soft assertion.
     *
     * @param bookingPage The ShowtimePage instance
     * @param selectedSeats List of seat numbers that were selected
     * @param driver WebDriver instance (needed for screenshot capture)
     * @param softAssert The SoftAssert instance for accumulating assertions
     */
    public static void verifyBookingBlockedForGuest(BookingPage bookingPage, List<String> selectedSeats, WebDriver driver, SoftAssert softAssert) {
        boolean isDialogDisplayed = bookingPage.isBookingDialogDisplayed();
        verifySoftTrue(isDialogDisplayed,
                "Booking dialog is displayed", driver, softAssert);

        if (isDialogDisplayed) {
            String expectedMsg = MessagesProvider.getUnauthenticatedBookingError();
            String actualMsg = bookingPage.getBookingDialogHeader();
            verifySoftEquals(actualMsg, expectedMsg, "Dialog text for Login request for unauthenticated booking", driver, softAssert);

            // Close the alert to decline login redirect and proceed
            bookingPage.denyAndCloseDialog();
        }

        bookingPage.refreshPage();
        bookingPage.waitForSeatMapToLoad();

        verifySoftTrue(bookingPage.areSeatsAvailable(selectedSeats),
                "Selected seats are still available after failed booking attempt by guest",
                driver, softAssert);
    }

    /**
     * Verify dialog with empty seat selection error message is displayed.
     * Uses hard assertion since this is typically the main assertion of the test.
     *
     * @param bookingPage The ShowtimePage instance
     */
    public static void verifyNoSeatSelectedDialog(BookingPage bookingPage, WebDriver driver, SoftAssert softAssert) {

        boolean isDialogDisplayed = bookingPage.isBookingDialogDisplayed();
        verifySoftTrue(isDialogDisplayed,
                "Booking dialog is displayed", driver, softAssert);

        if (isDialogDisplayed) {
            String expectedMsg = MessagesProvider.getNoSeatSelectedError();
            String actualMsg = bookingPage.getBookingDialogHeader();
            verifySoftEquals(actualMsg, expectedMsg, "Dialog text for Empty Seat selection error", driver, softAssert);

            bookingPage.confirmAndCloseDialog();
        }
    }

    public static void verifyEntryDetailsInOrderHistory(AccountPage accountPage, OrderEntry expectedEntry, WebDriver driver, SoftAssert softAssert) {
        OrderEntry displayedEntry = accountPage.getLastOrderEntryDetails();

        verifySoftEquals(displayedEntry.getMovieName(), expectedEntry.getMovieName(),
                "Movie name in Order History", driver, softAssert);

        verifySoftEquals(displayedEntry.getCinemaBranchName(), expectedEntry.getCinemaBranchName(),
                "Cinema branch name in Order History", driver, softAssert);

        verifySoftEquals(displayedEntry.getTheaterName(), expectedEntry.getTheaterName(),
                "Theater name in Order History", driver, softAssert);

        verifySoftEquals(displayedEntry.getSeatNumbers(), expectedEntry.getSeatNumbers(),
                "Seat number in Order History", driver, softAssert);

        verifySoftEquals(displayedEntry.getPrice(), expectedEntry.getPrice(),
                "Price in Order History", driver, softAssert);
    }

}