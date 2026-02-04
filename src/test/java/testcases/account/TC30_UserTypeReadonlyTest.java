package testcases.account;

import base.BaseTest;
import helpers.actions.AuthActionHelper;
import model.UserAccount;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AccountPage;
import pages.LoginPage;
import reports.ExtentReportManager;

public class TC30_UserTypeReadonlyTest extends BaseTest {

    @Test(groups = "requiresUser")
    public void testUsernameIsReadonly() {
        LoginPage loginPage = new LoginPage(getDriver());

        // Login
        ExtentReportManager.info("Login with newly created user credentials");
        UserAccount testUser = getTestUser();
        AuthActionHelper.login(loginPage, testUser);

        // Attempt to select Admin option from UserType dropdown
        ExtentReportManager.info("Navigate select Admin option from UserType dropdown");
        AccountPage accountPage = new AccountPage(getDriver());
        accountPage.navigateToAccountPage();
        accountPage.waitForAccountFormDisplay();

        accountPage.attemptToChangeUserTypeToAdmin();

        // Verify User Type selected option remains unchanged
        String displayedUserType = accountPage.getUserType();
        Assert.assertEquals(testUser.getUserType(), displayedUserType,
                "User type does not display original type. Expected = " + testUser.getUserType() + ", Actual = " + displayedUserType);

        ExtentReportManager.pass("Username field is readonly");
    }
}