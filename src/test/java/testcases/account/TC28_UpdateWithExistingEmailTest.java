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
            description = "Test Blocked Update due to existing email")
    public void testUpdateBlockedWithExistingEmail() {
        SoftAssert softAssert = new SoftAssert();

        // Create a second user to source an existing email — cleaned up after test
        UserAccount secondUser = TestUserProvider.createNewTestUser();
        try {
            // Login with primary test user
            ExtentReportManager.info("Login with newly created user credentials");
            LoginPage loginPage = new LoginPage(getDriver());
            UserAccount testUser = getTestUser();
            AuthActionHelper.login(loginPage, testUser);

            // Attempt to update primary user's info using the second user's email
            ExtentReportManager.info("Navigate to account page and attempt update with existing email");
            AccountPage accountPage = new AccountPage(getDriver());
            accountPage.navigateToAccountPage();
            accountPage.waitForAccountFormDisplay();

            String existingEmail = secondUser.getEmail();
            String newName = UserAccountTestDataGenerator.generateNewName(testUser.getFullName());
            String newPhoneNr = UserAccountTestDataGenerator.generateNewPhoneNumber(testUser.getPhoneNumber());

            accountPage.changeUserInfoAndSave(newName, existingEmail, newPhoneNr);

            // Verify correct validation message and form displays original values
            ExtentReportManager.info("Verify failed update of user info due to existing email");
            AccountVerificationHelper.verifyUpdateFailsDueToExistingEmailError(accountPage, testUser, getDriver(),
                    softAssert);

            softAssert.assertAll();
        } finally {
            TestUserProvider.deleteUser(secondUser);
        }
    }
}