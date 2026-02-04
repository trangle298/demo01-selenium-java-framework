package config;

/**
 * Application route constants.
 */
public final class urlConstants {

    // Base URL pattern, %s will be replaced by host name mapped to environment
    public static final String BASE_URL_PATTERN = "https://%s.cybersoft.edu.vn";

    // Page routes
    public static final String HOME = "/";
    public static final String LOGIN = "/sign-in";
    public static final String REGISTER = "/sign-up";
    public static final String ACCOUNT = "/account";
    public static final String SHOWTIME = "/purchase/%s";

    private urlConstants() {}

}
