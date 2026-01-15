package testcases.booking;

import base.BaseTest;
import helpers.BookingSamplesProvider;
import helpers.Messages;
import helpers.TestUserProvider;
import model.Showtime;
import model.TestUser;
import model.TestUserType;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.ShowtimePage;
import reports.ExtentReportManager;

import java.util.List;

public class BookingTest extends BaseTest {

    ShowtimePage showtimePage;

    @Test(groups = {"integration", "booking", "smoke"})
    public void testValidBookingLoggedinUser() throws Exception {
        SoftAssert softAssert = new SoftAssert();

        ExtentReportManager.info("Login as test user for booking test");
        loginAsBookingTestUser();

        ExtentReportManager.info("Navigate to sample showtime page");
        navigateToSampleShowtime();

        ExtentReportManager.info("Select sample available seats and confirm booking");
        List<String> availableSeats = showtimePage.getAvailableSeatNumbers();
        List<String> seatsToBook = BookingSamplesProvider.getSampleSeats(availableSeats, 2);

        showtimePage.selectAvailableSeats(seatsToBook);
        showtimePage.clickBookTicketsButton();

        ExtentReportManager.info("Verify booking success");
        verifyBookingSuccess(seatsToBook, softAssert);
    }

    @Test(groups = {"component", "booking", "negative"})
    public void testInvalidBooking_NoSeatSelected() throws Exception {
        ExtentReportManager.info("Login as test user for booking test");
        loginAsBookingTestUser();

        ExtentReportManager.info("Navigate to sample showtime page");
        navigateToSampleShowtime();

        ExtentReportManager.info("Attempt to book without selecting any seats");
        showtimePage.clickBookTicketsButton();

        ExtentReportManager.info("Verify booking failure due to no seat selection");

        String expectedMsg = Messages.getNoSeatSelectedError();
        String actualMsg = showtimePage.getBookingAlertText();
        Assert.assertEquals(actualMsg, expectedMsg,
                "Alert text for empty seat selection error is incorrect. Actual = " + actualMsg + " . Expected = " + expectedMsg );
        ExtentReportManager.pass("Booking error alert is displayed as expected");
    }

    @Test(groups = {"component", "booking", "negative"})
    public void testUnauthenticatedBooking() throws Exception {
        SoftAssert softAssert = new SoftAssert();

        ExtentReportManager.info("Navigate to sample showtime page without logging in");
        navigateToSampleShowtime();

        ExtentReportManager.info("Select sample available seats and confirm booking");
        List<String> availableSeats = showtimePage.getAvailableSeatNumbers();
        List<String> seatsToBook = BookingSamplesProvider.getSampleSeats(availableSeats, 2);

        showtimePage.selectAvailableSeats(seatsToBook);
        showtimePage.clickBookTicketsButton();

        ExtentReportManager.info("Verify booking is blocked for guest user");
        verifyBookingBlockedForGuest(seatsToBook, softAssert);

        softAssert.assertAll();
    }

    //--------------------------
    // Reusable steps and verifications
    //--------------------------
    public void loginAsBookingTestUser() {
        LoginPage loginPage = new LoginPage(getDriver());
        TestUser testUser = TestUserProvider.getUser(TestUserType.bookingUser);

        loginPage.navigateToLoginPage();
        loginPage.fillLoginFormAndSubmit(testUser.getUsername(), testUser.getPassword());
        loginPage.topBarNavigation.waitForUserProfileLink();
    }

    public void navigateToSampleShowtime() throws Exception {
        String showtimeId = getSampleShowtimeId();
        showtimePage = new ShowtimePage(getDriver());
        showtimePage.navigateToShowtimePage(showtimeId);
    }

    public String getSampleShowtimeId() throws Exception {
        // Find a random showtime with at least 5 available seats and return its ID
        Showtime showtime = BookingSamplesProvider.getShowtimesWithAvailableSeats(5, 1).getFirst();
        return showtime.getThongTinPhim().getMaLichChieu().toString();
    }

    public void verifyBookingSuccess(List<String> bookedSeats, SoftAssert softAssert) {
        // Verify success by checking alert message and seat availability, current website did not implement payment flow
        String expectedMsg = Messages.getBookingSuccessMessage();
        String actualMsg = showtimePage.getBookingAlertText();
        verifySoftEquals(actualMsg, expectedMsg, "Booking success message text", softAssert);

        showtimePage.refreshPage();
        verifySoftFalse(showtimePage.areSeatsAvailable(bookedSeats),
                "Booked seats are no longer available", softAssert);
    }

    private void verifyBookingBlockedForGuest(List<String> selectedSeats, SoftAssert softAssert) {
        String expectedMsg = Messages.getUnauthenticatedBookingError();
        String actualMsg = showtimePage.getBookingAlertText();
        verifySoftEquals(actualMsg, expectedMsg, "Unauthenticated booking error text", softAssert);

        showtimePage.refreshPage();
        verifySoftFalse(showtimePage.areSeatsAvailable(selectedSeats),
                "Verify selected seats are still available after failed booking attempt by guest",
                softAssert);
    }

}
