package testcases.authentication;

import base.BaseTest;
import helpers.actions.AuthActionHelper;
import model.UserAccount;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.HomePage;
import pages.LoginPage;
import reports.ExtentReportManager;

import static helpers.verifications.SoftAssertionHelper.verifySoftFalse;
import static helpers.verifications.SoftAssertionHelper.verifySoftTrue;

public class TC22_LogoutCancelledTest extends BaseTest {

    private HomePage homePage;

    @Test(groups = "requiresUser")
    public void testLogoutCancelled(){

        SoftAssert softAssert = new SoftAssert();
        LoginPage loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());

        ExtentReportManager.info("Log in and navigate to Homepage if not redirected");
        UserAccount testUser = getTestUser();
        AuthActionHelper.login(loginPage, testUser);

        boolean homepageRedirected = loginPage.isRedirectedToHomepage();
        if (!homepageRedirected) {
            homePage.navigateToHomePage();
        }

        ExtentReportManager.info("Click Logout link then click Cancel at confirmation dialog");
        homePage.topBarNavigation.clickLogoutLink();
        homePage.topBarNavigation.cancelLogout();

        // Verify logout cancelled: No success alert, user profile is still visible, login link not visible
        ExtentReportManager.info("Verify user is not logged out");
        verifyLogoutCancelled(softAssert);

        softAssert.assertAll();
    }

    private void verifyLogoutCancelled(SoftAssert softAssert) {
        verifySoftFalse(homePage.topBarNavigation.isLogoutSuccessAlertVisible(),
                "Logout success alert should not be visible after cancelling logout", getDriver(), softAssert);

        verifySoftTrue(homePage.topBarNavigation.isUserProfileVisible(),
                "User profile should still be visible after cancelling logout", getDriver(), softAssert);

        verifySoftFalse(homePage.topBarNavigation.isLoginLinkVisible(),
                "Login link should not be visible after cancelling logout", getDriver(), softAssert);
    }
}