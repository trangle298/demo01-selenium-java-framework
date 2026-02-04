package testcases.authentication;

import base.BaseTest;
import model.UserAccount;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import reports.ExtentReportManager;

import static helpers.verifications.AuthVerificationHelper.verifyLoginSuccess;

public class TC20_UsernameCaseInsensitiveTest extends BaseTest {

    @Test(groups = "requiresUser",
            description = "Test User can Login with Username in different casing")
    public void testUsernameIsCaseInsensitive() {

        SoftAssert softAssert =  new SoftAssert();
        LoginPage loginPage = new LoginPage(getDriver());

        ExtentReportManager.info("Navigate to Login page");
        loginPage.navigateToLoginPage();

        // Generate username in different casing and login
        ExtentReportManager.info("Fill valid credentials and submit login form");
        UserAccount testUser = getTestUser();
        String usernameDifferentCasing = testUser.getUsername().toUpperCase();
        loginPage.fillLoginFormThenSubmit(usernameDifferentCasing, testUser.getPassword());

        // Verify login success: alert displayed + message text + top bar user profile
        ExtentReportManager.info("Verify successful login");
        verifyLoginSuccess(loginPage, getDriver(), softAssert);

        softAssert.assertAll();
    }
}
