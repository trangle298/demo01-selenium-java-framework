package testcases.registration;

import base.BaseTest;
import helpers.providers.MessagesProvider;
import helpers.verifications.AccountVerificationHelper;
import helpers.verifications.RegisterVerificationHelper;
import model.enums.RegisterField;
import model.ui.RegisterDataUI;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import pages.RegisterPage;
import reports.ExtentReportManager;

import static helpers.providers.UserAccountTestDataGenerator.*;

public class TC02_RegisterWithEmptyFieldTest extends BaseTest {

    @Test(description = "Test Blocked Registration With One Empty Field: Email")
    public void testEmptyEmailBlocksRegistration() {
        SoftAssert softAssert = new SoftAssert();
        RegisterPage registerPage = new RegisterPage(getDriver());

        // GenerateEmai form input, double check that generated username is unique
        RegisterDataUI formInputs = generateValidRegisterFormInputs();
        formInputs.setEmail("");
        AccountVerificationHelper.verifyAccountExistence(formInputs.getUsername(), false, softAssert);

        ExtentReportManager.info("Navigate to Register page");
        registerPage.navigateToRegisterPage();

        // Submit register form with empty email
        ExtentReportManager.info("Submit register form with all valid inputs but empty email");
        registerPage.fillRegisterFormThenSubmit(formInputs);

        // Verify validation error message
        ExtentReportManager.info("Verify validation error message");
        String expectedMsg = MessagesProvider.getRequiredFieldError();
        RegisterVerificationHelper.verifyRegisterFieldValidationMsg(registerPage, RegisterField.EMAIL, expectedMsg, getDriver(), softAssert);

        // Verify account (based on username) not created in backend
        ExtentReportManager.info("Verify account is not created in backend");
        AccountVerificationHelper.verifyAccountExistence(formInputs.getUsername(), false, softAssert);

        softAssert.assertAll();
    }
}
