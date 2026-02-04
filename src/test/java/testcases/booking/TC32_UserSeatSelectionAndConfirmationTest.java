package testcases.booking;

import base.BaseTest;
import helpers.actions.AuthActionHelper;
import helpers.actions.BookingActionHelper;
import helpers.providers.BookingSampleProvider;
import helpers.verifications.BookingVerificationHelper;
import model.UserAccount;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.BookingPage;
import pages.LoginPage;
import reports.ExtentReportManager;

import java.util.List;

public class TC32_UserSeatSelectionAndConfirmationTest extends BaseTest {

    @Test(groups = "requiresUser")
    public void testValidBookingLoggedinUser() throws Exception {

        SoftAssert softAssert = new SoftAssert();
        LoginPage loginPage = new LoginPage(getDriver());
        BookingPage bookingPage = new BookingPage(getDriver());

        // Login
        ExtentReportManager.info("Login");
        UserAccount testUser = getTestUser();
        AuthActionHelper.login(loginPage, testUser);

        // Find a random showtime with available seats (from API data) and navigate to its booking page
        ExtentReportManager.info("Navigate to showtime booking page");
        BookingActionHelper.navigateToSampleShowtimePageWithAvailability(bookingPage);

        // Find random sample of available seats to book (randomly between 1 and 5 seats if not specified)
        ExtentReportManager.info("Select seats and confirm booking");
        List<String> seatsToBook = BookingSampleProvider.getSampleAvailableSeats(bookingPage);

        bookingPage.selectSeatsBySeatNumbers(seatsToBook);
        bookingPage.clickBookTicketsButton();

        // Verify booking success - success alert displayed with correct message, seats no longer available after refresh
        ExtentReportManager.info("Verify booking success");
        BookingVerificationHelper.verifyBookingSuccess(bookingPage, seatsToBook, getDriver(), softAssert);

        softAssert.assertAll();
    }
}