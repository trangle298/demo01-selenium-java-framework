package testcases.booking;

import base.BaseTest;
import helpers.actions.AuthActionHelper;
import helpers.actions.BookingActionHelper;
import helpers.verifications.BookingVerificationHelper;
import model.UserAccount;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.BookingPage;
import pages.LoginPage;
import reports.ExtentReportManager;

public class TC34_EmptySeatSelectionAlertTest extends BaseTest {

    @Test(groups = "requiresUser")
    public void testBookingNoSeatSelectedError() throws Exception {

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

        // click book ticket without choosing seats
        ExtentReportManager.info("Click button Book Ticket without choosing any seats");
        bookingPage.clickBookTicketsButton();

        // Verify error alert for empty seat selection
        ExtentReportManager.info("Verify empty seat selection alert displays with correct text");
        BookingVerificationHelper.verifyNoSeatSelectedDialog(bookingPage, getDriver(), softAssert);

        softAssert.assertAll();
    }
}