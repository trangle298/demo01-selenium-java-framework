package helpers.actions;

import model.UserAccount;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pages.CommonPage;
import pages.LoginPage;

public class AuthActionHelper {

    private static final Logger LOG = LogManager.getLogger(AuthActionHelper.class);

    public static void login(LoginPage loginPage, String username, String password) {
        LOG.info("Login as user: " + username);
        loginPage.navigateToLoginPage();
        loginPage.fillLoginFormThenSubmit(username, password);
        loginPage.topBarNavigation.waitForUserProfileLink();
    }

    public static void login(LoginPage loginPage, UserAccount userAccount) {
        login(loginPage, userAccount.getUsername(), userAccount.getPassword());
    }

    public static void logout(CommonPage page) {
        LOG.info("Log out");
        page.topBarNavigation.clickLogoutLinkAndConfirm();
        page.topBarNavigation.waitForLoginLink();
    }

}
