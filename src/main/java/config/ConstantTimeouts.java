package config;

public class ConstantTimeouts {

    public static final long DEFAULT_TIMEOUT = ConfigManager.getExplicitWait();
    public static final long SHORT_TIMEOUT = ConfigManager.getShortWait();
    public static final long LONG_TIMEOUT = ConfigManager.getLongWait();
}
