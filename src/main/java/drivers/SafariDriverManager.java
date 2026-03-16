package drivers;

import org.openqa.selenium.MutableCapabilities;
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
    protected MutableCapabilities getBrowserOptions() {
        boolean eager = isEagerPageLoad();

        SafariOptions options = new SafariOptions();
        options.setPageLoadStrategy(eager ? PageLoadStrategy.EAGER : PageLoadStrategy.NORMAL);

        return options;
    }

    @Override
    protected WebDriver createLocalDriver(MutableCapabilities options) {
        return new SafariDriver((SafariOptions) options);
    }
}