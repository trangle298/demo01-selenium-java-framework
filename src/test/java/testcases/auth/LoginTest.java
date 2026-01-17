package testcases.auth;

import base.BaseTest;
import helpers.Messages;
import helpers.TestUserProvider;
import model.TestUser;
import model.TestUserType;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import reports.ExtentReportManager;

import java.time.Instant;

import static helpers.SoftAssertionHelper.*;
import static helpers.AuthVerificationHelper.verifyLoginSuccess;

public class LoginTest extends BaseTest {

    private LoginPage loginPage;
    private TestUser user;

    @BeforeClass(alwaysRun = true)
    public void setupClass() {
        // Initialize user once for the entire class
        // This runs BEFORE DataProvider, so DataProvider can use it
        user = TestUserProvider.getUser(TestUserType.basicUser);
    }

    @BeforeMethod(alwaysRun = true)
    public void setUpMethod() {
        loginPage = new LoginPage(getDriver());

        ExtentReportManager.info("Test user: " + user.getUsername());
        ExtentReportManager.info("Step 1: Navigate to Login page");

        loginPage.navigateToLoginPage();
    }

    @Test(groups = {"integration", "auth", "login", "smoke", "critical"})
    public void testValidLogin() {
        // Use SoftAssert for multiple related checks (alert displayed + message text + user logged in)
        // This allows us to see the full picture if something fails
        SoftAssert softAssert = new SoftAssert();

        ExtentReportManager.info("Step 2: Submit login form with valid credentials");
        loginPage.fillLoginFormAndSubmit(user.getUsername(), user.getPassword());

        ExtentReportManager.info("Step 3: Verify login success");
        verifyLoginSuccess(loginPage, getDriver(), softAssert);

        softAssert.assertAll();  // Reports all failures together
    }

    @Test(groups = {"integration", "auth", "login"})
    public void testValidLogin_UsernameCaseInsensitive() {
        SoftAssert softAssert = new SoftAssert();

        ExtentReportManager.info("Step 2: Submit login form with valid username (different casing) and valid password");
        String modifiedUsername = user.getUsername().toUpperCase();
        loginPage.fillLoginFormAndSubmit(modifiedUsername, user.getPassword());

        ExtentReportManager.info("Step 3: Verify login success");
        verifyLoginSuccess(loginPage, getDriver(), softAssert);

        softAssert.assertAll();
    }

    @Test(groups = {"component", "auth", "login", "negative"})
    public void testInvalidLogin_EmptyPassword() {
        SoftAssert softAssert = new SoftAssert();

        ExtentReportManager.info("Step 2: Submit login form with valid username and empty password");
        loginPage.fillLoginFormAndSubmit(user.getUsername(), "");

        ExtentReportManager.info("Step 3: Verify password error message");
        verifyEmptyPasswordError(softAssert);

        softAssert.assertAll();
    }

    @DataProvider(name = "invalidPasswordScenarios")
    public Object[][] invalidPasswordScenarios() {
        return new Object[][]{
                {user.getUsername(), user.getPassword() + Instant.now().toEpochMilli(), "Incorrect Password"},
                {user.getUsername(), user.getPassword().toUpperCase(), "Incorrect Password Casing"},
        };
    }

    @Test(dataProvider = "invalidPasswordScenarios", groups = {"integration", "auth", "login", "critical", "negative"})
    public void testInvalidLogin_InvalidPassword(String username, String password, String scenario) {
        SoftAssert softAssert = new SoftAssert();

        ExtentReportManager.info("Step 2: Submit form with invalid scenario: " + scenario);
        loginPage.fillLoginFormAndSubmit(username, password);

        ExtentReportManager.info("Step 3: Verify login failure");
        verifyInvalidCredentialsError(softAssert);

        softAssert.assertAll();
    }

    // ---- Helper methods for verification ----
    private void verifyEmptyPasswordError(SoftAssert softAssert) {
        boolean errorMsgDisplayed = verifySoftTrue(loginPage.isInvalidPasswordMsgDisplayed(),
                "Password error message is displayed", getDriver(), softAssert);

        // Only verify text if error message is displayed
        if (errorMsgDisplayed) {
            String expectedMsg = Messages.getRequiredFieldError();
            String actualMsg = loginPage.getPasswordValidationText();
            verifySoftEquals(actualMsg, expectedMsg, "Empty password error message text", getDriver(), softAssert);
        }

        // Verify user is not logged in
        verifySoftFalse(loginPage.topBarNavigation.isUserProfileVisible(),
                "User profile link should not be visible", getDriver(), softAssert);
    }

    private void verifyInvalidCredentialsError(SoftAssert softAssert) {
        // Verify login error alert displayed
        boolean alertDisplayed = verifySoftTrue(loginPage.isLoginErrorAlertDisplayed(),
                                               "Login error alert is displayed",getDriver(), softAssert);

        // Verify error message text (only if alert is displayed)
        if (alertDisplayed) {
            String expectedMsg = Messages.getLoginErrorMessage();
            String actualMsg = loginPage.getLoginErrorMsgText();
            verifySoftEquals(actualMsg, expectedMsg, "Login error alert text", getDriver(), softAssert);
        }

        // Verify user is not logged in
        verifySoftFalse(loginPage.topBarNavigation.isUserProfileVisible(),
                       "User profile link should not be visible", getDriver(), softAssert);
    }
}