package helpers.verifications;

import api.services.UserService;
import helpers.actions.AuthActionHelper;
import helpers.providers.MessagesProvider;
import model.UserAccount;
import model.enums.AccountDataField;
import model.enums.UserType;
import model.ui.RegisterDataUI;
import org.openqa.selenium.WebDriver;
import org.testng.asserts.SoftAssert;
import pages.AccountPage;
import pages.LoginPage;
import reports.ExtentReportManager;

import static helpers.verifications.AuthVerificationHelper.verifyInvalidCredentialsLoginError;
import static helpers.verifications.SoftAssertionHelper.verifySoftTrue;
import static helpers.verifications.AuthVerificationHelper.verifyLoginSuccess;
import static helpers.verifications.SoftAssertionHelper.verifySoftEquals;

/**
 * Helper class for account management verifications.
 * Handles account information, user info updates, password update validation
 */
public class AccountVerificationHelper {

    // ================================================
    // ---- Verify Account existence, account info ----
    // ================================================

    /**
     * Verify that a user account exists or does not exist in the backend
     *
     * @param username          Unique identifier used for API request to find user account
     * @param expectedExistence Expected condition to match: account exists = true, account does not exist = false
     * @param softAssert        The SoftAssert instance for accumulating assertions
     */
    public static void verifyAccountExistence(String username, boolean expectedExistence, SoftAssert softAssert) {
        UserService userService = new UserService();
        boolean isAccountExisting = userService.isAccountExisting(username);
        verifySoftEquals(isAccountExisting, expectedExistence,
                "Expected account existence to be " + expectedExistence, softAssert);
    }

    /**
     * Verify that account details (fetched from UI or API) matches expected registration data from UI form submission.
     * This generalized method works for BOTH UI and API data verification.
     *
     * @param expectedData The registration data submitted via UI
     * @param actualData   The user account data from UI (AccountPage.getAccountData()) or API (UserService.getUserDetails())
     * @param source       Description of actualData source (e.g., "Backend API", "Account Page")
     * @param driver       WebDriver instance for screenshot capture
     * @param softAssert   The SoftAssert instance for accumulating assertions
     */
    public static void verifyAccountDataMatchesUIRegistration(
            RegisterDataUI expectedData,
            UserAccount actualData,
            String source,
            WebDriver driver,
            SoftAssert softAssert
    ) {
        verifySoftEquals(actualData.getUsername(), expectedData.getUsername(),
                "Username in " + source, driver, softAssert);
        verifySoftEquals(actualData.getFullName(), expectedData.getFullName(),
                "Full name in " + source, driver, softAssert);
        verifySoftEquals(actualData.getEmail(), expectedData.getEmail(),
                "Email in " + source, driver, softAssert);
        verifySoftEquals(actualData.getPassword(), expectedData.getPassword(),
                "Password in " + source, driver, softAssert);
        verifySoftEquals(actualData.getUserType(), UserType.CUSTOMER.getLabel(),
                "User Type in " + source, driver, softAssert);
    }

    // ==========================================
    // ---- Verify account info update success & failure ----
    // ==========================================
    /**
     * Verify that the account info: name, email, phone is updated correctly
     * - success message displayed and new value matches expected.
     *
     * @param accountPage The AccountPage instance
     * @param updatedFullName The expected full name after update
     * @param updatedEmail The expected email after update
     * @param updatedPhoneNr The expected phone number after update
     * @param driver WebDriver instance for screenshot capture
     * @param softAssert The SoftAssert instance for accumulating assertions
     */
    public static void verifyUserInfoUpdateSuccessOnUI(
            AccountPage accountPage,
            String updatedFullName,
            String updatedEmail,
            String updatedPhoneNr,
            WebDriver driver,
            SoftAssert softAssert
    ) {

        ExtentReportManager.info("Verify update success dialog displays with correct text");
        verifyAccountUpdateSuccessDialog(accountPage, driver, softAssert);

        ExtentReportManager.info("Refresh page and Verify account page displays updated values");
        accountPage.refreshPage();
        UserAccount uiAccount = accountPage.getAccountData();

        verifySoftEquals(uiAccount.getFullName(), updatedFullName,
                "Account full name after update", driver, softAssert);
        verifySoftEquals(uiAccount.getEmail(), updatedEmail,
                "Account email after update", driver, softAssert);
        verifySoftEquals(uiAccount.getPhoneNumber(), updatedPhoneNr,
                "Account phone number after update", driver, softAssert);
    }

    /**
     * Verify that the account info: name, email, phone is updated correctly in backend
     *
     * @param apiUserAccount The user info fetched from API
     * @param updatedFullName The expected full name after update
     * @param updatedEmail The expected email after update
     * @param updatedPhoneNr The expected phone number after update
     * @param softAssert The SoftAssert instance for accumulating assertions
     */
    public static void verifyUserInfoUpdateSuccessInBackend(
            UserAccount apiUserAccount,
            String updatedFullName,
            String updatedEmail,
            String updatedPhoneNr,
            SoftAssert softAssert
    ) {
        verifySoftEquals(apiUserAccount.getFullName(), updatedFullName,
                "Account full name in backend", softAssert);
        verifySoftEquals(apiUserAccount.getEmail(), updatedEmail,
                "Account email in backend", softAssert);
        verifySoftEquals(apiUserAccount.getPhoneNumber(), updatedPhoneNr,
                "Account phone number in backend", softAssert);
    }

    /**
     * Verify that the password is updated correctly - success message displayed, new password matches expected, and login with new password is successful.
     *
     * @param accountPage The AccountPage instance
     * @param username  The username of test user
     * @param oldPassword The previous password before updating
     * @param newPassword The expected password after update
     * @param driver WebDriver instance for screenshot capture
     * @param softAssert The SoftAssert instance for accumulating assertions
     */
    public static void verifyPasswordUpdateSuccess(
            AccountPage accountPage,
            String username,
            String oldPassword,
            String newPassword,
            WebDriver driver,
            SoftAssert softAssert
    ) {

        ExtentReportManager.info("Verify update success dialog displays with correct text");
        verifyAccountUpdateSuccessDialog(accountPage, driver, softAssert);

        AuthActionHelper.logout(accountPage);

        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateToLoginPage();

        ExtentReportManager.info("Verify login with old password failed");
        loginPage.fillLoginFormThenSubmit(username, oldPassword);
        verifyInvalidCredentialsLoginError(loginPage, driver, softAssert);

        ExtentReportManager.info("Verify login with new password is successful");
        loginPage.refreshPage();
        loginPage.fillLoginFormThenSubmit(username, newPassword);
        verifyLoginSuccess(loginPage, driver, softAssert);
    }

    public static void verifyUpdateFailsDueToFieldValidation(
            AccountPage accountPage,
            AccountDataField errorField,
            String expectedMsg,
            UserAccount originalUserData,
            WebDriver driver,
            SoftAssert softAssert
    ) {
        ExtentReportManager.info("Verify validation message displays with correct text");
        boolean isValidationMsgDisplayed = accountPage.isFieldValidationMsgDisplayed(errorField);
        verifySoftTrue(isValidationMsgDisplayed, "Validation message is displayed for field: " + errorField, driver, softAssert);

        if (isValidationMsgDisplayed) {
            String actualMsg = accountPage.getValidationMsgText(errorField);
            SoftAssertionHelper.verifySoftEquals(actualMsg, expectedMsg,  "Full name validation message text", driver, softAssert);
        }

        verifyDisplayedUserInfoRemainUnchanged(accountPage, originalUserData, driver, softAssert);
    }

    public static void verifyUpdateFailsDueToExistingEmailError(
            AccountPage accountPage,
            UserAccount originalUserData,
            WebDriver driver,
            SoftAssert softAssert
    ) {
        ExtentReportManager.info("Verify update error alert dialog displays with correct text");
        boolean isDialogDisplayed = accountPage.isUpdateResponseDialogDisplayed();
        verifySoftTrue(isDialogDisplayed, "Update response dialog is displayed", driver, softAssert);

        if (isDialogDisplayed) {
            String actualMsg = accountPage.getUpdateResponseMsgText();
            String expectedMsg = MessagesProvider.getAccountUpdateExistingEmailError();
            SoftAssertionHelper.verifySoftEquals(actualMsg, expectedMsg,  "Dialog text for existing email error", driver, softAssert);
        }

        verifyDisplayedUserInfoRemainUnchanged(accountPage, originalUserData, driver, softAssert);
    }

    // =================================
    // ---- Private Helper methods ----
    // =================================

    // Verify account update success message is displayed with correct text
    private static void verifyAccountUpdateSuccessDialog(AccountPage accountPage, WebDriver driver, SoftAssert softAssert) {
        boolean isDialogDisplayed = accountPage.isUpdateResponseDialogDisplayed();
        verifySoftTrue(isDialogDisplayed, "Account update success dialog displayed", driver, softAssert);

        if (isDialogDisplayed) {
            String expectedMsg = MessagesProvider.getAccountUpdateSuccessMessage();
            String actualMsg = accountPage.getUpdateResponseMsgText();

            verifySoftEquals(actualMsg, expectedMsg, "Account update success message text", driver, softAssert);
            accountPage.closeSuccessDialog();
        }
    }

    private static void verifyDisplayedUserInfoRemainUnchanged(AccountPage accountPage, UserAccount originalUserData, WebDriver driver, SoftAssert softAssert ) {
        ExtentReportManager.info("Refresh page and Verify account page displays original values");
        accountPage.refreshPage();
        UserAccount uiAccount = accountPage.getAccountData();

        verifySoftEquals(uiAccount, originalUserData,
                "Account page displayed user info", driver, softAssert);
    }
}