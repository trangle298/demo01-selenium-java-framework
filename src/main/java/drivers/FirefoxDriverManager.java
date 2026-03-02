package drivers;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Firefox browser driver manager.
 * Configures FirefoxDriver with options for page load strategy and headless
 * mode.
 */
public class FirefoxDriverManager extends DriverManager {

    @Override
    protected MutableCapabilities getBrowserOptions() {
        boolean eager = isEagerPageLoad();
        boolean headless = isHeadless();

        FirefoxOptions options = new FirefoxOptions();
        options.setPageLoadStrategy(eager ? PageLoadStrategy.EAGER : PageLoadStrategy.NORMAL);

        if (headless) {
            options.addArguments("-headless");
        }

        return options;
    }

    @Override
    protected WebDriver createLocalDriver(MutableCapabilities options) {
        return new FirefoxDriver((FirefoxOptions) options);
    }
}
