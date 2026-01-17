package config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Manages configuration properties from config.properties file.
 * Supports runtime overrides via system properties and environment variables.
 */
public class ConfigManager {
    
    // ---- Static Fields / Constants ---- //
    private static final Logger logger = LogManager.getLogger(ConfigManager.class);
    private static final Properties properties = new Properties();

    // Default timeout values (used as fallback if config.properties is missing/invalid)
    private static final int DEFAULT_EXPLICIT_WAIT = 10;
    private static final int DEFAULT_SHORT_WAIT = 3;
    private static final int DEFAULT_LONG_WAIT = 20;

    // ---- Static Initialization ---- //
    static {
        // Load properties from classpath once at class initialization so other methods can rely on them
        loadProperties();
    }
    
    // ---- Public Methods ---- //
    public static void loadProperties() {
        try(InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream("config.properties")) {
            if(input == null) {
                logger.warn("config.properties file not found on classpath");
                return;
            }
            // Load as UTF-8 to support non-ASCII characters in config values
            try (InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                properties.load(reader);
            }
            logger.info("Properties loaded successfully from config.properties");
        } catch (IOException e) {
            logger.error("Error loading properties file", e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Returns the resolved base URL to be used by tests/pages.
     * Precedence (highest -> lowest):
     * 1) JVM property -Dbase.url
     * 2) Environment variables BASE_URL
     * 3) config.properties entry base.url
     * 4) hardcoded default
     */
    public static String getBaseUrl() {
        String url = System.getProperty("base.url");

        if (isEmpty(url)) {
            url = System.getenv("BASE_URL");
        }

        if (isEmpty(url)) {
            url = properties.getProperty("base.url");
        }

        if (isEmpty(url)) {
            url = "https://demo1.cybersoft.edu.vn";
        }

        return url;
    }

    /**
     * Get the default explicit wait timeout in seconds.
     * Used by WebDriverWait in BasePage for standard wait operations.
     * 
     * @return Timeout in seconds (default: 10)
     */
    public static int getExplicitWait() {
        return getIntProperty("explicit.wait", DEFAULT_EXPLICIT_WAIT);
    }

    /**
     * Get the short wait timeout in seconds.
     * Used for quick checks like error messages or alerts that appear immediately.
     * 
     * @return Timeout in seconds (default: 3)
     */
    public static int getShortWait() {
        return getIntProperty("short.wait", DEFAULT_SHORT_WAIT);
    }

    /**
     * Get the long wait timeout in seconds.
     * Used for slow operations like API calls, page redirects, or complex interactions.
     * 
     * @return Timeout in seconds (default: 20)
     */
    public static int getLongWait() {
        return getIntProperty("long.wait", DEFAULT_LONG_WAIT);
    }

    // ---- Private Helper Methods ---- //
    /**
     * Get an integer property from config with a default fallback.
     * Supports system property override via -D{key}={value}
     * 
     * @param key The property key
     * @param defaultValue The default value if property not found or invalid
     * @return The property value as integer
     */
    private static int getIntProperty(String key, int defaultValue) {
        // 1. Check system property first (allows runtime override via -D flag)
        String systemValue = System.getProperty(key);
        if (!isEmpty(systemValue)) {
            try {
                return Integer.parseInt(systemValue);
            } catch (NumberFormatException e) {
                logger.warn("Invalid integer value for system property '{}': {}", key, systemValue);
            }
        }

        // 2. Check config.properties
        String propertyValue = properties.getProperty(key);
        if (!isEmpty(propertyValue)) {
            try {
                return Integer.parseInt(propertyValue);
            } catch (NumberFormatException e) {
                logger.warn("Invalid integer value for property '{}': {}", key, propertyValue);
            }
        }

        // 3. Return default
        return defaultValue;
    }

    /**
     * Check if a string is null, empty, or contains only whitespace.
     * 
     * @param s The string to check
     * @return true if string is null, empty (""), or whitespace-only ("  ")
     */
    private static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

}
