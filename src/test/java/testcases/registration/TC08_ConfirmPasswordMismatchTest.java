package testcases.registration;

import base.BaseTest;
import helpers.providers.UserAccountTestDataGenerator;
import helpers.providers.MessagesProvider;
import helpers.verifications.RegisterVerificationHelper;
import model.enums.RegisterField;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.RegisterPage;
import reports.ExtentReportManager;

public class TC08_ConfirmPasswordMismatchTest extends BaseTest {

    @DataProvider(name = "mismatchPasswordScenarios")
    public Object[][] mismatchPasswordScenarios() {
        // Generate valid password as base (mixed characters by default)
        String validPassword = UserAccountTestDataGenerator.generateValidRegisterFormInputs().getPassword();
        // Generate mismatch password by removing or adding 1 character from original value
        String differentPassword = UserAccountTestDataGenerator.generateNewPassword(validPassword);
        // Generate mismatch password by converting password to uppercase
        String differentCasingPassword = validPassword.toUpperCase();

        return new Object[][]{
                {validPassword, differentPassword, "Mismatched confirm password"},
                {validPassword, differentCasingPassword, "Mismatched confirm password due to casing"},
        };
    }

    @Test(dataProvider = "mismatchPasswordScenarios")
    public void testConfirmPasswordMismatch(String password, String mismatchedPassword, String scenario) {

        ExtentReportManager.info("Testing confirm password validation: " + scenario);
        SoftAssert  softAssert = new SoftAssert();
        RegisterPage registerPage = new RegisterPage(getDriver());

        ExtentReportManager.info("Fill valid password and mismatched confirm password");
        registerPage.navigateToRegisterPage();
        registerPage.enterFieldInput(RegisterField.PASSWORD, password);
        registerPage.enterFieldInputAndBlur(RegisterField.CONFIRM_PASSWORD, mismatchedPassword);

        ExtentReportManager.info("Verify confirm password validation message");
        String expectedMsg = MessagesProvider.getPasswordMismatchError();
        RegisterVerificationHelper.verifyRegisterFieldValidationMsg(registerPage, RegisterField.CONFIRM_PASSWORD, expectedMsg, getDriver(), softAssert);

        softAssert.assertAll();
    }
}