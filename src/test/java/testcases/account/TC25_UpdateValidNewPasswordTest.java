package testcases.account;

import base.BaseTest;
import helpers.actions.AuthActionHelper;
import helpers.providers.UserAccountTestDataGenerator;
import helpers.verifications.AccountVerificationHelper;
import model.UserAccount;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.AccountPage;
import pages.LoginPage;
import reports.ExtentReportManager;

public class TC25_UpdateValidNewPasswordTest extends BaseTest {
    @Test(groups = "requiresUser",
            description = "Test Successful Update User Info: Name, Email, Phone Number")
    public void testSuccessfulUpdatePassword() {
        SoftAssert softAssert = new SoftAssert();

        // Login
        ExtentReportManager.info("Login");
        LoginPage loginPage = new LoginPage(getDriver());
        UserAccount testUser = getTestUser();
        AuthActionHelper.login(loginPage,testUser);

        // Navigate to Account page and update password
        ExtentReportManager.info("Navigate to account page and update password");
        AccountPage accountPage = new AccountPage(getDriver());
        accountPage.navigateToAccountPage();
        accountPage.waitForAccountFormDisplay();

        String originalPassword = testUser.getPassword();
        String newPassword = UserAccountTestDataGenerator.generateNewPassword(originalPassword);
        accountPage.changePasswordAndSave(newPassword);

        // Verify success message, failed login with old password, successful login with new password
        AccountVerificationHelper.verifyPasswordUpdateSuccess(
                accountPage,
                testUser.getUsername(), originalPassword, newPassword,
                getDriver(), softAssert);

        softAssert.assertAll();
    }
}