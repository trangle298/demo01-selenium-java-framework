package testcases.auth;

import base.BaseTest;
import helpers.Messages;
import helpers.TestUserProvider;
import model.RegisterRequest;
import model.TestUser;
import model.TestUserType;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import pages.RegisterPage;
import reports.ExtentReportManager;

import static helpers.AuthTestDataGenerator.*;

public class RegisterTest extends BaseTest {

    private RegisterPage registerPage;

    @BeforeMethod(alwaysRun = true)
    public void setUpMethod() {
        registerPage = new RegisterPage(getDriver());
        ExtentReportManager.info("Step 1: Navigate to Register Page");
        registerPage.navigateToRegisterPage();
    }

    @Test(groups = {"component", "auth", "register", "smoke"})
    // Valid Registration Test - Tests ONLY the registration UI behavior
    public void testValidRegister() {
        ExtentReportManager.info("Step 2: Submit Register form with valid data");
        RegisterRequest registerRequest = generateValidRegisterData();
        registerPage.fillAndSubmitRegisterForm(
                registerRequest.getUsername(),
                registerRequest.getPassword(),
                registerRequest.getConfirmPassword(),
                registerRequest.getFullName(),
                registerRequest.getEmail()
        );

        ExtentReportManager.info("Step 3: Verify Register Success message");
        boolean isAlertDisplayed = registerPage.isRegisterSuccessAlertDisplayed();
        Assert.assertTrue(isAlertDisplayed, "Register success message is NOT displayed");
        ExtentReportManager.pass("Register success message is displayed");

        String expectedMsg = Messages.getRegisterSuccessMessage();
        String actualMsg = registerPage.getRegisterSuccessMsgText();
        Assert.assertEquals(actualMsg, expectedMsg,
                "Register success message text is incorrect. Actual = " + actualMsg + ". Expected = " + expectedMsg);
        ExtentReportManager.pass("Register success message text is correct");
    }

    // =========================
    // Form Validation Tests (Client-side)
    // Tests registration form behavior with invalid/missing inputs
    // =========================
    @Test(groups = {"component", "auth", "register", "negative"})
    public void testInvalidRegister_BlankField() {
        SoftAssert softAssert = new SoftAssert();

        ExtentReportManager.info("Step 2: Submit Register form with one blank field: Email");
        RegisterRequest request = generateValidRegisterData();
        registerPage.fillAndSubmitRegisterForm(
                request.getUsername(),
                request.getPassword(),
                request.getConfirmPassword(),
                request.getFullName(),
                "" // Blank Email
        );

        ExtentReportManager.info("Step 3: Verify field validation error");
        String expectedMsg = Messages.getRequiredFieldError();
        verifyFieldErrorMsg("email", expectedMsg, softAssert);

        softAssert.assertAll();
    }

    @Test(groups = {"component", "auth", "register", "negative"})
    public void testInvalidRegister_InvalidInput() {
        SoftAssert softAssert = new SoftAssert();

        ExtentReportManager.info("Step 2: Submit Register form with one blank field: Full Name");
        RegisterRequest request = generateValidRegisterData();
        String invalidFullName = generateInvalidNameContainingNumbers();

        registerPage.fillAndSubmitRegisterForm(
                request.getUsername(),
                request.getPassword(),
                request.getConfirmPassword(),
                invalidFullName, // Invalid Full Name
                request.getEmail()
        );

        ExtentReportManager.info("Step 3: Verify field validation error");
        String expectedMsg = Messages.getNameContainsNumberError();
        verifyFieldErrorMsg("fullName", expectedMsg, softAssert);

        softAssert.assertAll();
    }

    @Test(groups = {"component", "auth", "register", "negative"})
    public void testInvalidRegister_MismatchedPasswords() {
        SoftAssert softAssert = new SoftAssert();

        ExtentReportManager.info("Step 2: Submit Register form with mismatched passwords");
        RegisterRequest request = generateValidRegisterData();
        String password = request.getPassword();
        String mismatchedPassword = generateNewPassword(password);

        registerPage.fillAndSubmitRegisterForm(
                request.getUsername(),
                password,
                mismatchedPassword,
                request.getFullName(),
                request.getEmail()
        );

        ExtentReportManager.info("Step 3: Verify field validation error");
        String expectedMsg = Messages.getPasswordMismatchError();
        verifyFieldErrorMsg("confirmPassword", expectedMsg, softAssert);

        softAssert.assertAll();
    }

    // =========================
    // Form Validation Tests (Server-side)
    // =========================
    @Test(groups = {"integration", "auth", "register", "negative"})
    public void testInvalidRegister_ExistingUsername() {
        SoftAssert softAssert = new SoftAssert();

        ExtentReportManager.info("Step 2: Submit Register form with existing username");
        RegisterRequest request = generateValidRegisterData();
        TestUser existingUser = TestUserProvider.getUser(TestUserType.basicUser);

        registerPage.fillAndSubmitRegisterForm(
                existingUser.getUsername(), // Existing username
                request.getPassword(),
                request.getConfirmPassword(),
                request.getFullName(),
                request.getEmail()
        );
        ExtentReportManager.info("Step 3: Verify form validation error");
        String expectedMsg = Messages.getRegisterExistingUsernameError();
        verifyFormErrorAlert(expectedMsg, softAssert);

        softAssert.assertAll();
    }

    @Test(groups = {"integration", "auth", "register", "negative"})
    public void testRegister_ExistingEmail() {
        SoftAssert softAssert = new SoftAssert();

        ExtentReportManager.info("Step 2: Submit Register form with existing email");
        RegisterRequest request = generateValidRegisterData();
        TestUser existingUser = TestUserProvider.getUser(TestUserType.basicUser);

        registerPage.fillAndSubmitRegisterForm(
                request.getUsername(),
                request.getPassword(),
                request.getConfirmPassword(),
                request.getFullName(),
                existingUser.getEmail() // Existing email
        );

        ExtentReportManager.info("Step 3: Verify form validation error");
        String expectedMsg = Messages.getRegisterExistingEmailError();
        verifyFormErrorAlert(expectedMsg, softAssert);

        softAssert.assertAll();
    }

    // --------------------------
    // Helper methods for verification
    // --------------------------

    private void verifyFormErrorAlert(String expectedMsg, SoftAssert softAssert) {
        // Verify error alert is displayed
        boolean errorDisplayed = verifySoftTrue(registerPage.isRegisterErrorAlertDisplayed(),
                "Register error alert is displayed", softAssert);

        // Verify error message text
        if (errorDisplayed) {
            String actualMsg = registerPage.getRegisterErrorMsgText();
            verifySoftEquals(actualMsg, expectedMsg, "Register form error message text", softAssert);
        }
    }

    private void verifyFieldErrorMsg(String fieldName, String expectedMsg, SoftAssert softAssert) {
        boolean errorDisplayed = registerPage.isFieldValidationErrorDisplayed(fieldName);
        verifySoftTrue(errorDisplayed, fieldName + " field error is displayed", softAssert);

        if (errorDisplayed) {
            String actualMsg = registerPage.getFieldErrorText(fieldName);
            verifySoftEquals(actualMsg, expectedMsg, fieldName + " error message text", softAssert);
        }
    }

}
