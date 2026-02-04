package testcases.authentication;

import base.BaseTest;
import model.UserAccount;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import reports.ExtentReportManager;

import static helpers.verifications.AuthVerificationHelper.verifyLoginSuccess;

public class TC16_LoginWithValidCredentialsTest extends BaseTest {
    @Test(groups = "requiresUser",
            description = "Test successful login with valid credentials")
    public void testSuccessfulLoginWithValidCredentials() {
        SoftAssert softAssert =  new SoftAssert();

        ExtentReportManager.info("Navigate to Login page");
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigateToLoginPage();

        ExtentReportManager.info("Fill valid credentials and submit login form");
        UserAccount testUser = getTestUser();
        loginPage.fillLoginFormThenSubmit(testUser.getUsername(), testUser.getPassword());

        // Verify login success: alert displayed + message text + top bar user profile
        ExtentReportManager.info("Verify successful login");
        verifyLoginSuccess(loginPage, getDriver(), softAssert);

        softAssert.assertAll();
    }
}