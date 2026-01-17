package drivers;

import config.ConfigManager;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

/**
 * Safari browser driver manager.
 * Configures SafariDriver with options for page load strategy.
 */
public class SafariDriverManager extends DriverManager {

    @Override
    public WebDriver createDriver() {
        boolean eager = Boolean.parseBoolean(ConfigManager.getProperty("eagerPageLoadStrategy"));

        SafariOptions options = new SafariOptions();
        options.setPageLoadStrategy(eager ? PageLoadStrategy.EAGER : PageLoadStrategy.NORMAL);

        return new SafariDriver(options);
    }
}
