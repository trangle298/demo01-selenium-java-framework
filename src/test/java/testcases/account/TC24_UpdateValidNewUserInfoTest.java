package testcases.account;

import api.services.UserService;
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

public class TC24_UpdateValidNewUserInfoTest extends BaseTest {

    @Test(groups = "requiresUser",
            description = "Test Successful Update User Info: Name, Email, Phone Number")
    public void testSuccessfulUpdateUserInfo() {

        SoftAssert softAssert = new SoftAssert();
        LoginPage loginPage = new LoginPage(getDriver());

        // Login
        ExtentReportManager.info("Login");
        UserAccount testUser = getTestUser();
        AuthActionHelper.login(loginPage, testUser);

        // Navigate to account page and update user info: Name, Email, Phone Number
        ExtentReportManager.info("Navigate to account page and update user info: name, email, phone number");
        AccountPage accountPage = new AccountPage(getDriver());
        accountPage.navigateToAccountPage();
        accountPage.waitForAccountFormDisplay();

        String newName = UserAccountTestDataGenerator.generateNewName(testUser.getFullName());
        String newPhoneNr = UserAccountTestDataGenerator.generateNewPhoneNumber(testUser.getPhoneNumber());
        String newEmail = UserAccountTestDataGenerator.generateNewUniqueEmail();

        accountPage.changeUserInfoAndSave(newName, newEmail, newPhoneNr);

        // Verify update success message and new values are displayed on UI
        ExtentReportManager.info("Verify user info updated successfully on UI");
        AccountVerificationHelper.verifyUserInfoUpdateSuccessOnUI(accountPage, newName, newEmail, newPhoneNr, getDriver(), softAssert);

        // Verify the user account info in backend matches updated values
        ExtentReportManager.info("Verify updated user info persist in backend");
        UserService userService = new UserService();
        UserAccount apiUserInfo = userService.getUserDetails(testUser.getUsername());

        AccountVerificationHelper.verifyUserInfoUpdateSuccessInBackend(apiUserInfo, newName, newEmail, newPhoneNr, softAssert);

        softAssert.assertAll();
    }
}