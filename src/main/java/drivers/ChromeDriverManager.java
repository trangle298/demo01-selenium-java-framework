package drivers;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Chrome browser driver manager.
 * Configures ChromeDriver with options for page load strategy and automation
 * detection.
 * Supports both local execution and Selenium Grid via base class.
 */
public class ChromeDriverManager extends DriverManager {

    @Override
    protected MutableCapabilities getBrowserOptions() {
        boolean eager = isEagerPageLoad();
        boolean headless = isHeadless();

        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(eager ? PageLoadStrategy.EAGER : PageLoadStrategy.NORMAL);

        if (headless) {
            options.addArguments(
                    "--headless=new", // Use new headless mode (Chrome 109+)
                    "--window-size=1920,1080", // Set viewport size
                    "--disable-gpu",
                    "--no-sandbox",
                    "--disable-dev-shm-usage");
        } else {
            options.addArguments("--start-maximized");
        }

        options.setExperimentalOption("excludeSwitches",
                new String[] { "enable-automation" });
        options.setExperimentalOption("useAutomationExtension", false);

        return options;
    }

    @Override
    protected WebDriver createLocalDriver(MutableCapabilities options) {
        return new ChromeDriver((ChromeOptions) options);
    }

}
