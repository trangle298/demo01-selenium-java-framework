package testcases.e2e;

import base.BaseTest;
import helpers.AccountVerificationHelper;
import helpers.AuthTestDataGenerator;
import model.RegisterRequest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.AccountPage;
import pages.LoginPage;
import pages.RegisterPage;
import reports.ExtentReportManager;

import static helpers.AuthVerificationHelper.*;
import static helpers.AccountVerificationHelper.*;

/**
 * E2E Test: Complete Authentication and Account management Flow
 * Tests the entire user journey from registration, login, account update to logout.
 */
public class AuthAndAccountE2ETest extends BaseTest {

    private RegisterPage registerPage;
    private LoginPage loginPage;
    private AccountPage accountPage;

    @BeforeMethod
    public void setupMethod() {
        registerPage = new RegisterPage(getDriver());
        loginPage = new LoginPage(getDriver());
        accountPage = new AccountPage(getDriver());
    }

    @Test(groups = {"e2e", "auth", "account", "critical"})
    public void testCompleteAuthFlow() {
        SoftAssert softAssert = new SoftAssert();

        // ============================================
        // Step 1: Register new account
        // ============================================
        ExtentReportManager.info("Navigate to Register page and create new account");
        registerPage.navigateToRegisterPage();

        RegisterRequest registerRequest = AuthTestDataGenerator.generateValidRegisterData();
        registerPage.fillAndSubmitRegisterForm(
                registerRequest.getUsername(),
                registerRequest.getPassword(),
                registerRequest.getConfirmPassword(),
                registerRequest.getFullName(),
                registerRequest.getEmail()
        );
        verifyRegisterSuccessMsg(registerPage, getDriver(), softAssert);

        // ============================================
        // Step 2: Login with newly registered account
        // ============================================
        ExtentReportManager.info("Navigate to Login page and login with new credentials");
        loginPage.navigateToLoginPage();
        loginPage.fillLoginFormAndSubmit(registerRequest.getUsername(), registerRequest.getPassword());

        ExtentReportManager.info("Verify login success and correct user name displayed in top bar");
        verifyLoginSuccess(loginPage, getDriver(), softAssert);
        verifyUserButtonDisplaysCorrectName(loginPage, registerRequest.getFullName(), getDriver(), softAssert);

        // ============================================
        // Step 3: Navigate to Account page to verify account information
        // ============================================
        ExtentReportManager.info("Navigate to Account page and verify account information matches registration data");
        accountPage.navigateToAccountPage();
        verifyAccountDataMatchesRegisterData(accountPage, registerRequest, getDriver(), softAssert);

        // ============================================
        // Step 4: Update Account information: name, email, phone number
        // ============================================
        ExtentReportManager.info("Update account information: name, email, phone number");
        String newName = AuthTestDataGenerator.generateNewName(accountPage.getFullName());
        String newEmail = AuthTestDataGenerator.generateNewEmail(accountPage.getEmail());
        String newPhone = AuthTestDataGenerator.generateNewPhoneNumber(accountPage.getPhoneNumber());

        accountPage.changeAccountInfo(newName, newEmail, newPhone);
        accountPage.saveChanges();

        ExtentReportManager.info("Verify account update success");
        AccountVerificationHelper.verifyAllAccountInfoUpdateSuccess(accountPage, newName, newEmail, newPhone, getDriver(), softAssert);

        // ============================================
        // Step 5: Log out of the account
        // ============================================
        ExtentReportManager.info("Log out of the account");
        accountPage.topBarNavigation.logout();
        verifyLogoutSuccess(accountPage, getDriver(), softAssert);

        // ============================================
        // Step 6: Log in again to verify updated user profile name in top bar
        // ============================================
        ExtentReportManager.info("Log back in and verify user profile name is updated in top bar");
        loginPage.navigateToLoginPage();
        loginPage.fillLoginFormAndSubmit(registerRequest.getUsername(), registerRequest.getPassword());

        verifyLoginSuccess(loginPage, getDriver(), softAssert);
        verifyUserButtonDisplaysCorrectName(loginPage, newName, getDriver(), softAssert);

        softAssert.assertAll();
    }
}

