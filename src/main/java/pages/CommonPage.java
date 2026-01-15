package pages;

import base.BasePage;
import pages.components.TopBarNavigation;
import config.ConfigManager;
import config.Routes;
import org.openqa.selenium.WebDriver;

public class CommonPage extends BasePage {

    public TopBarNavigation topBarNavigation;

    public CommonPage(WebDriver driver) {
        super(driver);
        topBarNavigation = new TopBarNavigation(driver);
    }

    /**
     * Wait for application to redirect to homepage by checking URL.
     * This is a reusable helper for flows that end up on the homepage (login, logout, other actions).
     * Returns true when the URL changes to homepage within the default timeout.
     * Does not assert; callers/tests should decide how to handle false.
     */
    public boolean isRedirectedToHomepage() {
        String homepageUrl = ConfigManager.getBaseUrl();
        try {
           return waitForUrl(homepageUrl);
        } catch (Exception e) {
            LOG.warn("Did not redirect to homepage within timeout. Current URL: " + driver.getCurrentUrl());
            return false;
        }
    }

    public boolean isRedirectedToShowtimePage(String showtimeId) {
        String showtimeUrl = String.format(url(Routes.SHOWTIME), showtimeId);
        try {
           return waitForUrl(showtimeUrl);
        } catch (Exception e) {
            LOG.warn("Did not redirect to showtime page within timeout. Current URL: " + driver.getCurrentUrl());
            return false;
        }
    }


}
