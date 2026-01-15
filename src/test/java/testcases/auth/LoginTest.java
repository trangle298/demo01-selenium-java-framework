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

    @Test(groups = {"integration", "auth", "login", "smoke"})
    public void testValidLogin() {
        SoftAssert softAssert = new SoftAssert();

        ExtentReportManager.info("Step 2: Submit login form with valid credentials");
        loginPage.fillLoginFormAndSubmit(user.getUsername(), user.getPassword());

        ExtentReportManager.info("Step 3: Verify login success");
        verifyLoginSuccess(softAssert);
        verifyUserProfileName(user.getFullName(), softAssert);

        softAssert.assertAll();
    }

    @Test(groups = {"integration", "auth", "login"})
    public void testValidLogin_UsernameCaseInsensitive() {
        SoftAssert softAssert = new SoftAssert();

        ExtentReportManager.info("Step 2: Submit login form with valid username (different casing) and valid password");
        String modifiedUsername = user.getUsername().toUpperCase();
        loginPage.fillLoginFormAndSubmit(modifiedUsername, user.getPassword());

        ExtentReportManager.info("Step 3: Verify login success");
        verifyLoginSuccess(softAssert);
        verifyUserProfileName(user.getFullName(), softAssert);

        softAssert.assertAll();
    }

    @Test(groups = {"component", "auth", "login", "negative"})
    public void testInvalidLogin_EmptyPassword() {
        SoftAssert softAssert = new SoftAssert();

        ExtentReportManager.info("Step 2: Submit login form with valid username and empty password");
        loginPage.fillLoginFormAndSubmit(user.getUsername(), "");

        ExtentReportManager.info("Step 3: Verify password error message");
        boolean errorMsgDisplayed = verifySoftTrue(loginPage.isInvalidPasswordMessageDisplayed(),
                                                   "Password error message is displayed", softAssert);

        // Only verify text if error message is displayed
        if (errorMsgDisplayed) {
            String expectedMsg = Messages.getRequiredFieldError();
            String actualMsg = loginPage.getPasswordErrorMsgText();
            verifySoftEquals(actualMsg, expectedMsg, "Empty password error message text", softAssert);
        }

        softAssert.assertAll();
    }

    @DataProvider(name = "invalidPasswordScenarios")
    public Object[][] invalidPasswordScenarios() {
        return new Object[][]{
                {user.getUsername(), user.getPassword() + Instant.now().toEpochMilli(), "Incorrect Password"},
                {user.getUsername(), user.getPassword().toUpperCase(), "Incorrect Password Casing"},
        };
    }

    @Test(dataProvider = "invalidPasswordScenarios", groups = {"integration", "auth", "login", "negative"})
    public void testInvalidLogin_InvalidPassword(String username, String password, String scenario) {
        SoftAssert softAssert = new SoftAssert();

        ExtentReportManager.info("Step 2: Submit form with invalid scenario: " + scenario);
        loginPage.fillLoginFormAndSubmit(username, password);

        ExtentReportManager.info("Step 3: Verify login failure");
        verifyLoginFailure(softAssert);

        softAssert.assertAll();
    }

    // --------------------------
    // Helper methods for verification
    // --------------------------
    private void verifyLoginSuccess(SoftAssert softAssert) {
        // Verify login success message displayed
        boolean successMsgDisplayed = verifySoftTrue(loginPage.isLoginSuccessMessageDisplayed(),
                                                     "Verify Login success message is displayed", softAssert);

        // Verify success message text (only if alert is displayed)
        if (successMsgDisplayed) {
            String expectedMsg = Messages.getLoginSuccessMessage();
            String actualMsg = loginPage.getLoginSuccessMsgText();
            verifySoftEquals(actualMsg, expectedMsg, "Login success message text", softAssert);
        }

        // Verify user is logged in
        verifySoftTrue(loginPage.topBarNavigation.isUserProfileVisible(),
                "User profile link should be visible", softAssert);
    }

    private void verifyUserProfileName(String expectedUsername, SoftAssert softAssert) {
        String actualUsername = loginPage.topBarNavigation.getUserProfileName();
        verifySoftEquals(actualUsername, expectedUsername, "Displayed user profile name", softAssert);
    }

    private void verifyLoginFailure(SoftAssert softAssert) {
        // Verify login error alert displayed
        boolean alertDisplayed = verifySoftTrue(loginPage.isLoginErrorMessageDisplayed(),
                                               "Login error alert is displayed", softAssert);

        // Verify error message text (only if alert is displayed)
        if (alertDisplayed) {
            String expectedMsg = Messages.getLoginErrorMessage();
            String actualMsg = loginPage.getLoginErrorMsgText();
            verifySoftEquals(actualMsg, expectedMsg, "Login error alert text", softAssert);
        }

        // Verify user is not logged in
        verifySoftFalse(loginPage.topBarNavigation.isUserProfileVisible(),
                       "User profile link should not be visible", softAssert);
    }

}
