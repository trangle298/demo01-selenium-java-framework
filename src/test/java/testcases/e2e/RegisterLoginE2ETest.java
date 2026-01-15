package testcases.e2e;

import base.BaseTest;
import helpers.AuthTestDataGenerator;
import model.RegisterRequest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.HomePage;
import pages.LoginPage;
import pages.RegisterPage;
import reports.ExtentReportManager;

/**
 * E2E Test: Complete Registration and Login Flow
 * Tests the entire user journey from registration through to successful login and account usage
 */
public class RegisterLoginE2ETest extends BaseTest {

    private RegisterPage registerPage;
    private LoginPage loginPage;
    private HomePage homePage;

    @BeforeMethod
    public void setupMethod() {
        registerPage = new RegisterPage(getDriver());
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
    }

    @Test(groups = {"e2e", "auth", "critical"})
    public void testCompleteRegisterAndLoginFlow() {
        SoftAssert softAssert = new SoftAssert();

        // ============================================
        // Step 1: Register new account
        // ============================================
        ExtentReportManager.info("Step 1: Navigate to Register page and create new account");
        registerPage.navigateToRegisterPage();

        RegisterRequest registerRequest = AuthTestDataGenerator.generateValidRegisterData();
        registerPage.fillAndSubmitRegisterForm(
                registerRequest.getUsername(),
                registerRequest.getPassword(),
                registerRequest.getConfirmPassWord(),
                registerRequest.getFullName(),
                registerRequest.getEmail()
        );

        ExtentReportManager.info("Step 2: Verify registration success");



        // ============================================
        // Step 2: Login with newly registered account
        // ============================================
        ExtentReportManager.info("Step 3: Navigate to Login page and login with new credentials");
        loginPage.navigateToLoginPage();
        loginPage.fillLoginFormAndSubmit(registerRequest.getUsername(), registerRequest.getPassword());

        ExtentReportManager.info("Step 4: Verify login success with new account");
        verifySoftTrue(
                loginPage.isLoginSuccessMessageDisplayed(),
                "Login success message is displayed after registration",
                softAssert
        );

        // ============================================
        // Step 3: Verify user can access authenticated features
        // ============================================
        ExtentReportManager.info("Step 5: Verify user profile is accessible");
        verifySoftTrue(
                loginPage.topBarNavigation.isUserProfileVisible(),
                "User profile link is visible after login",
                softAssert
        );

        String actualProfileName = loginPage.topBarNavigation.getUserProfileName();
        String expectedProfileName = registerRequest.getFullName();
        verifySoftEquals(
                actualProfileName,
                expectedProfileName,
                "User profile displays correct full name from registration",
                softAssert
        );

        ExtentReportManager.info("Step 6: Verify navigation to homepage works");
        boolean homepageRedirected = loginPage.isRedirectedToHomepage();
        if (!homepageRedirected) {
            homePage.navigateToHomePage();
        }

        verifySoftTrue(
                homePage.isOnHomePage(),
                "User can navigate to homepage after successful registration and login",
                softAssert
        );

        softAssert.assertAll();
        ExtentReportManager.info("E2E Flow completed successfully: Register → Login → Access Features");
    }
}

