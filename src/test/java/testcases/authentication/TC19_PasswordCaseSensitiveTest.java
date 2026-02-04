package testcases.authentication;

import base.BaseTest;
import helpers.verifications.AuthVerificationHelper;
import model.UserAccount;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import reports.ExtentReportManager;

public class TC19_PasswordCaseSensitiveTest extends BaseTest {

    @Test(groups = "requiresUser",
            description = "Test Login is blocked if password has wrong casing")
    public void testPasswordIsCaseSensitive() {

        SoftAssert softAssert =  new SoftAssert();
        LoginPage loginPage = new LoginPage(getDriver());

        ExtentReportManager.info("Navigate to Login page");
        loginPage.navigateToLoginPage();

        // Attempt to log in with password in wrong casing
        ExtentReportManager.info("Attempt login with valid username and incorrect password");
        UserAccount testUser = getTestUser();
        String passwordWrongCasing = testUser.getPassword().toUpperCase();   // default mixed case
        loginPage.fillLoginFormThenSubmit(testUser.getUsername(), passwordWrongCasing);

        // Verify login failed: alert displayed + alert text + top bar user profile not shown
        ExtentReportManager.info("Verify unsuccessful login");
        AuthVerificationHelper.verifyInvalidCredentialsLoginError(loginPage, getDriver(), softAssert);

        softAssert.assertAll();
    }
}