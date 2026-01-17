package testcases.auth;

import base.BaseTest;
import helpers.TestUserProvider;
import model.TestUser;
import model.TestUserType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.HomePage;
import pages.LoginPage;
import reports.ExtentReportManager;

import static helpers.SoftAssertionHelper.verifySoftFalse;
import static helpers.SoftAssertionHelper.verifySoftTrue;
import static helpers.AuthVerificationHelper.verifyLogoutSuccess;

public class LogoutTest extends BaseTest {

    private HomePage homePage;

    @BeforeMethod(alwaysRun = true)
    public void setUpMethod() {
        LoginPage loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());

        TestUser testUser = TestUserProvider.getUser(TestUserType.basicUser);

        ExtentReportManager.info("Log in and navigate to Homepage before test");
        loginPage.navigateToLoginPage();
        loginPage.fillLoginFormAndSubmit(testUser.getUsername(), testUser.getPassword());

        loginPage.topBarNavigation.waitForUserProfileLink();

        boolean homepageRedirected = loginPage.isRedirectedToHomepage();
        if (!homepageRedirected) {
            homePage.navigateToHomePage();
        }
    }

    @Test(groups = {"integration", "auth", "logout", "smoke", "critical"})
    public void testValidLogout() {
        SoftAssert softAssert = new SoftAssert();

        ExtentReportManager.info("Click Logout link and confirm logout");
        homePage.topBarNavigation.logout();

        ExtentReportManager.info("Verify logout success");
        verifyLogoutSuccess(homePage, getDriver(), softAssert);

        softAssert.assertAll();
    }

    @Test(groups = {"component", "auth", "logout"})
    public void testCancelledLogout() {
        SoftAssert softAssert = new SoftAssert();

        ExtentReportManager.info("Click Logout link and cancel logout");
        homePage.topBarNavigation.clickLogoutLink();
        homePage.topBarNavigation.cancelLogout();

        ExtentReportManager.info("Verify user is not logged out");
        verifyLogoutCancelled(softAssert);

        softAssert.assertAll();
    }

    // ---- Helper methods for verification ----
    private void verifyLogoutCancelled(SoftAssert softAssert) {
        verifySoftFalse(homePage.topBarNavigation.isLogoutSuccessAlertVisible(),
                "Logout success alert should not be visible after cancelling logout", getDriver(), softAssert);

        verifySoftTrue(homePage.topBarNavigation.isUserProfileVisible(),
                "User profile should still be visible after cancelling logout", getDriver(), softAssert);

        verifySoftFalse(homePage.topBarNavigation.isLoginLinkVisible(),
                "Login link should not be visible after cancelling logout", getDriver(), softAssert);
    }
}
