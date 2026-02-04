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

public class TC06_EmptyRegisterFieldValidationTest extends BaseTest {

    @DataProvider(name = "emptyFieldScenarios")
    public Object[][] emptyFieldScenarios() {
        return new Object[][]{
                {RegisterField.USERNAME, "Blank Username"},
                {RegisterField.PASSWORD, "Blank Password"},
                {RegisterField.CONFIRM_PASSWORD, "Blank Confirm Password"},
                {RegisterField.FULL_NAME, "Blank Full Name"},
                {RegisterField.EMAIL, "Blank Email"},
        };
    }

    @Test(description = "Test Field Validation For Empty Field",
            dataProvider = "emptyFieldScenarios")
    public void testEmptyFieldValidation(RegisterField fieldType, String scenario) {
        RegisterPage registerPage = new RegisterPage(getDriver());
        SoftAssert softAssert = new SoftAssert();

        ExtentReportManager.info("Test scenario: " + scenario);
        registerPage.navigateToRegisterPage();
        registerPage.enterFieldInputAndBlur(fieldType, "");

        ExtentReportManager.info("Verify validation error message for " + scenario);
        String expectedMsg = MessagesProvider.getRequiredFieldError();
        RegisterVerificationHelper.verifyRegisterFieldValidationMsg(registerPage, fieldType, expectedMsg, getDriver(), softAssert);

        softAssert.assertAll();
    }
}