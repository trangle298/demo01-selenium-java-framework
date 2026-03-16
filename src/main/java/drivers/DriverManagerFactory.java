package drivers;

import config.enums.Browser;

/**
 * Factory for creating browser-specific DriverManager instances.
 * Supports Chrome, Firefox, Safari, and Edge browsers.
 */
public class DriverManagerFactory {

    public static DriverManager getDriverManager(Browser browser) {
        return switch (browser) {
            case CHROME -> new ChromeDriverManager();
            case FIREFOX -> new FirefoxDriverManager();
            case SAFARI -> new SafariDriverManager();
            case EDGE -> new EdgeDriverManager();
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };
    }
}
