package testcases.e2e;

import base.BaseTest;
import helpers.actions.AuthActionHelper;
import helpers.providers.BookingSampleProvider;
import helpers.verifications.BookingVerificationHelper;
import helpers.verifications.SoftAssertionHelper;
import model.UserAccount;
import model.api.response.ShowtimeBooking;
import model.ui.OrderEntry;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.AccountPage;
import pages.BookingPage;
import pages.HomePage;
import pages.LoginPage;
import reports.ExtentReportManager;

import java.util.List;

import static helpers.actions.BookingActionHelper.bookSeatsAndCollectOrderDetails;
import static helpers.providers.UserAccountTestDataGenerator.generateRegisterRequestPayload;
import static helpers.verifications.SoftAssertionHelper.verifySoftTrue;

/**
 * E2E Test: Complete Booking Flow
 * Tests the entire user journey: Login → Browse → Select Showtime → Book Tickets → Verify Booking
 */
public class TC49_BookingAsLoggedinUserE2eTest extends BaseTest {

    @Test(groups = "requiresUser")
    public void testCompleteLoggedinUserBookingFlow() throws Exception {
        ExtentReportManager.info("Testing User Booking Flow: Login → Browse → Select Showtime → Book → Verify");

        SoftAssert softAssert = new SoftAssert();
        LoginPage loginPage = new LoginPage(getDriver());

        // ============================================
        // Step 1: Login and navigate to Homepage if not redirected
        // ============================================
        UserAccount testUser = getTestUser();
        AuthActionHelper.login(loginPage, testUser);

        HomePage homePage  = new HomePage(getDriver());
        boolean homepageRedirected = loginPage.isRedirectedToHomepage();
        if (!homepageRedirected) {
            LOG.warn("User not redirected to homepage after login - navigating manually");
            homePage.navigateToHomePage();
        }

        // ============================================
        // Step 2: Apply filters and click Find Ticket button to navigate to booking page
        // ============================================
        ExtentReportManager.info("Use filters to navigate to a showtime booking page");
        homePage.showtimeFilterDropdowns.waitForDropdownsToLoad();

        // Find a sample showtime with available seats (from API) to test
        ShowtimeBooking selectedShowtime = BookingSampleProvider.getShowtimeWithAvailableSeats();
        String showtimeId = selectedShowtime.getShowtimeId();

        homePage.showtimeFilterDropdowns.selectAllFiltersAndConfirm(
                selectedShowtime.getMovieName(),
                selectedShowtime.getCinemaBranchName(),
                showtimeId
        );

        // Verify user is nagivated to correct showtime booking page
        boolean isNavigated = homePage.isRedirectedToBookingPage(showtimeId);
        verifySoftTrue(isNavigated, "User should be navigated to the booking page for showtime: " + showtimeId, getDriver(), softAssert);

        // ============================================
        // Step 3: Verify booking page displays correct showtime details
        // ============================================
        BookingPage bookingPage = new BookingPage(getDriver());
        ExtentReportManager.info("Verify booking page displays correct showtime details");
        BookingVerificationHelper.verifyBookingPageDisplaysCorrectDetails(bookingPage, selectedShowtime, getDriver(), softAssert);

        // ============================================
        // Step 4: Book seats and verify success confirmation on UI
        // ============================================
        ExtentReportManager.info("Select seats and confirm booking");
        List<String> seatsToBook = BookingSampleProvider.getSampleAvailableSeats(bookingPage);
        // Book seats and collect order details for verificatiob
        OrderEntry bookingDetails = bookSeatsAndCollectOrderDetails(bookingPage, seatsToBook);

        // Verify booking success - success alert displayed with correct message, seats no longer available after refresh
        ExtentReportManager.info("Verify booking success");
        BookingVerificationHelper.verifyBookingSuccess(bookingPage, seatsToBook, getDriver(), softAssert);

        // ============================================
        // Step 5: Navigate to Account Page and verify booking appears in order history with correct details
        // ============================================
        AccountPage accountPage = new AccountPage(getDriver());
        accountPage.navigateToAccountPage();

        ExtentReportManager.info("Verify order history displays new order entry with correct details");
        BookingVerificationHelper.verifyEntryDetailsInOrderHistory(accountPage, bookingDetails, getDriver(), softAssert);

        softAssert.assertAll();
    }
}