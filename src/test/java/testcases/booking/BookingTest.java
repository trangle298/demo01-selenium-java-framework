package testcases.booking;

import base.BaseTest;
import helpers.actions.BookingActionHelper;
import helpers.verifications.BookingVerificationHelper;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.BookingPage;
import reports.ExtentReportManager;

import java.util.List;

import static helpers.actions.BookingActionHelper.*;

public class BookingTest extends BaseTest {

    private BookingPage bookingPage;
    private SoftAssert softAssert;

    @BeforeMethod
    public void setUpMethod() {
        bookingPage = new BookingPage(getDriver());
        softAssert = new SoftAssert();
    }

    @Test(groups = {"integration", "booking", "smoke", "critical"})
    public void testValidBookingLoggedinUser() throws Exception {
        // Login
        ExtentReportManager.info("Login as test user for booking test");
        LoginPage loginPage = new LoginPage(getDriver());
        loginAsBookingUser(loginPage);

        // Navigate to showtime with available seats and book seats
        ExtentReportManager.info("Navigate to sample showtime page");
        navigateToSampleShowtimePageWithAvailability(bookingPage);

        // Select seats and confirm booking - return list of selected seat numbers for verification
        ExtentReportManager.info("Select sample available seats and confirm booking");
        List<String> seatsToBook = BookingActionHelper.selectSampleSeatsAndBook(bookingPage);

        // Verify booking success - success alert displayed with correct message, seats no longer available after refresh
        ExtentReportManager.info("Verify booking success");
        BookingVerificationHelper.verifyBookingSuccess(bookingPage, seatsToBook, getDriver(), softAssert);

        softAssert.assertAll();
    }

    @Test(groups = {"component", "booking", "negative"})
    public void testInvalidBooking_NoSeatSelected() throws Exception {
        // Login
        ExtentReportManager.info("Login as test user for booking test");
        LoginPage loginPage = new LoginPage(getDriver());
        loginAsBookingUser(loginPage);

        // Navigate to showtime with available seats and attempt booking without selecting seats
        ExtentReportManager.info("Navigate to sample showtime page");
        navigateToSampleShowtimePageWithAvailability(bookingPage);

        ExtentReportManager.info("Attempt to book without selecting any seats");
        bookingPage.clickBookTicketsButton();

        // Verify booking failure due to no seat selection - error alert displayed with correct message
        ExtentReportManager.info("Verify booking failure due to no seat selection");
        BookingVerificationHelper.verifyNoSeatSelectedDialog(bookingPage, getDriver(), softAssert);
    }

    @Test(groups = {"component", "booking", "negative"})
    public void testInvalidBooking_Unauthenticated() throws Exception {
        // Navigate to showtime with available seats without logging in
        ExtentReportManager.info("Navigate to sample showtime page without logging in");
        navigateToSampleShowtimePageWithAvailability(bookingPage);

        // Select seats and attempt booking - return list of selected seat numbers for verification
        ExtentReportManager.info("Select sample available seats and confirm booking");
        List<String> seatsToBook = BookingActionHelper.selectSampleSeatsAndBook(bookingPage);

        // Verify booking blocked for guest user - login required alert displayed with correct message, seats remain available after refresh
        ExtentReportManager.info("Verify booking is blocked for guest user");
        BookingVerificationHelper.verifyBookingBlockedForGuest(bookingPage, seatsToBook, getDriver(), softAssert);

        softAssert.assertAll();
    }
}