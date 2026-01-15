package config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ConfigManager {

    private static final Logger logger = LogManager.getLogger(ConfigManager.class);
    private static final Properties properties = new Properties();

    static {
        // Load properties from classpath once at class initialization so other methods can rely on them
        loadProperties();
    }

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

    private static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

}
