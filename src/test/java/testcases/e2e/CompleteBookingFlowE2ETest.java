package testcases.e2e;

import api.services.UserService;
import base.BaseTest;
import helpers.actions.BookingActionHelper;
import helpers.providers.ShowtimeSampleProvider;
import helpers.verifications.BookingVerificationHelper;
import model.ui.LoginInputs;
import model.api.request.RegisterRequest;
import model.api.response.ShowtimeBooking;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.HomePage;
import pages.LoginPage;
import pages.BookingPage;
import reports.ExtentReportManager;

import java.util.List;

import static helpers.providers.AuthTestDataGenerator.generateRegisterRequestPayload;
import static helpers.verifications.AuthVerificationHelper.verifyLoginSuccess;
import static helpers.verifications.SoftAssertionHelper.verifySoftTrue;

/**
 * E2E Test: Complete Booking Flow
 * Tests the entire user journey: Login → Browse → Select Showtime → Book Tickets → Verify Booking
 */
public class CompleteBookingFlowE2ETest extends BaseTest {

    private LoginPage loginPage;
    private HomePage homePage;
    private BookingPage bookingPage;
    private LoginInputs loginCredentials;

    @BeforeMethod
    public void setupMethod() {
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
        bookingPage = new BookingPage(getDriver());

        // Create a new user for booking and collect login credentials
        RegisterRequest registerRequest = generateRegisterRequestPayload();
        UserService userService = new UserService();
        userService.sendRegisterRequest(registerRequest);
        loginCredentials = new LoginInputs(registerRequest.getTaiKhoan(), registerRequest.getMatKhau());
    }

    @Test(groups = {"e2e", "booking", "critical"})
    public void testCompleteBookingFlow() throws Exception {
        SoftAssert softAssert = new SoftAssert();

        // ============================================
        // Step 1: User logs in (via UI - not API due to missing auth token)
        // ============================================
        ExtentReportManager.info("Navigate to login page and log in");
        loginPage.navigateToLoginPage();
        loginPage.fillLoginFormThenSubmit(loginCredentials);

//        loginPage.fillLoginFormAndSubmit(testUser.getUsername(), testUser.getPassword());
        verifyLoginSuccess(loginPage, getDriver(), softAssert);

        // ============================================
        // Step 2: Navigate to homepage after login if not redirected
        // ============================================
        ExtentReportManager.info("Navigate to homepage (if not redirected after login)");
        boolean homepageRedirected = loginPage.isRedirectedToHomepage();
        if (!homepageRedirected) {
            LOG.warn("User not redirected to homepage after login - navigating manually");
            homePage.navigateToHomePage();
        }

        // ============================================
        // Step 3: Select a movie showtime using filters
        // ============================================
        ExtentReportManager.info("User filter to navigate to a showtime with available seats");
        ShowtimeBooking showtimeWithSeats = ShowtimeSampleProvider.getShowtimeWithAvailableSeats(5, 1).get(0);

        homePage.showtimeFilterDropdowns.selectAllFiltersAndConfirm(
                showtimeWithSeats.getMovieName(),
                showtimeWithSeats.getCinemaBranchName(),
                showtimeWithSeats.getShowtimeId()
        );

        boolean isNavigated = homePage.isOnShowtimePage(showtimeWithSeats.getShowtimeId());
        verifySoftTrue(isNavigated, "User should be navigated to the selected showtime page", getDriver(), softAssert);

        // ============================================
        // Step 4: Select seats and book tickets
        // ============================================
        // Select random seats with random sample size between 1 and 5 seats if no size or range specified
        // Returns list of selected seat numbers for verification
        ExtentReportManager.info("Select sample seats and book");
        List<String> seatsToBook = BookingActionHelper.selectSampleSeatsAndBook(bookingPage);

        // ============================================
        // Step 5: Verify booking success
        // ============================================
        ExtentReportManager.info("Verify booking success");
        BookingVerificationHelper.verifyBookingSuccess(bookingPage, seatsToBook, getDriver(), softAssert);

        softAssert.assertAll();
        ExtentReportManager.info("E2E Flow completed successfully: Login → Browse → Select Showtime → Book → Verify");
    }

}

