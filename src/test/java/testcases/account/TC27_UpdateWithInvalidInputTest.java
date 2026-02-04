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

public class TC27_UpdateWithInvalidInputTest extends BaseTest {

    @Test(groups = "requiresUser",
            description = "Test Blocked Update due to an invalid field: Full Name")
    public void testUpdateBlockedWithInvalidFullName() {

        SoftAssert softAssert = new SoftAssert();
        LoginPage loginPage = new LoginPage(getDriver());

        // Login
        ExtentReportManager.info("Login");
        UserAccount testUser = getTestUser();
        AuthActionHelper.login(loginPage, testUser);

        // Attempt to update user info with valid phone nr and email but invalid name (containing numbers)
        ExtentReportManager.info("Navigate to account page and attempt update with invalid full name");
        AccountPage accountPage = new AccountPage(getDriver());
        accountPage.navigateToAccountPage();
        accountPage.waitForAccountFormDisplay();

        String invalidFullName = UserAccountTestDataGenerator.generateInvalidNameContainingNumbers();
        String newPhoneNr = UserAccountTestDataGenerator.generateNewPhoneNumber(testUser.getPhoneNumber());
        String newEmail = UserAccountTestDataGenerator.generateNewUniqueEmail();

        accountPage.changeUserInfoAndSave(invalidFullName, newEmail, newPhoneNr);

        // Verify correct validation message and form displays original values
        ExtentReportManager.info("Verify failed updated of user info due to invalid full name");
        String expectedMsg = MessagesProvider.getNameContainsNumberError();

        AccountVerificationHelper.verifyUpdateFailsDueToFieldValidation(
                accountPage, AccountDataField.FULL_NAME,
                expectedMsg, testUser,
                getDriver(), softAssert);

        softAssert.assertAll();
    }
}