package testcases.account;

import base.BaseTest;
import helpers.AccountVerificationHelper;
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

import static helpers.SoftAssertionHelper.*;

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
        accountPage.changeAccountInfo(newName, newEmail, newPhone);
        accountPage.saveChanges();

        ExtentReportManager.info("Verify account info update success");
        AccountVerificationHelper.verifyAllAccountInfoUpdateSuccess(accountPage, newName, newEmail, newPhone, getDriver(), softAssert);

        softAssert.assertAll();

        ExtentReportManager.info("Reverting changes after test");
        revertAccountChanges(originalName, originalEmail, originalPhone);
    }

    @Test(groups = {"integration", "auth", "account", "smoke", "critical"})
    public void testValidUpdate_Password() {
        SoftAssert softAssert = new SoftAssert();

        String newPassword = AuthTestDataGenerator.generateNewPassword(originalPassword);

        ExtentReportManager.info("Update password on Account Page");
        accountPage.changePassword(newPassword);
        accountPage.saveChanges();

        ExtentReportManager.info("Verify password update success");
        AccountVerificationHelper.verifyPasswordUpdateSuccess(accountPage, newPassword,getDriver(), softAssert);

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

    @Test(groups = {"component", "auth", "account", "negative", "critical"})
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

    @Test(groups = {"component", "auth", "account", "negative", "critical"})
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

    // ---- Helper methods for verification ----
    private void verifyUpdateFailureWithBlankEmail(SoftAssert softAssert) {
        verifySoftFalse(accountPage.isUpdateAlertDisplayed(),
                "No update alert displayed for failed update with blank email", getDriver(), softAssert);

        boolean emailErrorDisplayed = verifySoftTrue(accountPage.isEmailValidationErrorDisplayed(),
                "Email validation error is displayed for blank email", getDriver(), softAssert);

        if (emailErrorDisplayed) {
            String expectedErrorMsg = Messages.getRequiredFieldError();
            verifySoftEquals(expectedErrorMsg, accountPage.getEmailValidationErrorText(),
                    "Email validation error message text", getDriver(), softAssert);
        }

        accountPage.refreshPage();
        String currentEmail = accountPage.getEmail();

        verifySoftEquals(currentEmail, originalEmail,
                "Unchanged email after failed update", getDriver(), softAssert);
    }

    private void verifyUpdateFailureWithInvalidName(SoftAssert softAssert) {
        verifySoftFalse(accountPage.isUpdateAlertDisplayed(),
                "No update alert displayed for failed update with invalid name", getDriver(), softAssert);

        boolean nameErrorDisplayed = verifySoftTrue(accountPage.isNameValidationErrorDisplayed(),
                "Name validation error is displayed for invalid name", getDriver(), softAssert);

        if (nameErrorDisplayed) {
            String expectedErrorMsg = Messages.getNameContainsNumberError();
            verifySoftEquals(expectedErrorMsg, accountPage.getNameValidationErrorText(),
                    "Name validation error message text", getDriver(), softAssert);
        }

        accountPage.refreshPage();
        verifySoftEquals(accountPage.getFullName(), originalName,
                "Full Name remains unchanged after failed update", getDriver(), softAssert);
    }

    private void verifyUpdateFailureWithShortPassword(SoftAssert softAssert) {
        verifySoftFalse(accountPage.isUpdateAlertDisplayed(),
                "No update alert displayed for failed update with invalid password", getDriver(), softAssert);

        boolean passwordErrorDisplayed = verifySoftTrue(accountPage.isPasswordValidationErrorDisplayed(),
                "Password validation error is displayed for short password", getDriver(), softAssert);

        if (passwordErrorDisplayed) {
            String expectedErrorMsg = Messages.getPasswordMinLengthError();
            verifySoftEquals(expectedErrorMsg, accountPage.getPasswordValidationErrorText(),
                    "Password validation error message text", getDriver(), softAssert);
        }
        accountPage.refreshPage();
        verifySoftEquals(accountPage.getPassword(), originalPassword,
                "Password remains unchanged after failed update", getDriver(), softAssert);
    }

    private void verifyUsernameRemainsUnchanged(String newUsername, SoftAssert softAssert) {
        accountPage.refreshPage();
        String currentUsername = accountPage.getUsername();
        verifySoftEquals(currentUsername, originalUsername,
                "Username displayed in form remains unchanged after update attempt", getDriver(), softAssert);

        accountPage.topBarNavigation.logout();
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigateToLoginPage();
        loginPage.fillLoginFormAndSubmit(newUsername, originalPassword);

        verifySoftTrue(loginPage.isLoginErrorAlertDisplayed(),
                "Login error displayed for attempted new username", getDriver(), softAssert);

        verifySoftFalse(loginPage.topBarNavigation.isUserProfileVisible(),
                "Login failed with attempted new username", getDriver(), softAssert);

        loginPage.refreshPage();
        loginPage.fillLoginFormAndSubmit(originalUsername, originalPassword);

        verifySoftTrue(loginPage.topBarNavigation.isUserProfileVisible(),
                "Login successful with original username", getDriver(), softAssert);
    }

    private void revertAccountChanges(String originalName, String originalEmail, String originalPhone) {
        accountPage.refreshPage();

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
