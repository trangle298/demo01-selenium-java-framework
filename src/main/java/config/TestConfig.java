package config;

import config.enums.Browser;
import config.enums.Environment;
import config.enums.RunOn;

public class TestConfig {

    private TestConfig() {
    }

    public static RunOn getRunOn() {
        return RunOn.fromName(ConfigManager.getProperty("runOn"));
    }

    public static Environment getEnvironment() {
        return Environment.fromName(ConfigManager.getEnv());
    }

    public static Browser getBrowser() {
        return Browser.fromName(ConfigManager.getProperty("browser"));
    }

    public static String getBaseUrl() {
        return ConfigManager.getBaseUrl();
    }

    public static String getUsername() {
        return ConfigManager.getRequiredEnvProperty("username");
    }

    public static String getPassword() {
        return ConfigManager.getRequiredEnvProperty("password");
    }

    public static int getExplicitWait() {
        return ConfigManager.getExplicitWait();
    }

    public static int getShortWait() {
        return ConfigManager.getShortWait();
    }

    public static int getLongWait() {
        return ConfigManager.getLongWait();
    }

    public static String getHubUrl() {
        String hubKey = getRunOn().getName() + ".url";
        String hubUrl = ConfigManager.getProperty(hubKey);

        if (hubUrl == null || hubUrl.trim().isEmpty()) {
            throw new IllegalStateException("Missing property for key: " + hubKey);
        }
        return hubUrl;
    }

}
