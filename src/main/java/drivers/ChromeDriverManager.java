package drivers;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import config.ConfigManager;

/**
 * Chrome browser driver manager.
 * Configures ChromeDriver with options for page load strategy and automation detection.
 */
public class ChromeDriverManager extends DriverManager {

    @Override
    public WebDriver createDriver() {

        boolean eager = Boolean.parseBoolean(ConfigManager.getProperty("eagerPageLoadStrategy"));

        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(eager ? PageLoadStrategy.EAGER : PageLoadStrategy.NORMAL);
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

       return new ChromeDriver(options);
    }
}
