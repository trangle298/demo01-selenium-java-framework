package testcases.registration;

import base.BaseTest;
import helpers.verifications.AccountVerificationHelper;
import helpers.verifications.RegisterVerificationHelper;
import model.ui.RegisterDataUI;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import pages.RegisterPage;
import reports.ExtentReportManager;

import static helpers.providers.UserAccountTestDataGenerator.*;

public class TC01_RegisterWithValidInputsTest extends BaseTest {

    @Test(description = "Test Successful Registration")
    public void testRegisterSuccessWithValidInputs() {

        RegisterPage registerPage = new RegisterPage(getDriver());
        SoftAssert softAssert = new SoftAssert();

        // Generate and use valid inputs to submit register form
        RegisterDataUI formInputs = generateValidRegisterFormInputs();
        // Confirm that account username is unique
        AccountVerificationHelper.verifyAccountExistence(formInputs.getUsername(), false, softAssert);

        ExtentReportManager.info("Navigate to Register page");
        registerPage.navigateToRegisterPage();

        ExtentReportManager.info("Submit Register form with valid data");
        registerPage.fillRegisterFormThenSubmit(formInputs);

        // Verify success message
        ExtentReportManager.info("Verify Register Success message");
        RegisterVerificationHelper.verifyRegisterSuccessMsg(registerPage, getDriver(), softAssert);

        // Verify account is created
        ExtentReportManager.info("Verify account is created in backend");
        AccountVerificationHelper.verifyAccountExistence(formInputs.getUsername(), true, softAssert);

        softAssert.assertAll();
    }
}