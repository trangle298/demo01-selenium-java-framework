package testcases.account;

import base.BaseTest;
import helpers.verifications.AccountVerificationHelper;
import helpers.providers.AuthTestDataGenerator;
import helpers.providers.MessagesProvider;
import helpers.providers.TestUserProvider;
import model.TestUserType;
import model.ui.LoginInputs;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.AccountPage;
import pages.LoginPage;
import reports.ExtentReportManager;

import static helpers.verifications.SoftAssertionHelper.*;

public class AccountUpdateTest extends BaseTest {

    private AccountPage accountPage;
    private LoginInputs userCredentials;
    private SoftAssert softAssert;

    // store originals so @AfterMethod can revert changes
    private String originalUsername;
    private String originalName;
    private String originalEmail;
    private String originalPhone;
    private String originalPassword;

    @BeforeMethod(alwaysRun = true)
    public void setUpMethod() {
        LoginPage loginPage = new LoginPage(getDriver());
        softAssert = new SoftAssert();

        ExtentReportManager.info("Log in and navigate to Account page before test");
        userCredentials = TestUserProvider.getUserCredentials(TestUserType.USER_ACCOUNT_UPDATE);
        loginPage.navigateToLoginPage();
        loginPage.fillLoginFormThenSubmit(userCredentials);
        loginPage.topBarNavigation.waitForUserProfileLink();

        // navigate to account page and capture current values for revert
        accountPage = new AccountPage(getDriver());

        accountPage.navigateToAccountPage();
        originalUsername = accountPage.getUsername();
        originalName = accountPage.getFullName();
        originalEmail = accountPage.getEmail();
        originalPhone = accountPage.getPhoneNumber();
        originalPassword = accountPage.getPassword();
    }

    @Test(groups = {"integration", "auth", "account", "smoke"})
    public void testValidUpdate_PersonalInfo() {
        ExtentReportManager.info("Update user profile: new name, email, phone on Account Page");
        // Generate new valid data
        String newName = AuthTestDataGenerator.generateNewName(originalName);
        String newEmail = AuthTestDataGenerator.generateNewUniqueEmail();
        String newPhone = AuthTestDataGenerator.generateNewPhoneNumber(originalPhone);
        // Update account info
        accountPage.changeAccountInfo(newName, newEmail, newPhone);
        accountPage.clickSaveBtn();

        // Verify update success
        ExtentReportManager.info("Verify user profile info update success");
        AccountVerificationHelper.verifyPersonalInfoUpdateSuccess(accountPage, newName, newEmail, newPhone, getDriver(), softAssert);

        softAssert.assertAll();

        // Revert changes
        ExtentReportManager.info("Reverting changes after test");
        revertAccountChanges(originalName, originalEmail, originalPhone);
    }

    @Test(groups = {"integration", "auth", "account", "smoke", "critical"})
    public void testValidUpdate_Password() {
        ExtentReportManager.info("Update password on Account Page");
        // Generate new valid password
        String newPassword = AuthTestDataGenerator.generateNewPassword(originalPassword);

        // Update password
        accountPage.changePassword(newPassword);
        accountPage.clickSaveBtn();

        // Verify update success
        ExtentReportManager.info("Verify password update success");
        AccountVerificationHelper.verifyPasswordUpdateSuccess(accountPage, newPassword,getDriver(), softAssert);

        softAssert.assertAll();

        // Revert changes
        ExtentReportManager.info("Reverting changes after test");
        revertPasswordChange(originalPassword);
    }

    @Test(groups = {"component", "auth", "account", "negative"})
    public void testInvalidUpdate_BlankEmail() {
        ExtentReportManager.info("Attempt to update account with blank email");
        // Set email to blank and save
        accountPage.changeEmail("");
        accountPage.clickSaveBtn();

        // Verify update failure due to validation
        ExtentReportManager.info("Verify update failed due to blank field validation");
        verifyUpdateFailureWithBlankEmail(softAssert);

        softAssert.assertAll();
    }

    @Test(groups = {"component", "auth", "account", "negative"})
    public void testInvalidUpdate_InvalidName() {
        // Generate invalid name containing numbers
        String invalidName = AuthTestDataGenerator.generateInvalidNameContainingNumbers();
        ExtentReportManager.info("Attempt to update account with invalid name: " + invalidName);

        // Set invalid name and save
        accountPage.changeName(invalidName);
        accountPage.clickSaveBtn();

        // Verify update failure due to validation
        ExtentReportManager.info("Verify update failed due to invalid input validation");
        verifyUpdateFailureWithInvalidName(softAssert);

        softAssert.assertAll();
    }

    @Test(groups = {"component", "auth", "account", "negative", "critical"})
    public void testInvalidUpdate_ShortPassword() {
        // Generate short invalid password
        String shortPassword = AuthTestDataGenerator.generateInvalidShortPassword();
        ExtentReportManager.info("Attempt to update password with short password: " + shortPassword);

        // Set short password and save
        accountPage.changePassword(shortPassword);
        accountPage.clickSaveBtn();

        // Verify update failure due to validation
        ExtentReportManager.info("Verify update failed due to short password validation");
        verifyUpdateFailureWithShortPassword(softAssert);

        softAssert.assertAll();
    }

    @Test(groups = {"component", "auth", "account", "negative", "critical"})
    public void testUsernameReadonly() {
        ExtentReportManager.info("Attempt to change username (should be read-only)");
        // Generate new username
        String newUsername = AuthTestDataGenerator.generateValidRegisterFormInputs().getUsername();

        // Attempt to change username and save
        accountPage.changeUsername(newUsername);
        accountPage.clickSaveBtn();

        // Verify username remains unchanged
        ExtentReportManager.info("Verify username remains unchanged after update attempt");
        verifyUsernameRemainsUnchanged(newUsername, softAssert);

        softAssert.assertAll();
    }

    // ---- Helper methods for verification ----
    private void verifyUpdateFailureWithBlankEmail(SoftAssert softAssert) {
        verifySoftFalse(accountPage.isUpdateResponseDialogDisplayed(),
                "No update alert displayed for failed update with blank email", getDriver(), softAssert);

        boolean emailErrorDisplayed = verifySoftTrue(accountPage.isEmailValidationMsgDisplayed(),
                "Email validation error is displayed for blank email", getDriver(), softAssert);

        if (emailErrorDisplayed) {
            String expectedErrorMsg = MessagesProvider.getRequiredFieldError();
            verifySoftEquals(expectedErrorMsg, accountPage.getEmailValidationMsgText(),
                    "Email validation error message text", getDriver(), softAssert);
        }

        accountPage.refreshPage();
        String currentEmail = accountPage.getEmail();

        verifySoftEquals(currentEmail, originalEmail,
                "Unchanged email after failed update", getDriver(), softAssert);
    }

    private void verifyUpdateFailureWithInvalidName(SoftAssert softAssert) {
        verifySoftFalse(accountPage.isUpdateResponseDialogDisplayed(),
                "No update alert displayed for failed update with invalid name", getDriver(), softAssert);

        boolean nameErrorDisplayed = verifySoftTrue(accountPage.isNameValidationMsgDisplayed(),
                "Name validation error is displayed for invalid name", getDriver(), softAssert);

        if (nameErrorDisplayed) {
            String expectedErrorMsg = MessagesProvider.getNameContainsNumberError();
            verifySoftEquals(expectedErrorMsg, accountPage.getNameValidationMsgText(),
                    "Name validation error message text", getDriver(), softAssert);
        }

        accountPage.refreshPage();
        verifySoftEquals(accountPage.getFullName(), originalName,
                "Full Name remains unchanged after failed update", getDriver(), softAssert);
    }

    private void verifyUpdateFailureWithShortPassword(SoftAssert softAssert) {
        verifySoftFalse(accountPage.isUpdateResponseDialogDisplayed(),
                "No update alert displayed for failed update with invalid password", getDriver(), softAssert);

        boolean passwordErrorDisplayed = verifySoftTrue(accountPage.isPasswordValidationMsgDisplayed(),
                "Password validation error is displayed for short password", getDriver(), softAssert);

        if (passwordErrorDisplayed) {
            String expectedErrorMsg = MessagesProvider.getPasswordMinLengthError();
            verifySoftEquals(expectedErrorMsg, accountPage.getPasswordValidationMsgText(),
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
        loginPage.fillLoginFormThenSubmit(newUsername, originalPassword);

        verifySoftTrue(loginPage.isLoginErrorAlertDisplayed(),
                "Login error displayed for attempted new username", getDriver(), softAssert);

        verifySoftFalse(loginPage.topBarNavigation.isUserProfileVisible(),
                "Login failed with attempted new username", getDriver(), softAssert);

        loginPage.refreshPage();
        loginPage.fillLoginFormThenSubmit(originalUsername, originalPassword);

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

        accountPage.saveChangesAndWaitForSuccessDialog();
    }

    private void revertPasswordChange(String originalPassword) {
        accountPage.navigateToAccountPage();
        accountPage.changePassword(originalPassword);

        accountPage.saveChangesAndWaitForSuccessDialog();
    }
}
