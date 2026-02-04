package testcases.registration;

import base.BaseTest;
import helpers.providers.UserAccountTestDataGenerator;
import helpers.providers.MessagesProvider;
import helpers.verifications.AccountVerificationHelper;
import helpers.verifications.RegisterVerificationHelper;
import model.enums.RegisterField;
import model.ui.RegisterDataUI;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.RegisterPage;
import reports.ExtentReportManager;

import static helpers.providers.UserAccountTestDataGenerator.generateValidRegisterFormInputs;

public class TC03_RegisterWithInvalidInputTest extends BaseTest {

    @Test(description = "Test Blocked Registration With One Invalid Input: Short Password")
    public void testShortPasswordBlocksRegistration() {
        SoftAssert softAssert = new SoftAssert();
        RegisterPage registerPage = new RegisterPage(getDriver());

        // Generate form input, double check that generated username is unique
        RegisterDataUI formInputs = generateValidRegisterFormInputs();
        String shortPassword = UserAccountTestDataGenerator.generateShortPassword();
        formInputs.setPassword(shortPassword);
        formInputs.setConfirmPassword(shortPassword);
        AccountVerificationHelper.verifyAccountExistence(formInputs.getUsername(), false, softAssert);

        ExtentReportManager.info("Navigate to Register page");
        registerPage.navigateToRegisterPage();

        // Submit register form with short password
        ExtentReportManager.info("Submit register form with all valid inputs but short password");
        registerPage.fillRegisterFormThenSubmit(formInputs);

        // Verify validation error message
        ExtentReportManager.info("Verify validation error message");
        String expectedMsg = MessagesProvider.getPasswordMinLengthError();
        RegisterVerificationHelper.verifyRegisterFieldValidationMsg(registerPage, RegisterField.PASSWORD, expectedMsg, getDriver(), softAssert);

        // Verify account (based on username) not created in backend
        ExtentReportManager.info("Verify account is not created in backend");
        AccountVerificationHelper.verifyAccountExistence(formInputs.getUsername(), false, softAssert);

        softAssert.assertAll();
    }
}