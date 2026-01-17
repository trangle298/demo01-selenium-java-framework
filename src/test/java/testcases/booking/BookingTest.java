package testcases.booking;

import base.BaseTest;
import helpers.BookingHelper;
import helpers.BookingSamplesProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.ShowtimePage;
import reports.ExtentReportManager;

import java.util.List;

import static helpers.BookingHelper.*;

public class BookingTest extends BaseTest {

    ShowtimePage showtimePage;

    @Test(groups = {"integration", "booking", "smoke", "critical"})
    public void testValidBookingLoggedinUser() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        LoginPage loginPage = new LoginPage(getDriver());
        showtimePage = new ShowtimePage(getDriver());

        ExtentReportManager.info("Login as test user for booking test");
        loginAsBookingUser(loginPage);

        ExtentReportManager.info("Navigate to sample showtime page");
        navigateToAvailableShowtimePage(showtimePage);

        ExtentReportManager.info("Select sample available seats and confirm booking");
        List<String> seatsToBook = selectAvailableSeats(showtimePage, 2);
        showtimePage.clickBookTicketsButton();

        ExtentReportManager.info("Verify booking success");
        BookingHelper.verifyBookingSuccess(showtimePage, seatsToBook, getDriver(), softAssert);
    }

    @Test(groups = {"component", "booking", "negative"})
    public void testInvalidBooking_NoSeatSelected() throws Exception {
        LoginPage loginPage = new LoginPage(getDriver());
        showtimePage = new ShowtimePage(getDriver());

        ExtentReportManager.info("Login as test user for booking test");
        loginAsBookingUser(loginPage);

        ExtentReportManager.info("Navigate to sample showtime page");
        navigateToAvailableShowtimePage(showtimePage);

        ExtentReportManager.info("Attempt to book without selecting any seats");
        showtimePage.clickBookTicketsButton();

        ExtentReportManager.info("Verify booking failure due to no seat selection");
        BookingHelper.verifyNoSeatSelectedError(showtimePage);
    }

    @Test(groups = {"component", "booking", "negative"})
    public void testUnauthenticatedBooking() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        showtimePage = new ShowtimePage(getDriver());

        ExtentReportManager.info("Navigate to sample showtime page without logging in");
        navigateToAvailableShowtimePage(showtimePage);

        ExtentReportManager.info("Select sample available seats and confirm booking");
        List<String> availableSeats = showtimePage.getAvailableSeatNumbers();
        List<String> seatsToBook = BookingSamplesProvider.getSampleSeats(availableSeats, 2);

        showtimePage.selectAvailableSeats(seatsToBook);
        showtimePage.clickBookTicketsButton();

        ExtentReportManager.info("Verify booking is blocked for guest user");
        BookingHelper.verifyBookingBlockedForGuest(showtimePage, seatsToBook, getDriver(), softAssert);

        softAssert.assertAll();
    }

}
