package testcases.e2e;

import api.services.UserService;
import base.BaseTest;
import helpers.providers.UserAccountTestDataGenerator;
import helpers.verifications.AccountVerificationHelper;
import helpers.verifications.RegisterVerificationHelper;
import model.UserAccount;
import model.ui.RegisterDataUI;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.AccountPage;
import pages.LoginPage;
import pages.RegisterPage;
import reports.ExtentReportManager;

import static helpers.verifications.AuthVerificationHelper.*;

public class TC46_RegisterAuthE2ETest extends BaseTest {

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

    @Test
    public void testCompleteRegisterAndAuthFlow() {
        SoftAssert softAssert = new SoftAssert();

        // ============================================
        // Step 1: Register new account
        // ============================================
        ExtentReportManager.info("Register new account");
        registerPage.navigateToRegisterPage();

        RegisterDataUI registerData = UserAccountTestDataGenerator.generateValidRegisterFormInputs();
        registerPage.fillRegisterFormThenSubmit(
                registerData.getUsername(),
                registerData.getPassword(),
                registerData.getConfirmPassword(),
                registerData.getFullName(),
                registerData.getEmail()
        );

        RegisterVerificationHelper.verifyRegisterSuccessMsg(registerPage, getDriver(), softAssert);

        // Verify account created in backend
        ExtentReportManager.info("Verify account persisted in backend");
        UserAccount userFromApi = userService.getUserDetails(registerData.getUsername());
        AccountVerificationHelper.verifyAccountDataMatchesUIRegistration(registerData, userFromApi, "Backend API", getDriver(), softAssert);

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

        ExtentReportManager.info("Verify displayed user data matches registered inputs");
        UserAccount accountDataFromUI = accountPage.getAccountData();
        AccountVerificationHelper.verifyAccountDataMatchesUIRegistration(registerData, accountDataFromUI, "UI Account Page", getDriver(), softAssert);

        // ============================================
        // Step 4: Logout
        // ============================================
        ExtentReportManager.info("Logout and login again to verify persistence");
        accountPage.topBarNavigation.clickLogoutLinkAndConfirm();

        loginPage.navigateToLoginPage();
        loginPage.fillLoginFormThenSubmit(registerData.getUsername(), registerData.getPassword());

        verifyLoginSuccess(loginPage, getDriver(), softAssert);

        softAssert.assertAll();
    }
}
