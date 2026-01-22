package helpers.verifications;

import helpers.providers.MessagesProvider;
import model.UserAccount;
import model.ui.RegisterInputs;
import org.openqa.selenium.WebDriver;
import org.testng.asserts.SoftAssert;
import pages.AccountPage;
import pages.LoginPage;
import reports.ExtentReportManager;

import static helpers.verifications.SoftAssertionHelper.verifySoftTrue;
import static helpers.verifications.AuthVerificationHelper.verifyLoginSuccess;
import static helpers.verifications.SoftAssertionHelper.verifySoftEquals;

/**
 * Helper class for account management verifications.
 * Handles profile updates, password update and account information validation
 * and verify that displayed data matches expected values.
 * 
 * <p>Uses soft assertions for multiple related checks and E2E test compatibility.
 */
public class AccountVerificationHelper {

    /**
     * Verify account update success message is displayed with correct text.
     */
    public static void verifyAccountUpdateSuccessDialog(AccountPage accountPage, WebDriver driver, SoftAssert softAssert) {
        boolean isDialogDisplayed = accountPage.isUpdateResponseDialogDisplayed();
        verifySoftTrue(isDialogDisplayed, "Account update success dialog displayed", driver, softAssert);

        if (isDialogDisplayed) {
            String expectedMsg = MessagesProvider.getAccountUpdateSuccessMessage();
            String actualMsg = accountPage.getUpdateResponseMsgText();

            verifySoftEquals(actualMsg, expectedMsg, "Account update success message text", driver, softAssert);
            accountPage.closeSuccessDialog();
        }
    }

    /**
     * Verify that the account info: name, email, phone is updated correctly
     * - success message displayed and new value matches expected.
     *
     * @param accountPage The AccountPage instance
     * @param expectedFullName The expected full name after update
     * @param expectedEmail The expected email after update
     * @param expectedPhoneNr The expected phone number after update
     * @param driver WebDriver instance for screenshot capture
     * @param softAssert The SoftAssert instance for accumulating assertions
     */
    public static void verifyPersonalInfoUpdateSuccess(AccountPage accountPage, String expectedFullName, String expectedEmail, String expectedPhoneNr, WebDriver driver, SoftAssert softAssert) {
        verifyAccountUpdateSuccessDialog(accountPage, driver, softAssert);

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
        verifyAccountUpdateSuccessDialog(accountPage, driver, softAssert);
        String username = accountPage.getUsername();

        accountPage.refreshPage();
        String actualPhoneNr = accountPage.getPassword();
        verifySoftEquals(actualPhoneNr, expectedPassword, "Password after update", driver, softAssert);

        ExtentReportManager.info("Logout and verify login with new password");
        accountPage.topBarNavigation.logout();
        AuthVerificationHelper.verifyLogoutSuccess(accountPage, driver, softAssert);

        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateToLoginPage();
        loginPage.fillLoginFormThenSubmit(username, expectedPassword);
        verifyLoginSuccess(loginPage, driver, softAssert);
    }

    /**
     * Verify that account details (from UI or API) matches expected registration inputs.
     * This generalized method works for BOTH UI and API data verification.
     *
     * @param expectedData The registration data submitted via UI
     * @param actualData   The user account data from UI (AccountPage.getAccountData()) or API (UserService.getUserDetails())
     * @param source       Description of data source (e.g., "Backend API", "Account Page")
     * @param driver       WebDriver instance for screenshot capture
     * @param softAssert   The SoftAssert instance for accumulating assertions
     */
    public static void verifyAccountDataMatchesRegistration(
            RegisterInputs expectedData,
            UserAccount actualData,
            String source,
            WebDriver driver,
            SoftAssert softAssert
    ) {
        verifySoftEquals(actualData.getTaiKhoan(), expectedData.getUsername(),
                "Username in " + source, driver, softAssert);
        verifySoftEquals(actualData.getHoTen(), expectedData.getFullName(),
                "Full name in " + source, driver, softAssert);
        verifySoftEquals(actualData.getEmail(), expectedData.getEmail(),
                "Email in " + source, driver, softAssert);
        verifySoftEquals(actualData.getMatKhau(), expectedData.getPassword(),
                "Password in " + source, driver, softAssert);
    }
}