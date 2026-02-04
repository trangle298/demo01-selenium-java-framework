package testcases.authentication;

import base.BaseTest;
import helpers.verifications.AuthVerificationHelper;
import model.UserAccount;
import model.enums.LoginField;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import reports.ExtentReportManager;

public class TC18_LoginWithEmptyPasswordTest extends BaseTest {

    @Test(groups = "requiresUser",
            description = "Test Blocked Login with empty password field")
    public void testEmptyFieldValidation() {
        SoftAssert softAssert =  new SoftAssert();
        LoginPage loginPage = new LoginPage(getDriver());

        ExtentReportManager.info("Navigate to Login page");
        loginPage.navigateToLoginPage();

        // Attempt login with empty password
        ExtentReportManager.info("Attempt login with empty password field" );
        UserAccount testUser = getTestUser();
        loginPage.fillLoginFormThenSubmit(testUser.getUsername(), "");

        // Verify empty field validation error message & user not logged in
        ExtentReportManager.info("Verify unsuccessful login due to empty password field");
        AuthVerificationHelper.verifyEmptyFieldValidationMsg(loginPage, LoginField.PASSWORD, getDriver(), softAssert);

        softAssert.assertAll();
    }
}