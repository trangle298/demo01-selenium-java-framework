package pages.components;

import base.BasePage;
import org.openqa.selenium.interactions.Actions;
import utils.DateTimeNormalizer;
import model.enums.MovieDropdownField;
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
    // ---- Elements ----
    // ============================================

    @FindBy(xpath = "//div[@id='homeTool']//button")
    private WebElement btnFindTickets;

    private PopupDialog dlgMissingFilter;

    // ============================================
    // ---- Constructor ----
    // ============================================
    public ChainedDropdownsHome(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
        this.dlgMissingFilter = new PopupDialog(driver);
    }

    // ============================================
    // ---- Public Methods  ----
    // ============================================

    // ---- Wait Methods ----
    // Wait for first movie option to load / be present
    public void waitForDropdownsToLoad() {
        WebElement selMovie = getSelectFilter(MovieDropdownField.MOVIE);
        Actions actions = new Actions(driver);
        actions.scrollToElement(selMovie).perform();

        By movieOption = getOptionLocator(MovieDropdownField.MOVIE);
        waitForNestedElementToBePresent(selMovie, movieOption);
    }

    // ---- Actions ----
    // Interactions with dropdowns and buttons
    public void clickApplyFilterBtn() {
        LOG.info("Click Find Tickets button");
        // need to force click because current component is partially overlapped / hidden under top bar (UI bug)
        safeClick(btnFindTickets);
    }

    public void selectMovieByMovieTitle(String movieTitle) {
        selectDropdownOptionByVisibleText(getSelectFilter(MovieDropdownField.MOVIE), movieTitle);
    }

    public void selectCinemaBranchByName(String cinemaLocation) { 
        selectDropdownOptionByVisibleText(getSelectFilter(MovieDropdownField.CINEMA), cinemaLocation);
    }

    public void selectShowtimeById(String showtimeId) {
        selectDropdownOptionByValue(getSelectFilter(MovieDropdownField.SHOWTIME), showtimeId);
    }

    public void selectAllFiltersAndConfirm(String movieTitle, String cinemaLocation, String showtimeId) {
        LOG.info("Apply filters: movie = " + movieTitle + ", " +
                "cinema = " +cinemaLocation + ", showtime = " + showtimeId + " and click find tickets");
        selectMovieByMovieTitle(movieTitle);
        selectCinemaBranchByName(cinemaLocation);
        selectShowtimeById(showtimeId);
        clickApplyFilterBtn();
    }

    // ---- Getters ----
    // Get mapping of option IDs to titles/names/datetimes for each dropdown
    public Map<String, String> getMovieOptionIdToTitleMap() {
        Map<String, String> movieTitlesWithIds = new HashMap<>();
        By bySelMovie = getSelectLocator(MovieDropdownField.MOVIE);
        By byOptionMovie = getOptionLocator(MovieDropdownField.MOVIE);

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
        List<WebElement> cinemaOptions;

        By bySelCinema = getSelectLocator(MovieDropdownField.CINEMA);
        By byOptionCinema = getOptionLocator(MovieDropdownField.CINEMA);

        try {
           cinemaOptions = waitForAllNestedElementsToBePresent(bySelCinema, byOptionCinema);
        } catch (Exception e) {
            LOG.warn("No cinema options found in dropdown");
            return cinemaNamesWithIds; // Return empty map if no options found
        }

        for (WebElement option : cinemaOptions) {
            String name = getText(option);
            String id = getFieldValue(option);
            cinemaNamesWithIds.put(id, name);
        }
        return cinemaNamesWithIds;
    }

    public Map<String, String> getShowtimeOptionIdToDateTimeMap() {
        Map<String, String> showtimeDatetimesWithIds = new HashMap<>();
        By bySelShowtime = getSelectLocator(MovieDropdownField.SHOWTIME);
        By byOptionShowtime = getOptionLocator(MovieDropdownField.SHOWTIME);
        List<WebElement> showtimeOptions;
        try {
            showtimeOptions = waitForAllNestedElementsToBePresent(bySelShowtime, byOptionShowtime);
        } catch (Exception e) {
            LOG.warn("No showtime options found in dropdown");  // consider logging currently selected movie/cinema for debugging
            return showtimeDatetimesWithIds;                    // Return empty map if no options found
        }

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
    private By getSelectLocator(MovieDropdownField field) {
        String selectName = field.getLabel();
        if (selectName == null) {
            LOG.warn("Unknown filter type: " + field);
            return null;
        }
        String selectXPath = String.format("//select[@name='%s']", selectName);
        return By.xpath(selectXPath);
    }

    private WebElement getSelectFilter(MovieDropdownField field) {
        By bySelFilter = getSelectLocator(field);
        return waitForVisibilityOfElementLocatedBy(bySelFilter);
    }

    private By getOptionLocator(MovieDropdownField field) {
        String selectName = field.getLabel();
        if (selectName == null) {
            LOG.warn("Unknown filter type: " + field);
            return null;
        }
        String optionXPath = String.format("//select[@name='%s']//option[not(@disabled)]", selectName);
        return By.xpath(optionXPath);
    }

}