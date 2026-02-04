package testcases.authentication;

import base.BaseTest;
import helpers.providers.UserAccountTestDataGenerator;
import helpers.verifications.AuthVerificationHelper;
import model.UserAccount;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import reports.ExtentReportManager;

public class TC17_LoginWithInvalidCredentialsTest extends BaseTest {

    @Test(groups = "requiresUser",
            description = "Test Blocked Login with invalid credential: Invalid password")
    public void testInvalidPasswordBlocksLogin(){

        SoftAssert softAssert =  new SoftAssert();

        ExtentReportManager.info("Navigate to Login page");
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigateToLoginPage();

        ExtentReportManager.info("Attempt login with valid username and incorrect password");
        // Generate incorrect password and attempt login
        UserAccount testUser = getTestUser();
        String incorrectPassword = UserAccountTestDataGenerator.generateNewPassword(testUser.getPassword());
        loginPage.fillLoginFormThenSubmit(testUser.getUsername(), incorrectPassword);

        // Verify login failed: alert displayed + alert text + top bar user profile not shown
        ExtentReportManager.info("Verify unsuccessful login");
        AuthVerificationHelper.verifyInvalidCredentialsLoginError(loginPage, getDriver(), softAssert);

        softAssert.assertAll();
    }
}