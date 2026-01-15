package drivers;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.safari.SafariDriver;

public class SafariDriverManager extends DriverManager {

    @Override
    public WebDriver createDriver() {
        return new SafariDriver();
    }
}
