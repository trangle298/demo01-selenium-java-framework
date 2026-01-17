package pages;

import base.BasePage;
import pages.components.TopBarNavigation;
import config.ConfigManager;
import config.Routes;
import org.openqa.selenium.WebDriver;

/**
 * Common page class for shared components across all pages.
 * Includes top bar navigation and common verification methods.
 */
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
     */
    public boolean isRedirectedToHomepage() {
        String homepageUrl = ConfigManager.getBaseUrl();
        return waitForUrl(homepageUrl);
    }

    /**
     * Wait for application to redirect to showtime page by checking URL.
     * Returns true when the URL changes to the expected showtime page within the default timeout.
     */
    public boolean isRedirectedToShowtimePage(String showtimeId) {
        String showtimeUrl = String.format(url(Routes.SHOWTIME), showtimeId);
        return waitForUrl(showtimeUrl);
    }
}
