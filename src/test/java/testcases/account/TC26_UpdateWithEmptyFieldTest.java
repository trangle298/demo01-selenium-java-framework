package testcases.account;

import base.BaseTest;
import helpers.actions.AuthActionHelper;
import helpers.providers.UserAccountTestDataGenerator;
import helpers.providers.MessagesProvider;
import helpers.verifications.AccountVerificationHelper;
import model.UserAccount;
import model.enums.AccountDataField;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.AccountPage;
import pages.LoginPage;
import reports.ExtentReportManager;

public class TC26_UpdateWithEmptyFieldTest extends BaseTest {

    @Test(groups = "requiresUser",
            description = "Test Blocked Update due to an empty field: Phone Number")
    public void testUpdateBlockedWithEmptyPhoneNr() {

        SoftAssert softAssert = new SoftAssert();
        LoginPage loginPage = new LoginPage(getDriver());

        // Login
        ExtentReportManager.info("Login with newly created user credentials");
        UserAccount testUser = getTestUser();
        AuthActionHelper.login(loginPage, testUser);

        // Attempt to update user info: valid email, valid phone number but empty phone number
        ExtentReportManager.info("Navigate to account page and attempt update with empty phone number");
        AccountPage accountPage = new AccountPage(getDriver());
        accountPage.navigateToAccountPage();
        accountPage.waitForAccountFormDisplay();

        String newName = UserAccountTestDataGenerator.generateNewName(testUser.getFullName());
        String newEmail = UserAccountTestDataGenerator.generateNewUniqueEmail();

        accountPage.changeUserInfoAndSave(newName, newEmail, "");

        // Verify correct validation message and form displays original values
        ExtentReportManager.info("Verify failed updated of user info");
        String expectedMsg = MessagesProvider.getAccountPhoneNrRequiredError();

        AccountVerificationHelper.verifyUpdateFailsDueToFieldValidation(
                accountPage, AccountDataField.PHONE_NUMBER,
                expectedMsg, testUser,
                getDriver(), softAssert);

        softAssert.assertAll();
    }
}