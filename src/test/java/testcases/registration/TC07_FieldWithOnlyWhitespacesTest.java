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

public class TC07_FieldWithOnlyWhitespacesTest extends BaseTest {

    @DataProvider(name = "fieldWithOnlyWhitespacesScenarios")
    public Object[][] fieldWithOnlyWhitespacesScenarios() {
        return new Object[][]{
                {RegisterField.USERNAME, "Username with only whitespaces"},
                {RegisterField.FULL_NAME, "Full Name with only whitespaces"},
                {RegisterField.EMAIL, "Email with only whitespaces"},
        };
    }

    @Test(description = "Test Field Validation For Field With Only Whitespace",
            dataProvider = "fieldWithOnlyWhitespacesScenarios")
    public void testFieldWithOnlyWhitespacesValidation(RegisterField fieldType, String scenario) {
        RegisterPage registerPage = new RegisterPage(getDriver());
        SoftAssert softAssert = new SoftAssert();

        ExtentReportManager.info("Test scenario: " + scenario);
        registerPage.navigateToRegisterPage();
        registerPage.enterFieldInputAndBlur(fieldType, "     ");

        ExtentReportManager.info("Verify validation error message for " + scenario);
        String expectedMsg = MessagesProvider.getRequiredFieldError();
        RegisterVerificationHelper.verifyRegisterFieldValidationMsg(registerPage, fieldType, expectedMsg, getDriver(), softAssert);

        softAssert.assertAll();
    }
}