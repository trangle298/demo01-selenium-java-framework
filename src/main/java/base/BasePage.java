package base;

import config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class BasePage {

    protected final Logger LOG = LogManager.getLogger(getClass());
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    // ---- Wait Helpers --- //
    // Wait for element to be visible
    public WebElement waitForVisibilityOfElementLocated(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    // Wait for all elements in list to be visible
    public void waitForVisibilityOfAllElements(List<WebElement> elements) {
        wait.until(ExpectedConditions.visibilityOfAllElements(elements));
    }

    // Wait for element to disappear
    public void waitForInvisibilityOfElementLocated(WebElement element) {
        wait.until(ExpectedConditions.invisibilityOf(element));
    }

    // Wait for element to be clickable
    public WebElement waitForElementToBeClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    // Wait for nested element to be present
    public WebElement waitForNestedElementToBePresent(WebElement parentElement, By childLocator) {
        return wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(parentElement, childLocator));
    }

    // Wait for exact URL match
    public boolean waitForUrl(String url) {
        try {
            Boolean result = wait.until(ExpectedConditions.urlToBe(url));
            return result != null && result;
        } catch (Exception e) {
            return false;
        }
    }

    // Wait for URL to contain specific text
    public boolean waitForUrlContains(String urlFragment) {
        try {
            Boolean result = wait.until(ExpectedConditions.urlContains(urlFragment));
            return result != null && result;
        } catch (Exception e) {
            return false;
        }
    }

    // ---- Common Actions ---- //
    // Refresh the current page
    public void refreshPage() {
        String currentUrl = driver.getCurrentUrl();
        driver.get(currentUrl);
        driver.navigate().to(currentUrl);
    }

    // Type text into input field
    public void sendKeys(WebElement element, String value) {
        waitForVisibilityOfElementLocated(element);
        element.sendKeys(value);
    }

    // Clear input field
    public void clearField(WebElement element) {
        Actions actions = new Actions(driver);
        waitForVisibilityOfElementLocated(element);
        actions.click(element)
                .keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).sendKeys(Keys.DELETE)
                .perform();
    }

    // Click element
    public void click(WebElement element) {
        waitForElementToBeClickable(element);
        element.click();
    }

    // Select dropdown option
    public void selectDropdownOptionByValue(WebElement dropdown, String value) {
        waitForVisibilityOfElementLocated(dropdown);
        Select select = new Select(dropdown);
        select.selectByValue(value);
    }

    public void selectDropdownOptionByVisibleText(WebElement dropdown, String visibleText) {
        waitForVisibilityOfElementLocated(dropdown);
        Select select = new Select(dropdown);
        select.selectByVisibleText(visibleText);
    }


    // ---- Getters ---- //
    // Get text from element
    public String getText(WebElement element) {
        waitForVisibilityOfElementLocated(element);
        return element.getText();
    }

    public String getFieldValue(WebElement field) {
        waitForVisibilityOfElementLocated(field);
        return field.getAttribute("value");
    }
    protected String url(String path) {
        if (path == null || path.isEmpty()) return ConfigManager.getBaseUrl();
        return ConfigManager.getBaseUrl() + path;
    }

    public List<String> getAllOptionsText(WebElement selectElement, By optionLocator) {
        waitForNestedElementToBePresent(selectElement, optionLocator);
        List<WebElement> elements = driver.findElements(optionLocator);

        List<String> elementTexts = new ArrayList<>();
        for (WebElement element : elements) {
            String text = getText(element); // Get the visible text of the element
            elementTexts.add(text); // Add the text to a new list of Strings
        }
        return elementTexts;
    }

    // ---- Utility Methods ---- //
    // Check if element is displayed (returns false instead of throwing exception)
    public boolean isElementDisplayed(WebElement element) {
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Check if element is displayed with custom timeout
    public boolean isElementDisplayed(WebElement element, int timeoutSeconds) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            customWait.until(ExpectedConditions.visibilityOf(element));
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
