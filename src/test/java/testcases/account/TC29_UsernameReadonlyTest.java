package testcases.account;

import base.BaseTest;
import helpers.actions.AuthActionHelper;
import helpers.providers.UserAccountTestDataGenerator;
import model.UserAccount;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AccountPage;
import pages.LoginPage;
import reports.ExtentReportManager;

public class TC29_UsernameReadonlyTest extends BaseTest {

    @Test(groups = "requiresUser")
    public void testUsernameIsReadonly() {
        LoginPage loginPage = new LoginPage(getDriver());

        // Login
        ExtentReportManager.info("Login with newly created user credentials");
        UserAccount testUser = getTestUser();
        AuthActionHelper.login(loginPage, testUser);

        // Attempt to change username field value
        ExtentReportManager.info("Navigate to account page and attempt update with invalid full name");
        AccountPage accountPage = new AccountPage(getDriver());
        accountPage.navigateToAccountPage();
        accountPage.waitForAccountFormDisplay();

        String newUsername = UserAccountTestDataGenerator.generateUniqueUsername();
        ExtentReportManager.info("Attempt to change username field input to: " + newUsername);
        accountPage.attemptToChangeUsername(newUsername);

        // Verify username input value is not changed
        String usernameFieldValue = accountPage.getUsername();
        Assert.assertEquals(testUser.getUsername(), usernameFieldValue,
                "Username field does not display original username. Expected = " + testUser.getUsername() + ", Actual = " + usernameFieldValue);

        ExtentReportManager.pass("Username field is readonly");
    }
}