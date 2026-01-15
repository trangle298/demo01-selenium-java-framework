package drivers;

import config.ConfigManager;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

public class EdgeDriverManager extends DriverManager {

    @Override
    public WebDriver createDriver() {
        boolean eager = Boolean.parseBoolean(ConfigManager.getProperty("eagerPageLoadStrategy"));

        EdgeOptions options = new EdgeOptions();
        options.setPageLoadStrategy(eager ? PageLoadStrategy.EAGER : PageLoadStrategy.NORMAL);

        return new EdgeDriver(options);
    }
}
