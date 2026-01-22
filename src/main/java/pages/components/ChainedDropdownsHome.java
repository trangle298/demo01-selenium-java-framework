package pages.components;

import base.BasePage;
import utils.DateTimeNormalizer;
import model.ui.MovieDropdownFields;
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
 * NOTE: Can extract to a generic chained dropdowns component if needed (current website only has this one instance).
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

    // ---- Components ----
    private PopupDialog dlgMissingFilter;

    // ---- Static Fields & Initialization ----
    // Map for FilterType to HTML Select Name
    private Map<MovieDropdownFields, String> filterSelectNameMap;

    /**
     * Initialize mapping for FilterType to HTML select name attributes.
     * This eliminates switch statements in getOptionLocator().
     */
    private void initializeFilterSelectNameMap() {
        // Map FilterType to HTML select name attribute
        filterSelectNameMap = new HashMap<>();
        filterSelectNameMap.put(MovieDropdownFields.MOVIE, "film");
        filterSelectNameMap.put(MovieDropdownFields.CINEMA, "cinema");
        filterSelectNameMap.put(MovieDropdownFields.SHOWTIME, "date");
    }

    // ============================================
    // ---- Constructor ----
    // ============================================
    public ChainedDropdownsHome(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
        initializeFilterSelectNameMap();
        this.dlgMissingFilter = new PopupDialog(driver);
    }

    // ============================================
    // ---- Public Methods  ----
    // ============================================

    // ---- Wait Methods ----
    public void waitForDropdownsToLoad() {
        waitForVisibilityOfElementLocated(selMovie);
        waitForMovieOptionsToLoad();
    }

    public void waitForMovieOptionsToLoad(){
        By movieOption = getOptionLocator(MovieDropdownFields.MOVIE);
        waitForNestedElementToBePresent(selMovie, movieOption);
    }

    // ---- Actions ----
    // Interactions with dropdowns and buttons
    public void clickApplyFilterBtn() {
        LOG.info("Click Find Tickets button");
        click(btnFindTickets);
    }

    public void selectMovieByMovieTitle(String movieTitle) {
        selectDropdownOptionByVisibleText(selMovie, movieTitle);
    }

    public void selectCinemaBranchByName(String cinemaLocation) {
        selectDropdownOptionByVisibleText(selCinemaLocation, cinemaLocation);
    }

    public void selectShowtimeById(String showtimeId) {
        selectDropdownOptionByValue(selShowtime, showtimeId);
    }

    public void selectAllFiltersAndConfirm(String movieTitle, String cinemaLocation, String showtimeId) {
        LOG.info("Apply filters: movie = " + movieTitle + ", " +
                "cinema = " +cinemaLocation + ", showtime = " + showtimeId + "and click find tickets");
        selectMovieByMovieTitle(movieTitle);
        selectCinemaBranchByName(cinemaLocation);
        selectShowtimeById(showtimeId);
        clickApplyFilterBtn();
    }

    // Flows of actions to trigger specific missing filter alert
    /**
     * Trigger missing filter alert by selecting filters before the specified missing one.
     * Uses ordinal-based logic: movie (0) &lt; cinema (1) &lt; showtime (2).
     *
     * @param missingFilter The filter that should be missing (not selected)
     * @param movieTitle Movie title to select (if needed)
     * @param cinemaLocation Cinema location to select (if needed)
     */
    public void triggerMissingFilterAlert(MovieDropdownFields missingFilter, String movieTitle, String cinemaLocation) {
        switch (missingFilter) {
            case MOVIE:
                clickApplyFilterBtn();
                break;
            case CINEMA:
                selectMovieByMovieTitle(movieTitle);
                clickApplyFilterBtn();
                break;
            case SHOWTIME:
                selectMovieByMovieTitle(movieTitle);
                selectCinemaBranchByName(cinemaLocation);
                clickApplyFilterBtn();
                break;
            default:
                LOG.warn("Unknown missing filter: " + missingFilter);
        }
    }

    // ---- Getters ----
    // Get mapping of option IDs to titles/names/datetimes for each dropdown
    public Map<String, String> getMovieOptionIdToTitleMap() {
        Map<String, String> movieTitlesWithIds = new HashMap<>();
        By bySelMovie = getSelectLocator(MovieDropdownFields.MOVIE);
        By byOptionMovie = getOptionLocator(MovieDropdownFields.MOVIE);

        List<WebElement> movieOptions = waitForAllNestedElementsToBePresent(bySelMovie, byOptionMovie);

        for (WebElement option : movieOptions) {
            String title = getText(option);
            String id = getFieldValue(option);
            movieTitlesWithIds.put(id, title);
        }
        return movieTitlesWithIds;
    }

    public Map<String, String> getCinemaBranchOptionIdToNameMap() {
        Map<String, String> cinemaNamesWithIds = new HashMap<>();
        By bySelCinema = getSelectLocator(MovieDropdownFields.CINEMA);
        By byOptionCinema = getOptionLocator(MovieDropdownFields.CINEMA);

        try {
            waitForNestedElementToBePresent(selCinemaLocation, byOptionCinema);
        } catch (Exception e) {
            LOG.warn("No cinema options found in dropdown");
            return cinemaNamesWithIds; // Return empty map if no options found
        }

        List<WebElement> cinemaOptions = waitForAllNestedElementsToBePresent(bySelCinema, byOptionCinema);

        for (WebElement option : cinemaOptions) {
            String name = getText(option);
            String id = getFieldValue(option);
            cinemaNamesWithIds.put(id, name);
        }
        return cinemaNamesWithIds;
    }

    public Map<String, String> getShowtimeOptionIdToDateTimeMap() {
        Map<String, String> showtimeDatetimesWithIds = new HashMap<>();
        By bySelShowtime = getSelectLocator(MovieDropdownFields.SHOWTIME);
        By byOptionShowtime = getOptionLocator(MovieDropdownFields.SHOWTIME);

        try {
            waitForNestedElementToBePresent(selShowtime, byOptionShowtime);
        } catch (Exception e) {
            LOG.warn("No showtime options found in dropdown");  // consider logging currently selected movie/cinema for debugging
            return showtimeDatetimesWithIds;                    // Return empty map if no options found
        }

        List<WebElement> showtimeOptions = waitForAllNestedElementsToBePresent(bySelShowtime, byOptionShowtime);

        for (WebElement option : showtimeOptions) {
            String datetime = getText(option);
            String normalizedDatetime = DateTimeNormalizer.normalize(datetime);

            String id = getFieldValue(option);

            showtimeDatetimesWithIds.put(id, normalizedDatetime);
        }
        return showtimeDatetimesWithIds;
    }

    // Get missing filter alert state and text
    public boolean isMissingFilterAlertVisible() {
        return dlgMissingFilter.isDialogDisplayed();
    }

    public String getMissingFilterAlertText() {
        return dlgMissingFilter.getDialogTitle();
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
    private By getOptionLocator(MovieDropdownFields filterType) {
        String selectName = filterSelectNameMap.get(filterType);
        if (selectName == null) {
            LOG.warn("Unknown filter type: " + filterType);
            return null;
        }
        String optionXPath = String.format("//select[@name='%s']//option[not(@disabled)]", selectName);
        return By.xpath(optionXPath);
    }

    private By getSelectLocator(MovieDropdownFields filterType) {
        String selectName = filterSelectNameMap.get(filterType);
        if (selectName == null) {
            LOG.warn("Unknown filter type: " + filterType);
            return null;
        }
        String selectXPath = String.format("//select[@name='%s']", selectName);
        return By.xpath(selectXPath);
    }
}