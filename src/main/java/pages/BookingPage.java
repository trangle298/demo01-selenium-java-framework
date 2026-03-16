package pages;

import config.urlConstants;
import model.enums.BookingSummaryField;
import model.ui.ShowtimeDetails;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import pages.components.PopupDialog;

import java.time.LocalDateTime;
import java.util.List;

import static utils.DateTimeNormalizer.normalize;

/**
 * Page Object for Showtime Booking page.
 * Handles seat selection and ticket booking.
 */
public class BookingPage extends CommonPage {
    // ============================================
    // ---- Page Elements ----
    // ============================================

    // Available seats: buttons that are NOT disabled and NOT the "ĐẶT VÉ" (Purchase) button
    @FindBy(xpath = "//button[not(@disabled)][not(contains(., 'ĐẶT VÉ'))]")
    private List<WebElement> btnAvailableSeats;

    @FindBy(xpath = ".//button[not(.='ĐẶT VÉ')]")
    private List<WebElement> btnAllSeats;

    @FindBy(xpath = "//button[contains(., 'ĐẶT VÉ')]")
    private WebElement btnBookTickets;

    @FindBy(xpath = "//button[.='ĐẶT VÉ']//ancestor::div[1]")
    private WebElement divSummarySection;

    // ---- Components ----
    // Popup dialog for booking response - success, empty selection error, unauthenticated error
    private PopupDialog dlgResponse;

    // ============================================
    // Constructor
    // ============================================
    public BookingPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
        this.dlgResponse = new PopupDialog(driver);
    }

    // ============================================
    // ---- Public Methods ----
    // ============================================

    // ---- Navigation ----
    public void navigateToShowtimePage(String showtimeId) {
        LOG.info("Navigate to Booking page for showtime: " + showtimeId);
        driver.get(url(String.format(urlConstants.SHOWTIME, showtimeId)));
    }

    // ---- Wait Helpers ----
    /**
     * Wait for the seat map to fully load - regardless of seat availability
     * Use this after page refresh or navigation to ensure page is ready.
     */
    public void waitForSeatMapToLoad() {
        LOG.info("Waiting for seat map to load");
        waitForVisibilityOfAllElementsLocated(btnAllSeats);
    }

    // ---- Interactions with seat map and dialog ----
    public void selectSeatBySeatNumber(String seatNumber) {
        LOG.info("Select Seat Number: " + seatNumber);
        By seatLocator = By.xpath(String.format("//button[.='%s']", seatNumber));
        WebElement seatElement = waitForVisibilityOfElementLocatedBy(seatLocator);

        click(seatElement);

        // Wait for seat to be marked as selected (background color change)
        By selectedSeatLocator = By.xpath(String.format("//button[.='%s'][contains(@style, 'background-color')]", seatNumber));
        waitForVisibilityOfElementLocatedBy(selectedSeatLocator);
    }

    public void selectSeatsBySeatNumbers(List<String> seatNumbers) {
        for (String seatNumber : seatNumbers) {
            selectSeatBySeatNumber(seatNumber);
        }
    }

    public void clickBookTicketsButton() {
        LOG.info("Click Book Tickets button");
        scrollIntoView(btnBookTickets);
        safeClick(btnBookTickets);
    }

    public void confirmAndCloseDialog() {
        dlgResponse.clickConfirmButton();
        dlgResponse.waitForDialogToBeInvisible();
    }

    public void denyAndCloseDialog() {
        dlgResponse.clickDenyButton();
        dlgResponse.waitForDialogToBeInvisible();
    }

    // Confirm booking and get purchase timestamp based on success dialog appearance -- will update next
    public String confirmBookingAndGetPurchaseTimestamp() {
        clickBookTicketsButton();
        dlgResponse.waitForDialogToBeVisible();

        LocalDateTime now = LocalDateTime.now(); // Get current date and time
        String datetimeString = normalize(now);

        return datetimeString;
    }

    // ---- Getters ----
    // Seat availability states and seat numbers
    public List<String> getAvailableSeatNumbers() {
        LOG.info("Get Available Seat Numbers");
        waitForSeatMapToLoad();
        return btnAvailableSeats.stream()
                .map(WebElement::getText)
                .toList();
    }

    public boolean isSeatAvailable(String seatNumber) {
        waitForSeatMapToLoad();
        try {
            By seatLocator = By.xpath(String.format("//button[.='%s']", seatNumber));
            WebElement seatElement = waitForVisibilityOfElementLocatedBy(seatLocator);
            return isElementDisplayed(seatElement);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean areSeatsAvailable(List<String> seatNumbers) {
        return seatNumbers.stream().allMatch(this::isSeatAvailable);
    }

    // Summary section details
    public ShowtimeDetails getShowtimeDetailsFromSummary() {
        ShowtimeDetails showtimeDetails = new ShowtimeDetails();

        showtimeDetails.setMovieName(getSummaryFieldValue(BookingSummaryField.MOVIE_NAME));
        showtimeDetails.setCinemaBranchName(getSummaryFieldValue(BookingSummaryField.CINEMA_BRANCH_NAME));
        showtimeDetails.setCinemaAddress(getSummaryFieldValue(BookingSummaryField.CINEMA_ADDRESS));
        showtimeDetails.setTheaterName(getSummaryFieldValue(BookingSummaryField.THEATER_NAME));
        showtimeDetails.setShowtimeDateTime(normalize(getSummaryFieldValue(BookingSummaryField.SHOWING_DATETIME)));

        return showtimeDetails;
    }

    public List<String> getSelectedSeatNumbersInSummary() {
        String seatNumbersStr = getSummaryFieldValue(BookingSummaryField.SEAT_NUMBERS).replaceAll("Ghế", "");
        return List.of(seatNumbersStr.split(",\\s*"));
    }

    public String getTotalPriceInSummary() {
        return getSummaryFieldValue(BookingSummaryField.PRICE);
    }

    // Dialog visibility and text
    public boolean isBookingDialogDisplayed() {
        return dlgResponse.isDialogDisplayed();
    }

    public String getBookingDialogHeader() {
        return dlgResponse.getDialogTitle();
    }

    // ============================================
    // ---- Private Helper Methods ----
    // ============================================
    private WebElement getSummaryFieldElement(BookingSummaryField field) {
        String labelText = field.getLabel();

        if (labelText == null) {
            LOG.warn("Unknown booking summary field type: " + field);
            return null;
        }

        String fieldXPath = String.format(".//*[text()='%s']//parent::div", labelText);
        By fieldLocator = By.xpath(fieldXPath);

        return waitForVisibilityOfNestedElementLocatedBy(divSummarySection, fieldLocator);
    }

    private String getSummaryFieldValue(BookingSummaryField field) {
        WebElement fieldElement = getSummaryFieldElement(field);
        String fullText = getText(fieldElement);

        // Extract the value after the label text
        String labelText = field.getLabel();
        return fullText.replace(labelText, "").trim();
    }

}