package helpers.verifications;

import helpers.providers.MessagesProvider;
import model.enums.LoginField;
import org.openqa.selenium.WebDriver;
import org.testng.asserts.SoftAssert;
import pages.CommonPage;
import pages.LoginPage;

import static helpers.verifications.SoftAssertionHelper.*;

/**
 * Helper class for common authentication-related verifications.
 * Reduces code duplication across component, integration, and E2E tests.
 *
 * <p>Handles authentication flows: login and logout.
 *
 */
public class AuthVerificationHelper {

    /**
     * Verify login success - message displayed, correct text, and user is in logged-in state.
     * Uses SoftAssertionHelper to automatically capture screenshots on each failed soft assertion.
     */
    public static void verifyLoginSuccess(LoginPage loginPage, WebDriver driver, SoftAssert softAssert) {
        // Verify login success message displayed
        boolean isSuccessAlertDisplayed = loginPage.isLoginSuccessDialogDisplayed();
        verifySoftTrue(isSuccessAlertDisplayed,
                "Login success alert is displayed", driver, softAssert);

        // Verify success message text (only if alert is displayed)
        if (isSuccessAlertDisplayed) {
            String expectedMsg = MessagesProvider.getLoginSuccessMessage();
            String actualMsg = loginPage.getLoginSuccessMsgText();
            verifySoftEquals(actualMsg, expectedMsg,
                    "Login success message text", driver, softAssert);
        }

        // Verify user is logged in
        boolean isLoggedIn = loginPage.topBarNavigation.isUserProfileVisible();
        verifySoftTrue(isLoggedIn,
                "User profile is visible (logged in)", driver, softAssert);
    }

    /**
     * Verify that the user button in the top bar displays the correct user's name.
     * Works with ANY page since all pages extend CommonPage which has topBarNavigation.
     *
     * @param page         Any page object (HomePage, AccountPage, LoginPage, etc.) - all have topBarNavigation
     * @param expectedName The expected fullname to be displayed
     * @param driver       WebDriver instance for screenshot capture
     * @param softAssert   The SoftAssert instance for accumulating assertions
     */
    public static void verifyUserButtonDisplaysCorrectName(CommonPage page, String expectedName, WebDriver driver, SoftAssert softAssert) {
        String actualName = page.topBarNavigation.getUserProfileName();
        verifySoftEquals(actualName, expectedName,
                "User profile name displayed in top bar", driver, softAssert);
    }

    /**
     * Verify logout success - alert visible, login link appears, user profile disappears.
     * Works with ANY page since all pages extend CommonPage which has topBarNavigation.
     *
     * @param page       Any page object (HomePage, AccountPage, LoginPage, etc.) - all have topBarNavigation
     * @param driver     WebDriver instance for screenshot capture
     * @param softAssert The SoftAssert instance for accumulating assertions
     */
    public static void verifyLogoutSuccess(CommonPage page, WebDriver driver, SoftAssert softAssert) {
        verifySoftTrue(page.topBarNavigation.isLogoutSuccessAlertVisible(),
                "Logout success alert is visible", driver, softAssert);

        verifySoftTrue(page.topBarNavigation.isLoginLinkVisible(),
                "Login link should be visible after logout", driver, softAssert);

        verifySoftFalse(page.topBarNavigation.isUserProfileVisible(),
                "User profile should not be visible after logout", driver, softAssert);
    }

    public static void verifyInvalidCredentialsLoginError(LoginPage loginPage, WebDriver driver, SoftAssert softAssert) {
        // Verify login error alert displayed
        boolean alertDisplayed = verifySoftTrue(loginPage.isLoginErrorAlertDisplayed(),
                "Login error alert is displayed", driver, softAssert);

        // Verify error message text (only if alert is displayed)
        if (alertDisplayed) {
            String expectedMsg = MessagesProvider.getLoginErrorMessage();
            String actualMsg = loginPage.getLoginErrorMsgText();
            verifySoftEquals(actualMsg, expectedMsg, "Login error alert text", driver, softAssert);
        }

        // Verify user is not logged in
        verifySoftFalse(loginPage.topBarNavigation.isUserProfileVisible(),
                "User profile link should not be visible", driver, softAssert);
    }

    public static void verifyEmptyFieldValidationMsg(LoginPage loginPage, LoginField missingField, WebDriver driver, SoftAssert softAssert) {
        boolean errorMsgDisplayed = verifySoftTrue(loginPage.isValidationMessageDisplayed(missingField),
                "Password error message is displayed", driver, softAssert);

        // Only verify text if error message is displayed
        if (errorMsgDisplayed) {
            String expectedMsg = MessagesProvider.getRequiredFieldError();
            String actualMsg = loginPage.getFieldValidationText(missingField);
            verifySoftEquals(actualMsg, expectedMsg, "Empty password error message text", driver, softAssert);
        }

        // Verify user is not logged in
        verifySoftFalse(loginPage.topBarNavigation.isUserProfileVisible(),
                "User profile link should not be visible", driver, softAssert);
    }

}