package pages;

import config.Routes;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

/**
 * Page Object for Showtime Booking page.
 * Handles seat selection and ticket booking.
 */
public class ShowtimePage extends CommonPage {

    // Available seats: buttons that are NOT disabled and NOT the "ĐẶT VÉ" (Purchase) button
    @FindBy(xpath = "//button[not(@disabled)][not(contains(., 'ĐẶT VÉ'))]")
    private List<WebElement> btnAvailableSeats;

    @FindBy(xpath = "//button[contains(., 'ĐẶT VÉ')]")
    private WebElement btnBookTickets;

    @FindBy(css = "div[role='dialog'] h2")
    private WebElement alertBooking;

    public ShowtimePage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public void navigateToShowtimePage(String showtimeId) {
        LOG.info("Navigate to Booking page for showtime: " + showtimeId);
        driver.get(url(String.format(Routes.SHOWTIME, showtimeId)));
    }

    /**
     * Wait for the showtime page to fully load.
     * Waits for the booking button to be visible, which is always present regardless of seat availability.
     * Use this after page refresh or navigation to ensure page is ready.
     */
    public void waitForPageToLoad() {
        LOG.info("Waiting for showtime page to load");
        isElementDisplayed(btnBookTickets);
    }

    public void selectAvailableSeat(String seatNumber) {
        LOG.info("Select Available Seat: " + seatNumber);
        By seatLocator = By.xpath(String.format("//button[.='%s']", seatNumber));
        WebElement seatElement = driver.findElement(seatLocator);

        click(seatElement);
    }

    public void selectAvailableSeats(List<String> seatNumbers) {
        LOG.info("Select Available Seats: " + seatNumbers);
        for (String seatNumber : seatNumbers) {
            selectAvailableSeat(seatNumber);
        }
    }

    public void clickBookTicketsButton() {
        LOG.info("Click Book Tickets button");
        click(btnBookTickets);
    }

    public List<String> getAvailableSeatNumbers() {
        LOG.info("Get Available Seat Numbers");
        waitForPageToLoad();
        return btnAvailableSeats.stream()
                .map(WebElement::getText)
                .toList();
    }

    /**
     * Check if a specific seat is available (exists and is clickable).
     * Uses default wait timeout since seats load with the page.
     *
     * @param seatNumber The seat number to check (e.g., "A1", "B5")
     * @return true if seat exists and is available, false otherwise
     */
    public boolean isSeatAvailable(String seatNumber) {
        try {
            By seatLocator = By.xpath(String.format("//button[.='%s']", seatNumber));
            WebElement seatElement = driver.findElement(seatLocator);
            return isElementDisplayed(seatElement);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if multiple seats are all available.
     *
     * @param seatNumbers List of seat numbers to check
     * @return true if all seats are available, false if any seat is unavailable
     */
    public boolean areSeatsAvailable(List<String> seatNumbers) {
        return seatNumbers.stream().allMatch(this::isSeatAvailable);
    }

    public String getBookingAlertText() {
        return getText(alertBooking);
    }

}
