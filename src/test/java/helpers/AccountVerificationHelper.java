package helpers;

import model.RegisterRequest;
import org.openqa.selenium.WebDriver;
import org.testng.asserts.SoftAssert;
import pages.AccountPage;
import pages.LoginPage;
import reports.ExtentReportManager;

import static helpers.AuthVerificationHelper.verifyLoginSuccess;
import static helpers.SoftAssertionHelper.verifySoftEquals;

/**
 * Helper class for account management verifications.
 * Handles profile updates, password update and account information validation
 * and verify that displayed data matches expected values.
 * 
 * <p>Uses soft assertions for multiple related checks and E2E test compatibility.
 */
public class AccountVerificationHelper {

    /**
     * Verify that account data displayed on AccountPage matches the data used during registration.
     *
     * @param accountPage The AccountPage instance
     * @param registerRequest The RegisterRequest containing expected registration data
     * @param driver WebDriver instance for screenshot capture
     * @param softAssert The SoftAssert instance for accumulating assertions
     */
    public static void verifyAccountDataMatchesRegisterData(AccountPage accountPage, RegisterRequest registerRequest, WebDriver driver, SoftAssert softAssert) {
        verifySoftEquals(accountPage.getUsername(), registerRequest.getUsername(), "Account full name after registration", driver, softAssert);
        verifySoftEquals(accountPage.getEmail(), registerRequest.getEmail(), "Account email after registration", driver, softAssert);
        verifySoftEquals(accountPage.getPassword(), registerRequest.getPassword(), "Password after registration", driver, softAssert);
        verifySoftEquals(accountPage.getFullName(), registerRequest.getFullName(), "Account full name after registration", driver, softAssert);
    }

    /**
     * Verify account update success message is displayed with correct text.
     *
     * @param accountPage The AccountPage instance
     * @param driver WebDriver instance for screenshot capture
     * @param softAssert The SoftAssert instance for accumulating assertions
     */
    public static void verifyAccountUpdateSuccessMsg(AccountPage accountPage, WebDriver driver, SoftAssert softAssert) {
        String expectedMsg = Messages.getAccountUpdateSuccessMessage();
        String actualMsg = accountPage.getUpdateAlertText();

        verifySoftEquals(actualMsg, expectedMsg, "Account update success message text", driver, softAssert);
        accountPage.waitForUpdateAlertToDisappear();
    }

    /**
     * Verify that the account info: name, email, phone is updated correctly - success message displayed and new value matches expected.
     *
     * @param accountPage The AccountPage instance
     * @param expectedFullName The expected full name after update
     * @param expectedEmail The expected email after update
     * @param expectedPhoneNr The expected phone number after update
     * @param driver WebDriver instance for screenshot capture
     * @param softAssert The SoftAssert instance for accumulating assertions
     */
    public static void verifyAllAccountInfoUpdateSuccess(AccountPage accountPage, String expectedFullName, String expectedEmail, String expectedPhoneNr, WebDriver driver, SoftAssert softAssert) {
        verifyAccountUpdateSuccessMsg(accountPage, driver, softAssert);

        accountPage.refreshPage();
        String actualFullName = accountPage.getFullName();
        verifySoftEquals(actualFullName, expectedFullName, "Account full name after update", driver, softAssert);
        
        String actualEmail = accountPage.getEmail();
        verifySoftEquals(actualEmail, expectedEmail, "Account email after update", driver, softAssert);
        
        String actualPhoneNr = accountPage.getPhoneNumber();
        verifySoftEquals(actualPhoneNr, expectedPhoneNr, "Account phone number after update", driver, softAssert);
    }

    /**
     * Verify that the password is updated correctly - success message displayed, new password matches expected, and login with new password is successful.
     *
     * @param accountPage The AccountPage instance
     * @param expectedPassword The expected password after update
     * @param driver WebDriver instance for screenshot capture
     * @param softAssert The SoftAssert instance for accumulating assertions
     */
    public static void verifyPasswordUpdateSuccess(AccountPage accountPage, String expectedPassword, WebDriver driver, SoftAssert softAssert) {
        verifyAccountUpdateSuccessMsg(accountPage, driver, softAssert);
        String username = accountPage.getUsername();

        accountPage.refreshPage();
        String actualPhoneNr = accountPage.getPassword();
        verifySoftEquals(actualPhoneNr, expectedPassword, "Password after update", driver, softAssert);

        ExtentReportManager.info("Logout and verify login with new password");
        accountPage.topBarNavigation.logout();
        AuthVerificationHelper.verifyLogoutSuccess(accountPage, driver, softAssert);

        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateToLoginPage();
        loginPage.fillLoginFormAndSubmit(username, expectedPassword);
        verifyLoginSuccess(loginPage, driver, softAssert);
    }

}

