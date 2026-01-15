package drivers;

public class DriverManagerFactory {

    public static DriverManager getDriverManager(String browserName) {
        if (browserName.equalsIgnoreCase("chrome")) {
            return new ChromeDriverManager();
        } else if (browserName.equalsIgnoreCase("firefox")) {
            return new FirefoxDriverManager();
        } else if (browserName.equalsIgnoreCase("safari")) {
            return new SafariDriverManager();
        } else {
            return new EdgeDriverManager();
        }
    }
}
