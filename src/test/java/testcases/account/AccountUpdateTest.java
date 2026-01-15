package testcases.account;

import base.BaseTest;
import helpers.AuthTestDataGenerator;
import helpers.Messages;
import helpers.TestUserProvider;
import model.TestUser;
import model.TestUserType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.AccountPage;
import pages.LoginPage;
import reports.ExtentReportManager;

public class AccountUpdateTest extends BaseTest {

    private AccountPage accountPage;
    TestUser testUser;

    // store originals so @AfterMethod can revert changes
    private String originalUsername;
    private String originalName;
    private String originalEmail;
    private String originalPhone;
    private String originalPassword;

    @BeforeMethod(alwaysRun = true)
    public void setUpMethod() {
        LoginPage loginPage = new LoginPage(getDriver());
        testUser = TestUserProvider.getUser(TestUserType.accountUpdateUser);
        originalUsername = testUser.getUsername();

        ExtentReportManager.info("Log in and navigate to Account page before test");
        loginPage.navigateToLoginPage();
        loginPage.fillLoginFormAndSubmit(originalUsername, testUser.getPassword());

        loginPage.topBarNavigation.waitForUserProfileLink();

        // initialize page objects with the same driver/session
        accountPage = new AccountPage(getDriver());
        // navigate to account page and capture current values for revert
        accountPage.navigateToAccountPage();
        originalName = accountPage.getFullName();
        originalEmail = accountPage.getEmail();
        originalPhone = accountPage.getPhoneNumber();
        originalPassword = accountPage.getPassword();
    }

    @Test(groups = {"integration", "auth", "account", "smoke"})
    public void testValidUpdate_AccountInfo() {
        SoftAssert softAssert = new SoftAssert();

        String newName = AuthTestDataGenerator.generateNewName(originalName);
        String newEmail = AuthTestDataGenerator.generateNewEmail(originalEmail);
        String newPhone = AuthTestDataGenerator.generateNewPhoneNumber(originalPhone);

        ExtentReportManager.info("Update new name, email, phone on Account Page");
        accountPage.changeName(newName);
        accountPage.changeEmail(newEmail);
        accountPage.changePhoneNumber(newPhone);
        accountPage.saveChanges();

        ExtentReportManager.info("Verify account info update success");
        verifyAccountInfoUpdateSuccess(newName, newEmail, newPhone, softAssert);

        softAssert.assertAll();

        ExtentReportManager.info("Reverting changes after test");
        revertAccountChanges(originalName, originalEmail, originalPhone);
    }

    @Test(groups = {"integration", "auth", "account", "smoke"})
    public void testValidUpdate_Password() {
        SoftAssert softAssert = new SoftAssert();

        String newPassword = AuthTestDataGenerator.generateNewPassword(originalPassword);

        ExtentReportManager.info("Update password on Account Page");
        accountPage.changePassword(newPassword);
        accountPage.saveChanges();

        ExtentReportManager.info("Verify password update success");
        verifyPasswordUpdateSuccess(newPassword, softAssert);

        softAssert.assertAll();

        ExtentReportManager.info("Reverting changes after test");
        revertPasswordChange(originalPassword);
    }

    @Test(groups = {"component", "auth", "account", "negative"})
    public void testInvalidUpdate_BlankEmail() {

        SoftAssert softAssert = new SoftAssert();

        ExtentReportManager.info("Attempt to update account with blank email");
        accountPage.changeEmail("");
        accountPage.saveChanges();

        ExtentReportManager.info("Verify update failed due to blank field validation");
        verifyUpdateFailureWithBlankEmail(softAssert);

        softAssert.assertAll();
    }

    @Test(groups = {"component", "auth", "account", "negative"})
    public void testInvalidUpdate_InvalidName() {

        SoftAssert softAssert = new SoftAssert();

        String invalidName = AuthTestDataGenerator.generateInvalidNameContainingNumbers();

        ExtentReportManager.info("Attempt to update account with invalid name: " + invalidName);
        accountPage.changeName(invalidName);
        accountPage.saveChanges();

        ExtentReportManager.info("Verify update failed due to invalid input validation");
        verifyUpdateFailureWithInvalidName(softAssert);

        softAssert.assertAll();
    }

    @Test(groups = {"component", "auth", "account", "negative"})
    public void testInvalidUpdate_ShortPassword() {

        SoftAssert softAssert = new SoftAssert();

        String shortPassword = AuthTestDataGenerator.generateInvalidShortPassword();

        ExtentReportManager.info("Attempt to update password with short password: " + shortPassword);
        accountPage.changePassword(shortPassword);
        accountPage.saveChanges();

        ExtentReportManager.info("Verify update failed due to short password validation");
        verifyUpdateFailureWithShortPassword(softAssert);

        softAssert.assertAll();
    }

    @Test(groups = {"component", "auth", "account", "negative"})
    public void testUsernameReadonly() {
        SoftAssert softAssert = new SoftAssert();

        ExtentReportManager.info("Attempt to change username (should be read-only)");
        String newUsername = AuthTestDataGenerator.generateValidRegisterData().getUsername();

        accountPage.changeUsername(newUsername);
        accountPage.saveChanges();

        ExtentReportManager.info("Verify username remains unchanged after update attempt");
        verifyUsernameRemainsUnchanged(newUsername, softAssert);

        softAssert.assertAll();
    }

    // --------------------------
    // Helper methods for verification
    // --------------------------

    private void verifyUpdateSuccessAlert(SoftAssert softAssert) {
        String expectedMsg = Messages.getAccountUpdateSuccessMessage();
        String actualMsg = accountPage.getUpdateAlertText();

        verifySoftEquals(actualMsg, expectedMsg, "Account update success message text", softAssert);
    }

    private void verifyAccountInfoUpdateSuccess(String newName, String newEmail, String newPhone, SoftAssert softAssert) {
        verifyUpdateSuccessAlert(softAssert);
        accountPage.waitForUpdateAlertToDisappear();

        verifySoftEquals(accountPage.getFullName(), newName,
                "Full Name in form after update", softAssert);
        verifySoftEquals(accountPage.getEmail(), newEmail,
                "Email in form after update", softAssert);
        verifySoftEquals(accountPage.getPhoneNumber(), newPhone,
                "Phone Number in form after update", softAssert);
    }

    private void verifyPasswordUpdateSuccess(String newPassword, SoftAssert softAssert) {
        verifyUpdateSuccessAlert(softAssert);
        accountPage.waitForUpdateAlertToDisappear();

        verifySoftEquals(accountPage.getPassword(), newPassword,
                "Password in form after update", softAssert);

        // move to e2e
//        accountPage.topBarNavigation.logout();
//        LoginPage loginPage = new LoginPage(getDriver());
//        loginPage.navigateToLoginPage();
//        loginPage.fillLoginFormAndSubmit(originalUsername, newPassword);
//
//        verifySoftTrue(loginPage.topBarNavigation.isUserProfileVisible(),
//                "Login with new password is successful", softAssert);
    }

    private void verifyUpdateFailureWithBlankEmail(SoftAssert softAssert) {
        verifySoftFalse(accountPage.isUpdateAlertDisplayed(),
                "No update alert displayed for failed update with blank email", softAssert);

        boolean emailErrorDisplayed = verifySoftTrue(accountPage.isEmailValidationErrorDisplayed(),
                "Email validation error is displayed for blank email", softAssert);

        if (emailErrorDisplayed) {
            String expectedErrorMsg = Messages.getRequiredFieldError();
            verifySoftEquals(expectedErrorMsg, accountPage.getEmailValidationErrorText(),
                    "Email validation error message text", softAssert);
        }

        accountPage.refreshPage();
        String currentEmail = accountPage.getEmail();

        verifySoftEquals(currentEmail, originalEmail,
                "Unchanged email after failed update", softAssert);
    }

    private void verifyUpdateFailureWithInvalidName(SoftAssert softAssert) {
        verifySoftFalse(accountPage.isUpdateAlertDisplayed(),
                "No update alert displayed for failed update with invalid name", softAssert);

        boolean nameErrorDisplayed = verifySoftTrue(accountPage.isNameValidationErrorDisplayed(),
                "Name validation error is displayed for invalid name", softAssert);

        if (nameErrorDisplayed) {
            String expectedErrorMsg = Messages.getNameContainsNumberError();
            verifySoftEquals(expectedErrorMsg, accountPage.getNameValidationErrorText(),
                    "Name validation error message text", softAssert);
        }

        accountPage.refreshPage();
        verifySoftEquals(accountPage.getFullName(), originalName,
                "Full Name remains unchanged after failed update", softAssert);
    }

    private void verifyUpdateFailureWithShortPassword(SoftAssert softAssert) {
        verifySoftFalse(accountPage.isUpdateAlertDisplayed(),
                "No update alert displayed for failed update with invalid password", softAssert);

        boolean passwordErrorDisplayed = verifySoftTrue(accountPage.isPasswordValidationErrorDisplayed(),
                "Password validation error is displayed for short password", softAssert);

        if (passwordErrorDisplayed) {
            String expectedErrorMsg = Messages.getPasswordMinLengthError();
            verifySoftEquals(expectedErrorMsg, accountPage.getPasswordValidationErrorText(),
                    "Password validation error message text", softAssert);
        }
        accountPage.refreshPage();
        verifySoftEquals(accountPage.getPassword(), originalPassword,
                "Password remains unchanged after failed update", softAssert);

        //move this to e2e tests
//        ExtentReportManager.info("Logout and verify login fails with new short password and succeeds with original password");
//        accountPage.topBarNavigation.logout();
//
//        LoginPage loginPage = new LoginPage(getDriver());
//        loginPage.navigateToLoginPage();
//        loginPage.fillLoginFormAndSubmit(originalUsername, shortPassword);
//
//        verifySoftTrue(loginPage.isInvalidPasswordMessageDisplayed(),
//                "Invalid password error displayed for attempted new password", softAssert);
//
//        verifySoftFalse(loginPage.topBarNavigation.isUserProfileVisible(),
//                "Login failed with attempted new password", softAssert);
//
//        loginPage.refreshPage();
//        loginPage.fillLoginFormAndSubmit(originalUsername, originalPassword);
//
//        verifySoftTrue(loginPage.topBarNavigation.isUserProfileVisible(),
//                "Login successful with original password", softAssert);

    }

    private void verifyUsernameRemainsUnchanged(String newUsername, SoftAssert softAssert) {
        accountPage.refreshPage();
        String currentUsername = accountPage.getUsername();
        verifySoftEquals(currentUsername, originalUsername,
                "Username displayed in form remains unchanged after update attempt", softAssert);

        accountPage.topBarNavigation.logout();
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigateToLoginPage();
        loginPage.fillLoginFormAndSubmit(newUsername, originalPassword);

        verifySoftTrue(loginPage.isLoginErrorMessageDisplayed(),
                "Login error displayed for attempted new username", softAssert);

        verifySoftFalse(loginPage.topBarNavigation.isUserProfileVisible(),
                "Login failed with attempted new username", softAssert);

        loginPage.refreshPage();
        loginPage.fillLoginFormAndSubmit(originalUsername, originalPassword);

        verifySoftTrue(loginPage.topBarNavigation.isUserProfileVisible(),
                "Login successful with original username", softAssert);
    }

    private void revertAccountChanges(String originalName, String originalEmail, String originalPhone) {
        accountPage.refreshPage();  // why does the form behave weirdly without refresh? (need to clear field twice, need to input/clear first letter twice for it to work)

        if (originalName == null || originalName.trim().isEmpty()) {
            ExtentReportManager.fail("Original name is empty. Keeping updated name.");
        } else accountPage.changeName(originalName);

        if (originalEmail == null || originalEmail.trim().isEmpty()) {
            ExtentReportManager.fail("Original email is empty. Keeping updated email.");
        } else accountPage.changeEmail(originalEmail);

        if (originalPhone == null || originalPhone.trim().isEmpty()) {
            ExtentReportManager.fail("Original phone number is empty. Keeping updated phone number.");
        } else accountPage.changePhoneNumber(originalPhone);

        accountPage.saveChanges();
        accountPage.waitForUpdateAlert();
    }

    private void revertPasswordChange(String originalPassword) {
        accountPage.navigateToAccountPage();
        accountPage.changePassword(originalPassword);

        accountPage.saveChanges();
        accountPage.waitForUpdateAlert();
    }

}
