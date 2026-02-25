package api;

import config.ConfigManager;

public class ApiConfig {

    public static String getBaseUri() {
        String uri = ConfigManager.getProperty("api.uri");
        if (uri != null && !uri.trim().isEmpty())
            return uri;
        return buildApiBaseUriFromEnv();
    }

    private static String buildApiBaseUriFromEnv() {
        String env = ConfigManager.getEnv();
        String key = "api.env." + env + ".host";

        String host = ConfigManager.getRequiredConfigProperty(key);
        return String.format(ApiConstants.baseUri_PATTERN, host);
    }
}
