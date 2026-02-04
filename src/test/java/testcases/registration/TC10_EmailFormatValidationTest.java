package testcases.registration;

import base.BaseTest;
import helpers.providers.MessagesProvider;
import helpers.verifications.RegisterVerificationHelper;
import model.enums.RegisterField;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.RegisterPage;
import reports.ExtentReportManager;

import static helpers.providers.UserAccountTestDataGenerator.generateNewUniqueEmail;

public class TC10_EmailFormatValidationTest extends BaseTest {

    String validEmail = generateNewUniqueEmail();

    @DataProvider(name = "invalidEmailFormatScenarios")
    public Object[][] invalidEmailFormatScenarios() {
        String localPart = validEmail.split("@")[0];
        String domain = validEmail.split("@")[1];

        // Include some representative / common errors - optionally can use json file to inject complete list of input scenarios
        return new Object[][]{
                {localPart + "@", "Missing domain"},
                {"@" + domain, "Missing local part"},
                {validEmail.replace("@",""), "Missing @"},
                {validEmail.replace("@","@@"), "Multiple @"},
                {validEmail.replace(".",""), "Invalid domain format"},
        };
    }

    @Test(description = "Test Field Validation For Email Format",
            dataProvider = "invalidEmailFormatScenarios")
    public void testEmailFormatValidation(String invalidEmail, String scenario) {

        ExtentReportManager.info("Testing Invalid Email Scenario: " + scenario);
        RegisterPage registerPage = new RegisterPage(getDriver());
        SoftAssert softAssert = new SoftAssert();

        ExtentReportManager.info("Fill invalid email input and blur: " + invalidEmail);
        registerPage.navigateToRegisterPage();
        registerPage.enterFieldInputAndBlur(RegisterField.EMAIL, invalidEmail);

        ExtentReportManager.info("Verify Email validation message");
        String emailValidationMsg = MessagesProvider.getEmailValidationError();
        RegisterVerificationHelper.verifyRegisterFieldValidationMsg(registerPage, RegisterField.EMAIL, emailValidationMsg, getDriver(), softAssert);

        softAssert.assertAll();
    }
}