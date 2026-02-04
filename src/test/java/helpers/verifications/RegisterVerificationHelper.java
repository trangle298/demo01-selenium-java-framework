package helpers.verifications;

import helpers.providers.MessagesProvider;
import model.enums.RegisterField;
import org.openqa.selenium.WebDriver;
import org.testng.asserts.SoftAssert;
import pages.RegisterPage;

import static helpers.verifications.SoftAssertionHelper.verifySoftEquals;
import static helpers.verifications.SoftAssertionHelper.verifySoftTrue;

public class RegisterVerificationHelper {

    /**
     * Verify registration success message is displayed and has correct text.
     */
    public static void verifyRegisterSuccessMsg(RegisterPage registerPage, WebDriver driver, SoftAssert softAssert) {
        boolean isAlertDisplayed = registerPage.isRegisterSuccessDialogDisplayed();
        verifySoftTrue(isAlertDisplayed, "Register success message is displayed", driver, softAssert);

        if (isAlertDisplayed) {
            String expectedMessage = MessagesProvider.getRegisterSuccessMessage();
            String actualMessage = registerPage.getRegisterSuccessMsgText();
            verifySoftEquals(actualMessage, expectedMessage,
                    "Register success message text",
                    driver, softAssert);
        }
    }

    public static void verifyRegisterFieldValidationMsg(RegisterPage registerPage, RegisterField fieldType, String expectedMsg, WebDriver driver, SoftAssert softAssert) {
        boolean errorDisplayed = registerPage.isFieldValidationMsgDisplayed(fieldType);
        verifySoftTrue(errorDisplayed, fieldType + " field error is displayed", driver, softAssert);

        if (errorDisplayed) {
            String actualMsg = registerPage.getFieldValidationText(fieldType);
            SoftAssertionHelper.verifySoftEquals(actualMsg, expectedMsg, fieldType + " error message text", driver, softAssert);
        }
    }

    public static void verifyRegisterFieldValidationMsgNotDisplayed(RegisterPage registerPage, RegisterField fieldType, WebDriver driver, SoftAssert softAssert) {
        boolean isMsgDisappeared = registerPage.isFieldValidationMsgNotDisplayed(fieldType);
        verifySoftTrue(isMsgDisappeared, fieldType + " field error is not displayed", driver, softAssert);
    }

    public static void verifyRegisterFormErrorAlert(RegisterPage registerPage, String expectedMsg, WebDriver driver, SoftAssert softAssert) {
        // Verify error alert is displayed
        boolean errorDisplayed = SoftAssertionHelper.verifySoftTrue(registerPage.isRegisterErrorAlertDisplayed(),
                "Register error alert is displayed", driver, softAssert);

        // Verify error message text
        if (errorDisplayed) {
            String actualMsg = registerPage.getRegisterErrorMsgText();
            SoftAssertionHelper.verifySoftEquals(actualMsg, expectedMsg, "Register form error message text", driver, softAssert);
        }
    }
}
