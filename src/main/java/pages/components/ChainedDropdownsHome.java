package pages.components;

import base.BasePage;
import model.FilterType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class ChainedDropdownsHome extends BasePage {

    @FindBy(css = "select[name='film']")
    private WebElement selMovie;

    @FindBy(xpath = "//select[@name='film']//option[not(@disabled)]")
    private List<WebElement> movieOptions;

    @FindBy(css = "select[name='cinema']")
    private WebElement selCinemaLocation;

    @FindBy(css = "select[name='date']")
    private WebElement selShowtime;

    @FindBy(xpath = "//div[@id='homeTool']//button")
    private WebElement btnFindTickets;

    @FindBy(css = "div[role='dialog']")
    private WebElement alertMissingFilter;

    @FindBy (xpath = "//div[@role='dialog']//h2")
    private WebElement lblMissingFilterAlertText;

    @FindBy(xpath = "//div[@role='dialog']//button[text()='Đã hiểu']")
    private WebElement btnCloseAlert;

    public ChainedDropdownsHome(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // Wait Methods
    public void waitForDropdownsToLoad() {
        LOG.info("Wait for movie, cinema location, and showtime dropdowns to load");
        waitForVisibilityOfElementLocated(selMovie);
        waitForVisibilityOfElementLocated(selCinemaLocation);
        waitForVisibilityOfElementLocated(selShowtime);
    }

    public void waitForMovieOptionsToLoad(){
        By movieOption = getOptionLocator(FilterType.movie);
        waitForNestedElementToBePresent(selMovie, movieOption);
    }

    public void waitForCinemaOptionsToLoad(){
        By cinemaOption = getOptionLocator(FilterType.cinema);
        waitForNestedElementToBePresent(selCinemaLocation, cinemaOption);
    }

    public void waitForShowtimeOptionsToLoad(){
        By showtimeOption = getOptionLocator(FilterType.showtime);
        waitForNestedElementToBePresent(selShowtime, showtimeOption);
    }

    public By getOptionLocator(FilterType filterType){
        String optionXPath = "//select[@name='%s']//option[not(@disabled)]";
        String name = "";
        switch (filterType) {
            case movie -> name = "film";
            case cinema -> name = "cinema";
            case showtime -> name = "date";
            default -> LOG.warn("Unknown filter type: " + filterType);
        }
        By optionLocator = By.xpath(String.format(optionXPath, name));
        return optionLocator;
    }

    // Interaction Methods
    public void clickFindTickets() {
        LOG.info("Click Find Tickets button");
        click(btnFindTickets);
    }

    public void selectMovieByMovieTitle(String movieTitle) {
        LOG.info("Select movie with title: " + movieTitle);
        By optionLocator = By.xpath(String.format("//option[text()='%s']", movieTitle));
        wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(selMovie, optionLocator));
        selectDropdownOptionByVisibleText(selMovie, movieTitle);
    }

    public void selectCinemaLocationByName(String cinemaLocation) {
        LOG.info("Select cinema location with name: " + cinemaLocation);
        By optionLocator = By.xpath(String.format("//option[text()='%s']", cinemaLocation));
        wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(selCinemaLocation, optionLocator));
        selectDropdownOptionByVisibleText(selCinemaLocation, cinemaLocation);
    }

    public void selectShowtimeById(String showtimeId) {
        By optionLocator = By.xpath(String.format("//option[@value='%s']", showtimeId));
        wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(selShowtime, optionLocator));

        String showtimeText = driver.findElement(optionLocator).getText();
        LOG.info("Select showtime: " + showtimeText + " with ID: " + showtimeId);

        selectDropdownOptionByValue(selShowtime, showtimeId);
    }

    public void applyFiltersAndFindTickets(String movieTitle, String cinemaLocation, String showtimeId) {
        LOG.info("Apply filters and find tickets");
        selectMovieByMovieTitle(movieTitle);
        selectCinemaLocationByName(cinemaLocation);
        selectShowtimeById(showtimeId);
        clickFindTickets();
    }

    // Methods to trigger missing filter alerts & interaction
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

    public void closeMissingFilterAlert() {
        click(btnCloseAlert);
    }

    // Getter Methods
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



    public boolean isMissingFilterAlertVisible() {
        return isElementDisplayed(alertMissingFilter, 5);
    }

    public String getMissingFilterAlertText() {
        return getText(lblMissingFilterAlertText);
    }

}
