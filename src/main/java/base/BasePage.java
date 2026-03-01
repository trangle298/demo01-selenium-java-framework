package base;

import config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Base class for all Page Objects.
 * Provides common wait helpers, actions, and utility methods.
 */
public class BasePage {

    protected final Logger LOG = LogManager.getLogger(getClass());
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        // Use configurable timeout from config.properties (default: 10 seconds)
        int explicitWait = ConfigManager.getExplicitWait();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait));
        PageFactory.initElements(driver, this);
    }

    // ---- Wait Helpers --- //
    // Wait for element to meet condition - presence, visibility, clickability, etc.
    public WebElement waitForVisibilityOfElementLocated(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    public WebElement waitForVisibilityOfElementLocatedBy(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public List<WebElement> waitForVisibilityOfAllElementsLocated(List<WebElement> elements) {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(ConfigManager.getLongWait()));
        return longWait.until(ExpectedConditions.visibilityOfAllElements(elements));
    }

    public List<WebElement> waitForVisibilityOfAllElementsLocatedBy(By locator) {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(ConfigManager.getLongWait()));
        return longWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public WebElement waitForElementToBeClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public WebElement waitForVisibilityOfNestedElementLocatedBy(WebElement parentElement, By childLocator) {
        return wait.until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(parentElement, childLocator)).get(0);
    }

    public List<WebElement> waitForVisibilityOfNestedElementsLocatedBy(WebElement parentElement, By childLocator) {
        return wait.until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(parentElement, childLocator));
    }

    public List<WebElement> waitForAllNestedElementsToBePresent(By parentElement, By childLocator) {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(ConfigManager.getLongWait()));
        return longWait.until(ExpectedConditions.presenceOfNestedElementsLocatedBy(parentElement, childLocator));
    }

    public void waitForNestedElementToBePresent(WebElement parentElement, By childLocator) {
        wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(parentElement, childLocator));
    }

    public void waitForInvisibilityOfElementLocated(WebElement element) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(ConfigManager.getShortWait()));
        shortWait.until(ExpectedConditions.invisibilityOf(element));
    }

    public void waitForInvisibilityOfElementLocatedBy(By locator) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(ConfigManager.getShortWait()));
        shortWait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    // Wait for URL / partial URL
    public boolean waitForUrl(String expectedUrl) {
        // Normalize expected URL (remove trailing slash for comparison)
        String normalizedExpected = expectedUrl.endsWith("/") ? expectedUrl.substring(0, expectedUrl.length() - 1) : expectedUrl;

        try {
            // Wait for URL to match (with normalization)
            Boolean result = wait.until(driver -> {
                String currentUrl = driver.getCurrentUrl();
                if (currentUrl == null) {
                    return false;
                }
                String normalizedCurrent = currentUrl.endsWith("/") ? currentUrl.substring(0, currentUrl.length() - 1) : currentUrl;
                return normalizedCurrent.equals(normalizedExpected);
            });
            return result != null && result;
        } catch (Exception e) {
            LOG.warn("Wait for URL: " + expectedUrl + " - FAILED. Current URL: " + driver.getCurrentUrl());
            return false;
        }
    }

    public boolean waitForUrlContains(String urlFragment) {
        try {
            Boolean result = wait.until(ExpectedConditions.urlContains(urlFragment));
            return result != null && result;
        } catch (Exception e) {
            LOG.warn("Wait for URL containing: " + urlFragment + " - FAILED. Current URL: " + driver.getCurrentUrl());
            return false;
        }
    }

    // ---- Common Actions ---- //
    // Refresh the current page
    public void refreshPage() {
        String currentUrl = driver.getCurrentUrl();
        driver.get(currentUrl);
    }

    // Type text into input field
    public void enterText(WebElement element, String value) {
        waitForVisibilityOfElementLocated(element);
        element.sendKeys(value);
    }

    // Blur field
    public void blurField(WebElement field) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].blur();", field);
    }

    // Clear input field
    public void clear(WebElement element) {
        Actions actions = new Actions(driver);
        waitForVisibilityOfElementLocated(element);
        actions.click(element)
                .keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).sendKeys(Keys.DELETE)
                .perform();
    }

    // Scroll element into view
    public void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    // Click element
    public void click(WebElement element) {
        waitForElementToBeClickable(element);
        element.click();
    }

    public void safeClick(WebElement element) {
        try {
            click(element);
        } catch (Exception e) {
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].click();", element);
        }
    }

    // Select dropdown option by value or visible text
    public void selectDropdownOptionByValue(WebElement dropdown, String value) {
        Select select = new Select(dropdown);
        By optionLocator = By.xpath(String.format("//option[@value='%s']", value));

        waitForNestedElementToBePresent(dropdown, optionLocator);
        select.selectByValue(value);
    }

    public void selectDropdownOptionByVisibleText(WebElement dropdown, String visibleText) {
        Select select = new Select(dropdown);
        By optionLocator = By.xpath(String.format("//option[text()='%s']", visibleText));

        waitForNestedElementToBePresent(dropdown, optionLocator);
        select.selectByVisibleText(visibleText);
    }

    // ---- Getters ---- //
    // Get text and attribute values - with trimming
    public String getText(WebElement element) {
        waitForVisibilityOfElementLocated(element);
        return element.getText().trim();
    }

    public String getFieldValue(WebElement field) {
        waitForVisibilityOfElementLocated(field);
        return field.getAttribute("value").trim();
    }

    // ---- Utility Methods ---- //
    // Check if element is displayed (returns false instead of throwing exception), default timeout specified in wait
    public boolean isElementDisplayed(WebElement element) {
        try {
            waitForVisibilityOfElementLocated(element);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isElementDisplayed(By locator) {
        try {
            waitForVisibilityOfElementLocatedBy(locator);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isElementNotDisplayed(By locator) {
        try {
            waitForInvisibilityOfElementLocatedBy(locator);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Check if element is displayed with custom timeout
    public boolean isElementDisplayedCustom(WebElement element, int timeoutSeconds) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            customWait.until(ExpectedConditions.visibilityOf(element));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if element is displayed using short timeout from config.properties.
     * Use for quick checks like error messages or alerts that appear immediately.
     * Default: 3 seconds (configurable via short.wait property)
     */
    public boolean isElementDisplayedShort(WebElement element) {
        int shortWait = ConfigManager.getShortWait();
        return isElementDisplayedCustom(element, shortWait);
    }

    /**
     * Check if element is displayed using long timeout from config.properties.
     * Use for slow operations like API responses, page redirects, or complex interactions.
     * Default: 20 seconds (configurable via long.wait property)
     */
    public boolean isElementDisplayedLong(WebElement element) {
        int longWait = ConfigManager.getLongWait();
        return isElementDisplayedCustom(element, longWait);
    }

    // Build full URL from base URL and path
    protected String url(String path) {
        if (path == null || path.isEmpty())
            return ConfigManager.getBaseUrl();
        return ConfigManager.getBaseUrl() + path;
    }

}
