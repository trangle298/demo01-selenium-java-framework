package testcases.account;

import base.BaseTest;
import helpers.actions.AuthActionHelper;
import helpers.verifications.SoftAssertionHelper;
import model.UserAccount;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.AccountPage;
import pages.LoginPage;
import reports.ExtentReportManager;

public class TC23_AccountPageDisplaysCorrectUserInfoTest extends BaseTest {

    @Test(groups = "requiresUser")
    public void testAccountPageDisplaysCorrectUserInfo()  {

        SoftAssert softAssert = new SoftAssert();
        LoginPage loginPage = new LoginPage(getDriver());

        ExtentReportManager.info("Login");
        UserAccount testUser = getTestUser();
        AuthActionHelper.login(loginPage, testUser);

        ExtentReportManager.info("Navigate to account page and collect displayed user info");
        AccountPage accountPage = new AccountPage(getDriver());
        accountPage.navigateToAccountPage();
        UserAccount displayedUserInfo = accountPage.getAccountData();

        ExtentReportManager.info("Verify displayed info matches payload info used for creating new user");
        SoftAssertionHelper.verifySoftEquals(displayedUserInfo, testUser,
                "User information", getDriver(), softAssert);

        softAssert.assertAll();
    }
}