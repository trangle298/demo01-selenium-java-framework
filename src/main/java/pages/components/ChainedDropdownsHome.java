package pages.components;

import base.BasePage;
import model.FilterType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Page component for chained showtime filter dropdowns on home page.
 * Handles movie, cinema, and showtime selection with dynamic options.
 */
public class ChainedDropdownsHome extends BasePage {

    // ============================================
    // ---- Component Elements ----
    // ============================================

    // ---- Dropdowns ----
    @FindBy(css = "select[name='film']")
    private WebElement selMovie;
    @FindBy(css = "select[name='cinema']")
    private WebElement selCinemaLocation;
    @FindBy(css = "select[name='date']")
    private WebElement selShowtime;

    // ---- Buttons ----
    @FindBy(xpath = "//div[@id='homeTool']//button")
    private WebElement btnFindTickets;

    // ---- Alerts ----
    @FindBy(css = "div[role='dialog']")
    private WebElement alertMissingFilter;
    @FindBy (xpath = "//div[@role='dialog']//h2")
    private WebElement lblMissingFilterAlertText;
    @FindBy(xpath = "//div[@role='dialog']//button[text()='Đã hiểu']")
    private WebElement btnCloseAlert;

    // ---- Static Fields & Initialization ----
    // Map for FilterType to HTML Select Name Mapping
    private Map<FilterType, String> filterSelectNameMap;

    /**
     * Initialize mapping for FilterType to HTML select name attributes.
     * This eliminates switch statements in getOptionLocator().
     */
    private void initializeFilterSelectNameMap() {
        // Map FilterType to HTML select name attribute
        filterSelectNameMap = new HashMap<>();
        filterSelectNameMap.put(FilterType.movie, "film");
        filterSelectNameMap.put(FilterType.cinema, "cinema");
        filterSelectNameMap.put(FilterType.showtime, "date");
    }

    // ============================================
    // ---- Constructor ----
    // ============================================
    public ChainedDropdownsHome(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
        initializeFilterSelectNameMap();
    }

    // ============================================
    // ---- Public Methods  ----
    // ============================================

    // ---- Wait Methods ----
    public void waitForDropdownsToLoad() {
        LOG.info("Wait for movie, cinema location, and showtime dropdowns to load");
        waitForVisibilityOfElementLocated(selMovie);
        waitForMovieOptionsToLoad();
    }

    public void waitForMovieOptionsToLoad(){
        By movieOption = getOptionLocator(FilterType.movie);
        waitForNestedElementToBePresent(selMovie, movieOption);
    }

    // ---- Interaction Methods ----
    public void clickFindTickets() {
        LOG.info("Click Find Tickets button");
        click(btnFindTickets);
    }

    public void selectMovieByMovieTitle(String movieTitle) {
        LOG.info("Select movie with title: " + movieTitle);
        selectDropdownOptionByVisibleText(selMovie, movieTitle);
    }

    public void selectCinemaLocationByName(String cinemaLocation) {
        LOG.info("Select cinema location with name: " + cinemaLocation);
        selectDropdownOptionByVisibleText(selCinemaLocation, cinemaLocation);
    }

    public void selectShowtimeById(String showtimeId) {
        LOG.info("Select showtime with ID: " + showtimeId);
        selectDropdownOptionByValue(selShowtime, showtimeId);
    }

    public void applyFiltersAndFindTickets(String movieTitle, String cinemaLocation, String showtimeId) {
        LOG.info("Apply filters and find tickets");
        selectMovieByMovieTitle(movieTitle);
        selectCinemaLocationByName(cinemaLocation);
        selectShowtimeById(showtimeId);
        clickFindTickets();
    }

    // ---- Getters ----
    public List<String> getMovieOptionsText() {
        By locator = getOptionLocator(FilterType.movie);
        return getAllOptionsText(selMovie, locator);
    }

    public List<String> getCinemaLocationOptionsText() {
        By locator = getOptionLocator(FilterType.cinema);
        return getAllOptionsText(selCinemaLocation, locator);
    }

    public List<String> getShowtimeOptionsText() {
        By locator = getOptionLocator(FilterType.showtime);
        return getAllOptionsText(selShowtime, locator);
    }

    // ---- Missing Filter Alerts Methods ----
    public void triggerMissingMovieFilterAlert() {
        clickFindTickets();
    }

    public void triggerMissingCinemaLocationFilterAlert(String movieTitle) {
        selectMovieByMovieTitle(movieTitle);
        clickFindTickets();
    }

    public void triggerMissingShowtimeFilterAlert(String movieTitle, String cinemaLocation) {
        selectMovieByMovieTitle(movieTitle);
        selectCinemaLocationByName(cinemaLocation);
        clickFindTickets();
    }

    /**
     * Trigger missing filter alert by selecting filters before the specified missing one.
     * Uses ordinal-based logic: movie (0) &lt; cinema (1) &lt; showtime (2).
     *
     * @param missingFilter The filter that should be missing (not selected)
     * @param movieTitle Movie title to select (if needed)
     * @param cinemaLocation Cinema location to select (if needed)
     */
    public void triggerMissingFilterAlert(FilterType missingFilter, String movieTitle, String cinemaLocation) {
        switch (missingFilter) {
            case movie:
                triggerMissingMovieFilterAlert();
                break;
            case cinema:
                triggerMissingCinemaLocationFilterAlert(movieTitle);
                break;
            case showtime:
                triggerMissingShowtimeFilterAlert(movieTitle, cinemaLocation);
                break;
            default:
                LOG.warn("Unknown missing filter: " + missingFilter);
        }
    }

    public boolean isMissingFilterAlertVisible() {
        return isElementDisplayedShort(alertMissingFilter);
    }

    public String getMissingFilterAlertText() {
        return getText(lblMissingFilterAlertText);
    }

    // ============================================
    // ---- Private Helper Methods ----
    // ============================================
    /**
     * Get the locator for dropdown options based on filter type.
     * Uses Map lookup instead of switch for better maintainability.
     *
     * @param filterType The type of filter (movie, cinema, showtime)
     * @return By locator for the dropdown options
     */
    private By getOptionLocator(FilterType filterType) {
        String selectName = filterSelectNameMap.get(filterType);
        if (selectName == null) {
            LOG.warn("Unknown filter type: " + filterType);
            return null;
        }
        String optionXPath = String.format("//select[@name='%s']//option[not(@disabled)]", selectName);
        return By.xpath(optionXPath);
    }
}
