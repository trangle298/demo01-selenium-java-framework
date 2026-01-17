package base;

import config.ConfigManager;
import drivers.DriverManagerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;
import reports.ExtentReportManager;

import java.lang.reflect.Method;

/**
 * Base class for all test classes.
 * Manages WebDriver lifecycle, ExtentReports, and TestNG hooks.
 * Supports parallel test execution using ThreadLocal.
 */
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
        // Get browser name from system property, config.properties, or default to "chrome"
        String browserName = System.getProperty("browser");
        if (browserName == null || browserName.isEmpty()) {
            browserName = ConfigManager.getProperty("browser");
        }
        if (browserName == null || browserName.isEmpty()) {
            browserName = "chrome"; // Default fallback
        }

        // Create a new WebDriver for this thread
        driver.set(DriverManagerFactory.getDriverManager(browserName).createDriver());
        getDriver().manage().window().maximize();

        LOG.info("Thread: " + Thread.currentThread().threadId() + " - [setUp] - WebDriver Instance: " + driver);
        LOG.info("Starting test: " + method.getName());

        // Create ExtentTest for this method
        ExtentReportManager.createTest(method.getName());
        ExtentReportManager.info("WebDriver initialized and browser maximized");
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(ITestResult result) {
        // Log test result to ExtentReport based on test status
        if (result.getStatus() == ITestResult.FAILURE) {
            LOG.error("Test FAILED: " + result.getName());

            // Capture screenshot for hard assertion failures (exceptions, NoSuchElementException, etc.)
            // Soft assertion failures already capture screenshots inline
            Throwable throwable = result.getThrowable();
            boolean isSoftAssertFailure = throwable != null &&
                    throwable.getMessage() != null &&
                    throwable.getMessage().contains("The following asserts failed");

            if (!isSoftAssertFailure) {
                // Hard failure (exception) - capture screenshot
                ExtentReportManager.captureScreenshot(getDriver(), result.getName());
            }

            String errorMsg = throwable != null ? throwable.getMessage() : "Unknown error";
            ExtentReportManager.fail("Test failed: " + errorMsg);

        } else if (result.getStatus() == ITestResult.SUCCESS) {
            LOG.info("Test PASSED: " + result.getName());
            ExtentReportManager.pass("Test completed successfully");

        } else if (result.getStatus() == ITestResult.SKIP) {
            LOG.warn("Test SKIPPED: " + result.getName());
            ExtentReportManager.skip("Test skipped: " + result.getThrowable());
        }

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
}

