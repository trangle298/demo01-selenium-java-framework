package config;

import config.enums.Browser;
import config.enums.RunOn;

public class TestConfig {

    private TestConfig() {
    }

    public static RunOn getRunOn() {
        return RunOn.fromName(ConfigManager.getProperty("runOn"));
    }

    public static Browser getBrowser() {
        return Browser.fromName(ConfigManager.getProperty("browser"));
    }

    public static String getUsername() {
        return ConfigManager.getRequiredEnvProperty("admin.username");
    }

    public static String getPassword() {
        return ConfigManager.getRequiredEnvProperty("admin.password");
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