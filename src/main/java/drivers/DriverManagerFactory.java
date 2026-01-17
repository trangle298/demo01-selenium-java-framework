package drivers;

/**
 * Factory for creating browser-specific DriverManager instances.
 * Supports Chrome, Firefox, Safari, and Edge browsers.
 */
public class DriverManagerFactory {

    public static DriverManager getDriverManager(String browserName) {
        if (browserName.equalsIgnoreCase("chrome")) {
            return new ChromeDriverManager();
        } else if (browserName.equalsIgnoreCase("firefox")) {
            return new FirefoxDriverManager();
        } else if (browserName.equalsIgnoreCase("safari")) {
            return new SafariDriverManager();
        } else if (browserName.equalsIgnoreCase("edge")) {
            return new EdgeDriverManager();
        } else {
            throw new IllegalArgumentException("Unsupported browser: " + browserName);
        }
    }
}
