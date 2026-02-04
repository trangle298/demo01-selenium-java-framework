package testcases.registration;

import base.BaseTest;
import helpers.providers.MessagesProvider;
import helpers.verifications.RegisterVerificationHelper;
import model.enums.RegisterField;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import pages.RegisterPage;
import reports.ExtentReportManager;

import static helpers.providers.UserAccountTestDataGenerator.*;

public class TC09_PasswordLengthValidationTest extends BaseTest {

    @DataProvider(name = "passwordLengthBoundaries")
    public Object[][] passwordLengthScenarios() {

        // Currently hard code for simplicity => to move to a standalone data file
        Integer minLength = 6;
        Integer maxLength = 50;

        String passwordMin = generatePasswordCustomLength(minLength);
        String passwordBelowMin = generatePasswordCustomLength(minLength-1);
        String passwordMax = generatePasswordCustomLength(maxLength);
        String passwordAboveMax = generatePasswordCustomLength(maxLength+1);

        return new Object[][]{
               {minLength, passwordMin, passwordBelowMin, MessagesProvider.getPasswordMinLengthError(), "Minimum Limit"},
               {maxLength, passwordMax, passwordAboveMax, MessagesProvider.getPasswordMaxLengthError(), "Maximum Limit"},
       };
    }

    @Test(description = "Test Field Validation For Password Min And Max Length Limit",
            dataProvider = "passwordLengthBoundaries")
    public void testPasswordLengthBoundary(Integer lengthLimit, String validLengthPassword, String invalidLengthPassword, String expectedValidationMsg, String scenario) {

        ExtentReportManager.info("Testing password length boundary: " + scenario + " = " + lengthLimit);

        SoftAssert softAssert = new SoftAssert();
        RegisterPage registerPage = new RegisterPage(getDriver());
        registerPage.navigateToRegisterPage();

        ExtentReportManager.info("Fill password input with length exceeding " + scenario);
        registerPage.enterFieldInputAndBlur(RegisterField.PASSWORD, invalidLengthPassword);

        ExtentReportManager.info("Verify password validation message");
        RegisterVerificationHelper.verifyRegisterFieldValidationMsg(registerPage, RegisterField.PASSWORD, expectedValidationMsg, getDriver(), softAssert);

        ExtentReportManager.info("Update password to input with length at " + scenario);
        registerPage.updateFieldInputAndBlur(RegisterField.PASSWORD, validLengthPassword);

        ExtentReportManager.info("Verify password validation message is not displayed");
        RegisterVerificationHelper.verifyRegisterFieldValidationMsgNotDisplayed(registerPage, RegisterField.PASSWORD, getDriver(), softAssert);

        softAssert.assertAll();
    }
}