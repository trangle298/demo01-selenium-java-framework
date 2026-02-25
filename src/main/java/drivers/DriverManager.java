package drivers;

import config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;

/**
 * Abstract base class for browser-specific driver managers.
 * Implements Template Method Pattern to centralize local vs remote execution
 * logic.
 * Subclasses must implement configureBrowserOptions() to return
 * browser-specific options.
 * The base class handles the decision of whether to create a local or remote
 * driver.
 */
public abstract class DriverManager {

    private static final Logger LOG = LogManager.getLogger(DriverManager.class);

    // ---- Public Methods ----
    /**
     * Creates and returns a WebDriver instance (local or remote based on
     * configuration).
     * This is the Template Method that orchestrates driver creation.
     * 
     * @return WebDriver instance ready for use
     */
    public final WebDriver createDriver() {
        MutableCapabilities options = getBrowserOptions();

        String runOn = getRunOn();

        if ("local".equalsIgnoreCase(runOn)) {
            return createLocalDriver(options);
        } else {
            // Remote execution: Grid, Perfecto, AWS, etc.
            return createRemoteDriver(options, runOn);
        }
    }

    // ---- Abstract Methods: Subclasses must implement these methods ----
    /**
     * Configures browser-specific options/capabilities.
     * Subclasses must implement this to provide their specific browser
     * configuration.
     * 
     * @return MutableCapabilities (ChromeOptions, FirefoxOptions, EdgeOptions,
     *         etc.)
     */
    protected abstract MutableCapabilities getBrowserOptions();

    /**
     * Creates a local WebDriver instance using the provided options.
     * Subclasses must implement this to return their specific driver type.
     * 
     * @param options Browser-specific options
     * @return Local WebDriver instance (ChromeDriver, FirefoxDriver, etc.)
     */
    protected abstract WebDriver createLocalDriver(MutableCapabilities options);

    // ---- Protected Helper Methods ----
    /**
     * Returns true if eager page load strategy is configured.
     * EAGER = doesn't wait for all resources (images, css) - faster.
     * NORMAL = waits for everything to load - more reliable.
     */
    protected boolean isEagerPageLoad() {
        return Boolean.parseBoolean(ConfigManager.getProperty("eagerPageLoadStrategy"));
    }

    /**
     * Returns true if headless browser mode is configured.
     * Headless = no UI window, required for CI/CD environments like Jenkins.
     */
    protected boolean isHeadless() {
        String value = ConfigManager.getProperty("headless");
        return value != null && Boolean.parseBoolean(value);
    }

    // ---- Private Helper Methods ----
    /**
     * Creates a RemoteWebDriver instance connected to the specified remote
     * platform.
     *
     * @param capabilities Browser capabilities configured by subclass
     * @param platform     Remote platform: grid, perfecto, aws
     * @return RemoteWebDriver instance connected to remote platform
     * @throws RuntimeException if remote URL is invalid or connection fails
     */
    private WebDriver createRemoteDriver(MutableCapabilities capabilities, String platform) {
        String remoteUrl = getRemoteUrl(platform);

        try {
            LOG.info("Connecting to remote platform: {} at {}", platform, remoteUrl);
            LOG.info("Browser capabilities: {}", capabilities);

            return new RemoteWebDriver(URI.create(remoteUrl).toURL(), capabilities);

        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid remote URL for platform '" + platform + "': " + remoteUrl, e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to remote platform '" + platform + "' at " + remoteUrl, e);
        }
    }

    /**
     * Resolves the execution platform from configuration.
     * Priority: System property (-DrunOn=...) > Environment variable >
     * config.properties
     * 
     * @return Execution platform: local, grid, perfecto, aws
     */
    private String getRunOn() {
        String runOn = ConfigManager.getProperty("runOn");
        if (runOn == null || runOn.trim().isEmpty()) {
            LOG.warn("'runOn' property not set, defaulting to 'local'");
            return "local";
        }
        return runOn.toLowerCase();
    }

    /**
     * Resolves the remote URL based on the platform.
     * 
     * @param platform Remote platform: grid, perfecto, aws
     * @return Remote URL for the platform
     */
    private String getRemoteUrl(String platform) {
        String urlKey = platform + ".url";
        String url = ConfigManager.getProperty(urlKey);

        if (url == null || url.trim().isEmpty()) {
            throw new IllegalStateException(
                    "Missing remote URL for platform '" + platform + "'. " +
                            "Set '" + urlKey + "' in config.properties or via system property.");
        }

        return url;
    }
}