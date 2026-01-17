package testcases.e2e;

import base.BaseTest;
import helpers.BookingHelper;
import helpers.TestUserProvider;
import model.TestUser;
import model.TestUserType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.HomePage;
import pages.LoginPage;
import pages.ShowtimePage;
import reports.ExtentReportManager;

import java.util.List;

import static helpers.AuthVerificationHelper.verifyLoginSuccess;

/**
 * E2E Test: Complete Booking Flow
 * Tests the entire user journey: Login → Browse → Select Showtime → Book Tickets → Verify Booking
 */
public class CompleteBookingFlowE2ETest extends BaseTest {

    private LoginPage loginPage;
    private HomePage homePage;
    private ShowtimePage showtimePage;
    private TestUser testUser;

    @BeforeMethod
    public void setupMethod() {
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
        showtimePage = new ShowtimePage(getDriver());
        testUser = TestUserProvider.getUser(TestUserType.bookingUser);
    }

    @Test(groups = {"e2e", "booking", "critical"})
    public void testCompleteBookingFlow() throws Exception {
        SoftAssert softAssert = new SoftAssert();

        // ============================================
        // Step 1: User logs in
        // ============================================
        ExtentReportManager.info("Step 1: Navigate to login page and authenticate");
        loginPage.navigateToLoginPage();
        loginPage.fillLoginFormAndSubmit(testUser.getUsername(), testUser.getPassword());

        verifyLoginSuccess(loginPage, getDriver(), softAssert);

        // ============================================
        // Step 2: Navigate to homepage after login
        // ============================================
        ExtentReportManager.info("Step 2: Navigate to homepage (if not redirected automatically after login)");
        boolean homepageRedirected = loginPage.isRedirectedToHomepage();
        if (!homepageRedirected) {
            homePage.navigateToHomePage();
        }

        // ============================================
        // Step 3: Select a movie showtime using filters
        // ============================================
        ExtentReportManager.info("Step 3: Find and navigate to a showtime with available seats");
        BookingHelper.navigateToAvailableShowtimePage(showtimePage);

        // ============================================
        // Step 4: Select seats and book tickets
        // ============================================
        ExtentReportManager.info("Step 4: Select available seats");
        List<String> seatsToBook = BookingHelper.selectAvailableSeats(showtimePage, 2);
        showtimePage.clickBookTicketsButton();

        // ============================================
        // Step 5: Verify booking success
        // ============================================
        ExtentReportManager.info("Step 6: Verify booking confirmation");
        BookingHelper.verifyBookingSuccess(showtimePage, seatsToBook, getDriver(), softAssert);

        softAssert.assertAll();
        ExtentReportManager.info("E2E Flow completed successfully: Login → Browse → Select Showtime → Book → Verify");
    }

}

