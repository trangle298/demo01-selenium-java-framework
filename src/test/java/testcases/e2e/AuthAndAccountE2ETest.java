package testcases.e2e;

import api.services.UserService;
import base.BaseTest;
import helpers.verifications.AccountVerificationHelper;
import helpers.providers.AuthTestDataGenerator;
import model.ui.RegisterInputs;
import model.UserAccount;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.AccountPage;
import pages.LoginPage;
import pages.RegisterPage;
import reports.ExtentReportManager;

import static helpers.verifications.AuthVerificationHelper.*;
import static helpers.verifications.AccountVerificationHelper.verifyAccountDataMatchesRegistration;
import static helpers.verifications.SoftAssertionHelper.verifySoftEquals;

/**
 * E2E Test: Complete Authentication and Account management Flow
 * Tests the entire user journey from registration, login, account update to logout.
 */
public class AuthAndAccountE2ETest extends BaseTest {

    private RegisterPage registerPage;
    private LoginPage loginPage;
    private AccountPage accountPage;
    private UserService userService;

    @BeforeMethod
    public void setupMethod() {
        registerPage = new RegisterPage(getDriver());
        loginPage = new LoginPage(getDriver());
        accountPage = new AccountPage(getDriver());
        userService = new UserService();
    }

    @Test(groups = {"e2e", "auth", "account", "critical"})
    public void testCompleteAuthFlow() {
        SoftAssert softAssert = new SoftAssert();

        // ============================================
        // Step 1: Register new account
        // ============================================
        ExtentReportManager.info("Register new account");
        registerPage.navigateToRegisterPage();

        RegisterInputs registerData = AuthTestDataGenerator.generateValidRegisterFormInputs();
        registerPage.fillRegisterFormThenSubmit(
                registerData.getUsername(),
                registerData.getPassword(),
                registerData.getConfirmPassword(),
                registerData.getFullName(),
                registerData.getEmail()
        );

        verifyRegisterSuccessMsg(registerPage, getDriver(), softAssert);

        // Verify account created in backend
        ExtentReportManager.info("Verify account persisted in backend");
        UserAccount userFromApi = userService.getUserDetails(registerData.getUsername());
        verifyAccountDataMatchesRegistration(registerData, userFromApi, "Backend API", getDriver(), softAssert);

        // ============================================
        // Step 2: Login with newly registered account
        // ============================================
        ExtentReportManager.info("Login with new credentials");
        loginPage.navigateToLoginPage();
        loginPage.fillLoginFormThenSubmit(registerData.getUsername(), registerData.getPassword());

        verifyLoginSuccess(loginPage, getDriver(), softAssert);
        verifyUserButtonDisplaysCorrectName(loginPage, registerData.getFullName(), getDriver(), softAssert);

        // ============================================
        // Step 3: Verify account data displayed in UI
        // ============================================
        ExtentReportManager.info("Navigate to Account page");
        accountPage.navigateToAccountPage();

        ExtentReportManager.info("Wait for account form to be visible then verify displayed data");
        accountPage.waitForAccountFormToAppear();

        UserAccount accountDataFromUI = accountPage.getAccountData();
        verifyAccountDataMatchesRegistration(registerData, accountDataFromUI, "UI Account Page", getDriver(), softAssert);

        // ============================================
        // Step 4: Update account information
        // ============================================
        ExtentReportManager.info("Update account: name, email, phone");
        String newName = AuthTestDataGenerator.generateNewName(accountPage.getFullName());
        String newEmail = AuthTestDataGenerator.generateNewUniqueEmail();
        String newPhone = AuthTestDataGenerator.generateNewPhoneNumber(accountPage.getPhoneNumber());

        accountPage.changeAccountInfo(newName, newEmail, newPhone);
        accountPage.clickSaveBtn();

        AccountVerificationHelper.verifyPersonalInfoUpdateSuccess(accountPage, newName, newEmail, newPhone, getDriver(), softAssert);

        // Verify update persisted in backend
        ExtentReportManager.info("Verify update persisted in backend");
        UserAccount updatedUser = userService.getUserDetails(registerData.getUsername());
        verifySoftEquals(updatedUser.getHoTen(), newName, "Updated name in backend", getDriver(), softAssert);
        verifySoftEquals(updatedUser.getEmail(), newEmail, "Updated email in backend", getDriver(), softAssert);
        verifySoftEquals(updatedUser.getSoDt(), newPhone, "Updated phone in backend", getDriver(), softAssert);

        // ============================================
        // Step 5: Logout and login again
        // ============================================
        ExtentReportManager.info("Logout and login again to verify persistence");
        accountPage.topBarNavigation.logout();

        loginPage.navigateToLoginPage();
        loginPage.fillLoginFormThenSubmit(registerData.getUsername(), registerData.getPassword());

        verifyLoginSuccess(loginPage, getDriver(), softAssert);
        verifyUserButtonDisplaysCorrectName(loginPage, newName, getDriver(), softAssert);

        softAssert.assertAll();
    }
}