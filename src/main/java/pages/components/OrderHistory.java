package pages.components;

import base.BasePage;
import model.ui.OrderEntry;
import model.enums.OrderEntryField;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mozilla.javascript.TopLevel.Builtins.RegExp;
import static utils.DateTimeNormalizer.normalize;

public class OrderHistory extends BasePage {

    // ============================================
    // ---- Component Elements ----
    // ============================================

    // Current elements does not have unique IDs or classes, using XPath with heavy reliance on text to locate
    // Should be updated if the application adds better selectors

    // Find the main container by locating the div that contains a h1 with text 'Lịch sử đặt vé' (Order History)
    @FindBy(xpath = "//div[contains(@class,'container')][.//h1='Lịch sử đặt vé']")
    private WebElement divOrderHistoryContainer;

    // Find all order entry divs by locating divs that contain a h3 with text 'Ngày đặt' (Order Date)
    // Should add unique identifiers such as order IDs
    @FindBy(xpath = "//h3[contains(text(),'Ngày đặt')]/ancestor::div[contains(@class,'container')][1]")
    private List<WebElement> divOrderEntries;

    public OrderHistory(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // ============================================
    // ---- Public Methods  ----
    // ============================================

    // ---- Getters ----
    public boolean isOrderHistoryDisplayed() {
        return isElementDisplayed(divOrderHistoryContainer);
    }

    public Integer getOrderCount() {
        try {
            waitForVisibilityOfAllElementsLocated(divOrderEntries);
        } catch (WebDriverException e) {
            LOG.info("No order entries found in order history.");
            return 0;
        }
        return divOrderEntries.size();
    }

    public OrderEntry getLastOrderEntryDetails() {
        By byOrderEntries = By.xpath("//h3[contains(text(),'Ngày đặt')]/ancestor::div[contains(@class,'container')][1]");
        List<WebElement> lastEntry = waitForVisibilityOfAllElementsLocatedBy(byOrderEntries);
        return getOrderEntryDetails(lastEntry.getLast());
    }

    // ============================================
    // ---- Private Methods  ----
    // ============================================
    private WebElement getOrderEntryFieldElement(WebElement divOrderEntry, OrderEntryField field) {
        By byFieldLocator;

        // Special case for Cinema Branch Name which does not have a field label
        // Currently identified as the h1 below order history title that does not contain 'Tên phim:'
        if (field.equals(OrderEntryField.CINEMA_BRANCH_NAME)) {
            byFieldLocator = By.xpath("//h1[text()='Lịch sử đặt vé']//following::h1[not(contains(text(),'Tên phim:'))]");
        }

        else {
            String labelText = field.getLabel();
            if (labelText == null) {
                LOG.warn("Unknown order entry field type: " + field);
                return null;
            }
            String fieldXPath = String.format("//h1[text()='Lịch sử đặt vé']//following::*[contains(text(), '%s')]", labelText);
            byFieldLocator = By.xpath(fieldXPath);
        }
        return waitForVisibilityOfNestedElementLocatedBy(divOrderEntry, byFieldLocator);
    }

    private String getOrderEntryFieldValue(WebElement divOrderEntry, OrderEntryField field) {
        WebElement fieldElement = getOrderEntryFieldElement(divOrderEntry, field);
        String fullText = getText(fieldElement);

        // Extract the value after the label text
        String labelText = field.getLabel();
        if (field.equals(OrderEntryField.CINEMA_BRANCH_NAME) || field.equals(OrderEntryField.THEATER_NAME)) {
            return fullText.trim(); // No label to remove
        }

        return fullText.replaceAll(labelText + ":?", "").trim();
    }

    private String getPurchaseDatetimeNormalized(WebElement orderDiv) {
        String purchaseDatetime = getOrderEntryFieldValue(orderDiv, OrderEntryField.PURCHASE_DATETIME);
        return normalize(purchaseDatetime);
    }

    private Integer getPriceInVND(WebElement orderDiv) {
        String priceText = getOrderEntryFieldValue(orderDiv, OrderEntryField.PRICE);
        String price = priceText.replace("VND", "").trim();
        return Integer.parseInt(price);
    }

    private List<String> getSeatNumbersList(WebElement orderDiv) {
        String seatNumbersStr = getOrderEntryFieldValue(orderDiv, OrderEntryField.SEAT_NUMBERS);
        return List.of(seatNumbersStr.split(" "));
    }

    private OrderEntry getOrderEntryDetails(WebElement orderDiv) {
        OrderEntry orderDetails = new OrderEntry();

        String movieName = getOrderEntryFieldValue(orderDiv, OrderEntryField.MOVIE_NAME);
        String branchName = getOrderEntryFieldValue(orderDiv, OrderEntryField.CINEMA_BRANCH_NAME);
        String theaterName = getOrderEntryFieldValue(orderDiv, OrderEntryField.THEATER_NAME);
        String purchaseDatetime = getPurchaseDatetimeNormalized(orderDiv);
        Integer price = getPriceInVND(orderDiv);
        List<String> seatNumbers = getSeatNumbersList(orderDiv);

        orderDetails.setPurchaseDatetime(purchaseDatetime)
                .setMovieName(movieName)
                .setPrice(price)
                .setCinemaBranchName(branchName)
                .setTheaterName(theaterName)
                .setSeatNumbers(seatNumbers);

        return orderDetails;
    }

}
