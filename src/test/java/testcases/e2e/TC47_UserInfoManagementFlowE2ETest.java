package testcases.e2e;

import api.services.UserService;
import base.BaseTest;
import helpers.actions.AuthActionHelper;
import helpers.providers.UserAccountTestDataGenerator;
import helpers.verifications.AccountVerificationHelper;
import helpers.verifications.SoftAssertionHelper;
import model.UserAccount;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.*;
import reports.ExtentReportManager;

import static helpers.verifications.AuthVerificationHelper.*;

public class TC47_UserInfoManagementFlowE2ETest extends BaseTest {

    @Test(groups = "requiresUser")
    public void testUserInfoManagementFlow() {

        SoftAssert softAssert = new SoftAssert();
        LoginPage loginPage = new LoginPage(getDriver());

        // ============================================
        // Step 1: Login and verify user profile link on top bar displays correct name
        // ============================================
        ExtentReportManager.info("Login");
        UserAccount testUser = getTestUser();
        AuthActionHelper.login(loginPage, testUser);

        ExtentReportManager.info("Verify user profile button on top bar displays correct name");
        verifyUserButtonDisplaysCorrectName(loginPage, testUser.getFullName(), getDriver(), softAssert);

        // =================================================
        // Step 2: Navigate to account page and verify UI displays correct user info
        // =================================================
        ExtentReportManager.info("Navigate to Account page");
        AccountPage accountPage = new AccountPage(getDriver());
        accountPage.navigateToAccountPage();

        ExtentReportManager.info("Verify displayed user data matches created user data (payload)");
        UserAccount accountDataFromUI = accountPage.getAccountData();
        SoftAssertionHelper.verifySoftEquals(
                accountDataFromUI, testUser,
                "Displayed user account info",
                getDriver(), softAssert);

        // ======================================================
        // Step 3: Update user info and verify update success on both UI and backend
        // ======================================================
        ExtentReportManager.info("Update account: name, email, phone");
        String newName = UserAccountTestDataGenerator.generateNewName(accountPage.getFullName());
        String newEmail = UserAccountTestDataGenerator.generateNewUniqueEmail();
        String newPhoneNr = UserAccountTestDataGenerator.generateNewPhoneNumber(accountPage.getPhoneNumber());

        accountPage.changeUserInfoAndSave(newName, newEmail, newPhoneNr);

        ExtentReportManager.info("Verify user info updated successfully on UI");
        AccountVerificationHelper.verifyUserInfoUpdateSuccessOnUI(accountPage, newName, newEmail, newPhoneNr, getDriver(), softAssert);

        // Verify user account info in backend matches updated values
        ExtentReportManager.info("Verify update persisted in backend");
        // Fetch user info via API using username
        UserService userService = new UserService();
        UserAccount apiUserInfo = userService.getUserDetails(testUser.getUsername());

        AccountVerificationHelper.verifyUserInfoUpdateSuccessInBackend(apiUserInfo, newName, newEmail, newPhoneNr, softAssert);

        // ======================================================
        // Step 4: Logout and login again to verify update persists across sessions
        // ======================================================
        ExtentReportManager.info("Logout and login again to verify update persistence");

        AuthActionHelper.logout(accountPage);
        AuthActionHelper.login(loginPage, testUser);

        ExtentReportManager.info("Verify user profile button on top bar displays updated name");
        verifyUserButtonDisplaysCorrectName(loginPage, newName, getDriver(), softAssert);

        softAssert.assertAll();
    }
}