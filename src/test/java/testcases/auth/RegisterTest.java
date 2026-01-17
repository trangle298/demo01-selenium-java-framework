package testcases.auth;

import base.BaseTest;
import helpers.Messages;
import helpers.SoftAssertionHelper;
import helpers.TestUserProvider;
import model.RegisterRequest;
import model.TestUser;
import model.TestUserType;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import pages.RegisterPage;
import reports.ExtentReportManager;

import static helpers.AuthTestDataGenerator.*;
import static helpers.AuthVerificationHelper.verifyRegisterSuccessMsg;

public class RegisterTest extends BaseTest {

    private RegisterPage registerPage;

    @BeforeMethod(alwaysRun = true)
    public void setUpMethod() {
        registerPage = new RegisterPage(getDriver());
        ExtentReportManager.info("Step 1: Navigate to Register Page");
        registerPage.navigateToRegisterPage();
    }

    @Test(groups = {"component", "auth", "register", "smoke", "critical"})
    public void testValidRegister() {
        SoftAssert softAssert = new SoftAssert();
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
        verifyRegisterSuccessMsg(registerPage, getDriver(), softAssert);

        softAssert.assertAll();
    }

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

    // Form Validation Tests (Server-side)
    @Test(groups = {"integration", "auth", "register", "negative", "critical"})
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

    @Test(groups = {"integration", "auth", "register", "negative", "critical"})
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

    // ---- Helper methods for verification ----
    private void verifyFormErrorAlert(String expectedMsg, SoftAssert softAssert) {
        // Verify error alert is displayed
        boolean errorDisplayed = SoftAssertionHelper.verifySoftTrue(registerPage.isRegisterErrorAlertDisplayed(),
                "Register error alert is displayed", getDriver(), softAssert);

        // Verify error message text
        if (errorDisplayed) {
            String actualMsg = registerPage.getRegisterErrorMsgText();
            SoftAssertionHelper.verifySoftEquals(actualMsg, expectedMsg, "Register form error message text", getDriver(), softAssert);
        }
    }

    private void verifyFieldErrorMsg(String fieldName, String expectedMsg, SoftAssert softAssert) {
        boolean errorDisplayed = registerPage.isFieldValidationErrorDisplayed(fieldName);
        SoftAssertionHelper.verifySoftTrue(errorDisplayed, fieldName + " field error is displayed", getDriver(), softAssert);

        if (errorDisplayed) {
            String actualMsg = registerPage.getFieldErrorText(fieldName);
            SoftAssertionHelper.verifySoftEquals(actualMsg, expectedMsg, fieldName + " error message text", getDriver(), softAssert);
        }
    }

}
