package config;

import io.github.cdimascio.dotenv.Dotenv;
import model.enums.UserType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static config.urlConstants.BASE_URL_PATTERN;

/**
 * Centralized configuration manager supporting layered configuration from
 * system properties, OS environment variables, .env files, and
 * config.properties, with environment-aware resolution and typed accessors.
 */
public class ConfigManager {

    // ============================================================
    // Constants & Fields
    // ============================================================
    private static final Logger LOG = LogManager.getLogger(ConfigManager.class);
    private static final Properties properties = new Properties();
    private static Dotenv dotenv;

    private static final String DEFAULT_ENV = "qa";

    private static final int DEFAULT_EXPLICIT_WAIT = 10;
    private static final int DEFAULT_SHORT_WAIT = 2;
    private static final int DEFAULT_LONG_WAIT = 20;

    // Resolve environment ONCE
    private static final String ENV = resolveEnv();

    // ============================================================
    // Static Initialization
    // ============================================================
    static {
        loadProperties();
        loadEnv();  // Must run before any method that accesses dotenv
    }

    // ============================================================
    // Environment Resolution
    // ============================================================
    private static String resolveEnv() {
        String env = System.getProperty("env");
        return isEmpty(env) ? DEFAULT_ENV : env.toLowerCase();
    }

    public static String getEnv() {
        return ENV;
    }

    // ============================================================
    // Core Property Resolution
    // ============================================================
    /**
     * Resolves a configuration value using the following priority order:
     *
     * <ol>
     *   <li>JVM System Property (-Dkey=value)</li>
     *   <li>OS Environment Variable (key or KEY_WITH_UNDERSCORES)</li>
     *   <li>Environment-specific .env file (loaded via dotenv)</li>
     *   <li>config.properties (classpath)</li>
     * </ol>
     *
     * @param key configuration key
     * @return resolved value, or null if not found in any source
     */
    public static String getProperty(String key) {
        String value = System.getProperty(key);
        if (!isEmpty(value)) return value;

        value = System.getenv(key);
        if (!isEmpty(value)) return value;

        String envKey = key.toUpperCase().replace('.', '_');
        value = System.getenv(envKey);
        if (!isEmpty(value)) return value;

        if (dotenv != null) {
            value = dotenv.get(key);
            if (!isEmpty(value)) return value;
        }

        return properties.getProperty(key);
    }

    // ============================================================
    // Public Config API
    // ============================================================
    public static String getRequiredProperty(String key) {
        String value = getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("Missing required config key: " + key);
        }
        return value;
    }

    public static String getBaseUrl() {
        String url = getProperty("base.url");
        if (!isEmpty(url)) return url;

        return buildBaseUrlFromEnv();
    }

    public static String getUsername(UserType type) {
        return getRequiredProperty(type.usernameKey());
    }

    public static String getPassword(UserType type) {
        return getRequiredProperty(type.passwordKey());
    }

    public static String getEmail(UserType type) {
        return getRequiredProperty(type.emailKey());
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

    // ============================================================
    // Specialized Builders
    // ============================================================

    /**
     * Builds base URL from -Denv JVM property
     */
    private static String buildBaseUrlFromEnv() {
        String env = getEnv();
        String key = "env." + env + ".host";
        String host = getProperty(key);

        if (isEmpty(host)) {
            throw new IllegalStateException(
                    "Missing host mapping for environment '" + env +
                            "'. Expected property: " + key
            );
        }
        return String.format(BASE_URL_PATTERN, host);
    }

    // ============================================================
    // Internal Helpers
    // ============================================================

    /**
     * Get an integer property from config with a default fallback.
     * Supports system property override via -D{key}={value}
     * 
     * @param key The property key
     * @param defaultValue The default value if property not found or invalid
     * @return The property value as integer
     */
    private static int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);

        if (!isEmpty(value)) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                LOG.warn("Invalid integer value for property '{}': {}", key, value);
            }
        }
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

    // ============================================================
    // Loading Methods
    // ============================================================

    private static void loadProperties() {
        try(InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream("config.properties")) {
            if(input == null) {
                LOG.warn("config.properties file not found on classpath");
                return;
            }
            // Load as UTF-8 to support non-ASCII characters in config values
            try (InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                properties.load(reader);
            }
            LOG.info("Properties loaded successfully from config.properties");
        } catch (IOException e) {
            LOG.error("Error loading properties file", e);
        }
    }

    private static void loadEnv() {
        String env = getEnv();
        String envFileName = ".env." + env;

        try {
            dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .filename(envFileName)
                    .load();

            LOG.info("Loaded configuration for environment: {}", env);

        } catch (Exception e) {
            LOG.error("Error loading .env file: {}", envFileName, e);
        }
    }
}