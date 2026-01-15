package pages;

import config.Routes;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

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
        waitForVisibilityOfAllElements(btnAvailableSeats);
        return btnAvailableSeats.stream()
                .map(WebElement::getText)
                .toList();
    }

    public boolean isSeatAvailable(String seatNumber) {
        By seatLocator = By.xpath(String.format("//button[.='%s']", seatNumber));
        List<WebElement> seats = driver.findElements(seatLocator);
        return !seats.isEmpty();
    }

    public boolean areSeatsAvailable(List<String> seatNumbers) {
        for (String seatNumber : seatNumbers) {
            if (!isSeatAvailable(seatNumber)) {
                return false;
            }
        }
        return true;
    }

    public String getBookingAlertText() {
        return getText(alertBooking);
    }

}
