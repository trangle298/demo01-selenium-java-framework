package pages;

import config.Routes;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import pages.components.PopupDialog;

import java.util.List;

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

    public void navigateToShowtimePage(String showtimeId) {
        LOG.info("Navigate to Booking page for showtime: " + showtimeId);
        driver.get(url(String.format(Routes.SHOWTIME, showtimeId)));
    }

    // ============================================
    // ---- Public Methods  ----
    // ============================================

    // ---- Wait Helpers  ----
    /**
     * Wait for the showtime page to fully load.
     * Waits for the booking button to be visible, which is always present regardless of seat availability.
     * Use this after page refresh or navigation to ensure page is ready.
     */
    public void waitForSeatMapToLoad() {
        LOG.info("Waiting for seat map to load");
        waitForVisibilityOfAllElementsLocated(btnAllSeats);
    }

    // ---- Actions ----
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
        click(btnBookTickets);
    }

    public void confirmAndCloseDialog() {
        dlgResponse.clickConfirmButton();
        dlgResponse.waitForDialogToBeInvisible();
    }

    public void denyAndCloseDialog() {
        dlgResponse.clickDenyButton();
        dlgResponse.waitForDialogToBeInvisible();
    }

    // ---- Getters ----
    // Get list of available seat numbers
    public List<String> getAvailableSeatNumbers() {
        LOG.info("Get Available Seat Numbers");
        waitForSeatMapToLoad();
        return btnAvailableSeats.stream()
                .map(WebElement::getText)
                .toList();
    }

    // Check single seat availability (button with seat number is present and visible)
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

    // Check multiple seats availability
    public boolean areSeatsAvailable(List<String> seatNumbers) {
        return seatNumbers.stream().allMatch(this::isSeatAvailable);
    }

    // Get booking dialog state and text
    public boolean isBookingDialogDisplayed() {
        return dlgResponse.isDialogDisplayed();
    }

    public String getBookingDialogHeader() {
        return dlgResponse.getDialogTitle();
    }
}
