package testcases.account;

import base.BaseTest;
import helpers.actions.AuthActionHelper;
import helpers.providers.TestUserProvider;
import helpers.providers.UserAccountTestDataGenerator;
import helpers.verifications.AccountVerificationHelper;
import model.UserAccount;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.AccountPage;
import pages.LoginPage;
import reports.ExtentReportManager;

public class TC28_UpdateWithExistingEmailTest extends BaseTest {

    @Test(groups = "requiresUser",
            description = "Test Blocked Update due to an invalid field: Full Name")
    public void testUpdateBlockedWithExistingEmail() {
        SoftAssert softAssert = new SoftAssert();

        // Login
        ExtentReportManager.info("Login with newly created user credentials");
        LoginPage loginPage = new LoginPage(getDriver());
        UserAccount testUser = getTestUser();
        AuthActionHelper.login(loginPage, testUser);

        // Attempt to update user info with valid phone nr and email but invalid name (containing numbers)
        ExtentReportManager.info("Navigate to account page and attempt update with invalid full name");
        AccountPage accountPage = new AccountPage(getDriver());
        accountPage.navigateToAccountPage();
        accountPage.waitForAccountFormDisplay();

        String existingEmail = TestUserProvider.getDefaultTestUser().getEmail();
        String newName = UserAccountTestDataGenerator.generateNewName(testUser.getFullName());
        String newPhoneNr = UserAccountTestDataGenerator.generateNewPhoneNumber(testUser.getPhoneNumber());

        accountPage.changeUserInfoAndSave(newName, existingEmail, newPhoneNr);

        // Verify correct validation message and form displays original values
        ExtentReportManager.info("Verify failed updated of user info due to existing email");
        AccountVerificationHelper.verifyUpdateFailsDueToExistingEmailError(accountPage, testUser, getDriver(), softAssert);

        softAssert.assertAll();
    }
}