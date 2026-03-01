package drivers;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

/**
 * Microsoft Edge browser driver manager.
 * Configures EdgeDriver with options for page load strategy.
 */
public class EdgeDriverManager extends DriverManager {

    @Override
    protected MutableCapabilities getBrowserOptions() {
        boolean eager = isEagerPageLoad();
        boolean headless = isHeadless();

        EdgeOptions options = new EdgeOptions();
        options.setPageLoadStrategy(eager ? PageLoadStrategy.EAGER : PageLoadStrategy.NORMAL);

        if (headless) {
            options.addArguments(
                    "--headless=new",
                    "--window-size=1920,1080",
                    "--disable-gpu",
                    "--no-sandbox",
                    "--disable-dev-shm-usage");
        } else {
            options.addArguments("--start-maximized");
        }

        return options;
    }

    @Override
    protected WebDriver createLocalDriver(MutableCapabilities options) {
        return new EdgeDriver((EdgeOptions) options);
    }

}
