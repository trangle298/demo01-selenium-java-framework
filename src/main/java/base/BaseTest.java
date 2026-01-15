package base;

import drivers.DriverManagerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import reports.ExtentReportManager;

import java.lang.reflect.Method;

import static reports.ExtentReportManager.captureScreenshot;

public class BaseTest {

    protected final Logger LOG = LogManager.getLogger(getClass());

    // ThreadLocal ensures each parallel thread gets its own WebDriver instance
    // Required for parallel="tests" or parallel="classes" in testng.xml
    protected ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        LOG.info("Before Suite executed");
        ExtentReportManager.initializeExtentReports();
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(Method method) {
        // Create a new WebDriver for this thread
        driver.set(DriverManagerFactory.getDriverManager("chrome").createDriver());
        getDriver().manage().window().maximize();

        LOG.info("Thread: " + Thread.currentThread().threadId() + " - [setUp] - WebDriver Instance: " + driver);
        LOG.info("Starting test: " + method.getName());

        // Create ExtentTest for this method
        ExtentReportManager.createTest(method.getName());
        ExtentReportManager.info("WebDriver initialized and browser maximized");
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(ITestResult result) {
        getDriver().quit();
        driver.remove(); // Remove ThreadLocal reference
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        ExtentReportManager.flushReports();
        LOG.info("Test Suite completed");
    }

    protected WebDriver getDriver() {
        return driver.get();
    }

    // --------------------------
    // Reusable assertion helpers for all test classes
    // --------------------------

    /**
     * Verify a condition is true with soft assertion
     * @return true if condition passed, false if failed
     */
    protected boolean verifySoftTrue(boolean condition, String description, SoftAssert softAssert) {
        if (condition) {
            ExtentReportManager.pass(description);
            LOG.info("Assertion PASSED: " + description);
            return true;
        } else {
            ExtentReportManager.fail(description + " - FAILED");
            captureScreenshot(getDriver(), Reporter.getCurrentTestResult().getName());
            softAssert.fail(description);
            return false;
        }
    }

    /**
     * Verify a condition is false with soft assertion
     * @return true if condition passed (was false), false if failed (was true)
     */
    protected boolean verifySoftFalse(boolean condition, String description, SoftAssert softAssert) {
        if (!condition) {
            ExtentReportManager.pass(description);
            LOG.info("Assertion PASSED: " + description);
            return true;
        } else {
            ExtentReportManager.fail(description + " - FAILED");
            captureScreenshot(getDriver(), Reporter.getCurrentTestResult().getName());
            softAssert.fail(description);
            return false;
        }
    }

    /**
     * Verify two strings are equal with soft assertion
     */
    protected void verifySoftEquals(String actual, String expected, String fieldToCheck, SoftAssert softAssert) {
        if (actual.equals(expected)) {
            ExtentReportManager.pass(fieldToCheck + " is correct");
            LOG.info("Assertion PASSED: " + fieldToCheck + " is correct");
        } else {
            String message = fieldToCheck + " is incorrect: actual='" + actual + "', expected='" + expected + "'";
            ExtentReportManager.fail(message);
            captureScreenshot(getDriver(), Reporter.getCurrentTestResult().getName());
            softAssert.fail(message);
        }
    }

}

