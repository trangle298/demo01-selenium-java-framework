package testcases.auth;

import base.BaseTest;
import helpers.providers.MessagesProvider;
import helpers.providers.TestUserProvider;
import model.TestUser;
import model.TestUserType;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import reports.ExtentReportManager;

import java.time.Instant;

import static helpers.verifications.SoftAssertionHelper.*;
import static helpers.verifications.AuthVerificationHelper.verifyLoginSuccess;

public class LoginTest extends BaseTest {

    private LoginPage loginPage;
    private TestUser user;
    private SoftAssert softAssert;

    @BeforeClass(alwaysRun = true)
    public void setupClass() {
        // Initialize user once for the entire class
        // This runs BEFORE DataProvider, so DataProvider can use it
        user = TestUserProvider.getUser(TestUserType.USER_BASIC);
    }

    @BeforeMethod(alwaysRun = true)
    public void setUpMethod() {
        loginPage = new LoginPage(getDriver());
        softAssert = new SoftAssert();

        ExtentReportManager.info("Test user: " + user.getUsername());
        ExtentReportManager.info("Navigate to Login page");
        loginPage.navigateToLoginPage();
    }

    @Test(groups = {"integration", "auth", "login", "smoke", "critical"})
    public void testValidLogin() {
        ExtentReportManager.info("Fill valid credentials and submit login form");
        loginPage.fillLoginFormThenSubmit(user.getUsername(), user.getPassword());

        // Verify login success: alert displayed + message text + top bar user profile
        ExtentReportManager.info("Verify successful login");
        verifyLoginSuccess(loginPage, getDriver(), softAssert);

        softAssert.assertAll();
    }

    @Test(groups = {"integration", "auth", "login"})
    public void testValidLogin_UsernameCaseInsensitive() {
        ExtentReportManager.info("Fill username in different casing and valid password and submit login form");
        String modifiedUsername = user.getUsername().toUpperCase();
        loginPage.fillLoginFormThenSubmit(modifiedUsername, user.getPassword());

        ExtentReportManager.info("Verify successful login");
        verifyLoginSuccess(loginPage, getDriver(), softAssert);

        softAssert.assertAll();
    }

    @Test(groups = {"component", "auth", "login", "negative"})
    public void testInvalidLogin_EmptyPassword() {
        ExtentReportManager.info("Submit login form with valid username and empty password");
        loginPage.fillLoginFormThenSubmit(user.getUsername(), "");

        ExtentReportManager.info("Verify password error message");
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
        ExtentReportManager.info("Submit form with invalid scenario: " + scenario);
        loginPage.fillLoginFormThenSubmit(username, password);

        ExtentReportManager.info("Verify login failure");
        verifyInvalidCredentialsError(softAssert);

        softAssert.assertAll();
    }

    // ---- Helper methods for verification ----
    private void verifyEmptyPasswordError(SoftAssert softAssert) {
        boolean errorMsgDisplayed = verifySoftTrue(loginPage.isInvalidPasswordMsgDisplayed(),
                "Password error message is displayed", getDriver(), softAssert);

        // Only verify text if error message is displayed
        if (errorMsgDisplayed) {
            String expectedMsg = MessagesProvider.getRequiredFieldError();
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
            String expectedMsg = MessagesProvider.getLoginErrorMessage();
            String actualMsg = loginPage.getLoginErrorMsgText();
            verifySoftEquals(actualMsg, expectedMsg, "Login error alert text", getDriver(), softAssert);
        }

        // Verify user is not logged in
        verifySoftFalse(loginPage.topBarNavigation.isUserProfileVisible(),
                       "User profile link should not be visible", getDriver(), softAssert);
    }
}