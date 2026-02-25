package drivers;

/**
 * Factory for creating browser-specific DriverManager instances.
 * Supports Chrome, Firefox, Safari, and Edge browsers.
 */
public class DriverManagerFactory {

    public static DriverManager getDriverManager(String browserName) {
        if ("chrome".equalsIgnoreCase(browserName)) {
            return new ChromeDriverManager();
        } else if ("firefox".equalsIgnoreCase(browserName)) {
            return new FirefoxDriverManager();
        } else if ("safari".equalsIgnoreCase(browserName)) {
            return new SafariDriverManager();
        } else if ("edge".equalsIgnoreCase(browserName)) {
            return new EdgeDriverManager();
        } else {
            throw new IllegalArgumentException("Unsupported browser: " + browserName);
        }
    }
}
