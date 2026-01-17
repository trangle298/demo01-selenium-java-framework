package pages;

import pages.components.ChainedDropdownsHome;
import config.Routes;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for Home page.
 * Contains showtime filter dropdowns component.
 */
public class HomePage extends CommonPage {

    public ChainedDropdownsHome showtimeFilterDropdowns;

    public HomePage(WebDriver driver) {
        super(driver);
        showtimeFilterDropdowns = new ChainedDropdownsHome(driver);
    }

    public void navigateToHomePage() {
        driver.get(url(Routes.HOME));
    }

    public boolean isOnHomePage() {
        return waitForUrlContains(Routes.HOME);
    }

}
