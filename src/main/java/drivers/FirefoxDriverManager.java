package drivers;

import config.ConfigManager;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class FirefoxDriverManager extends DriverManager {

    @Override
    public WebDriver createDriver() {
        boolean eager = Boolean.parseBoolean(ConfigManager.getProperty("eagerPageLoadStrategy"));

        FirefoxOptions options = new FirefoxOptions();
        options.setPageLoadStrategy(eager ? PageLoadStrategy.EAGER : PageLoadStrategy.NORMAL);

        return new FirefoxDriver(options);
    }
}
