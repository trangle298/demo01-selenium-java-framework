package base;

import config.ConfigManager;
import drivers.DriverManagerFactory;
import helpers.providers.TestUserProvider;
import model.UserAccount;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;
import reports.ExtentReportManager;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Base class for all test classes.
 * Manages WebDriver lifecycle, ExtentReports, TestNG hooks,
 * and provides/cleans up test user before/after method.
 * Supports parallel test execution using ThreadLocal.
 */
public class BaseTest {

    protected final Logger LOG = LogManager.getLogger(getClass());

    // ThreadLocal ensures each parallel thread gets its own WebDriver instance / test user
    // Required for parallel="tests" or parallel="classes" in user-management.xml
    protected ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static ThreadLocal<UserAccount> testUser = new ThreadLocal<>();

    private static final String REQUIRE_USER_GROUP = "requiresUser";

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        LOG.info("Initialize Extent Report");
        ExtentReportManager.initializeExtentReports();
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(Method method) {
        initializeWebDriver(resolveBrowser());
        ExtentReportManager.createTest(method.getName());
        setupTestUserIfNeeded(method);

        LOG.info("Starting test: " + method.getName());
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(ITestResult result) {
        logTestResult(result);
        cleanupTestUser();
        cleanupWebDriver();
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        ExtentReportManager.flushReports();
        LOG.info("Test Suite completed");
    }

    protected WebDriver getDriver() {
        return driver.get();
    }

    protected UserAccount getTestUser() {
        return testUser.get();
    }

    // --- Private Helpers ----
    private String resolveBrowser() {
        String browser = System.getProperty("browser");
        if (browser == null || browser.isEmpty()) {
            browser = ConfigManager.getProperty("browser");
        }
        return (browser == null || browser.isEmpty()) ? "chrome" : browser;
    }

    private void initializeWebDriver(String browserName) {
        driver.set(DriverManagerFactory.getDriverManager(browserName).createDriver());
        LOG.info("Thread: " + Thread.currentThread().threadId() +
                " - [setUp] - WebDriver Instance: " + getDriver());
    }

    private void cleanupWebDriver() {
        WebDriver webDriver = getDriver();
        if (webDriver != null) {
            webDriver.quit();
            driver.remove();
        }
    }

    private void setupTestUserIfNeeded(Method method) {
        Test testAnnotation = method.getAnnotation(Test.class);
        boolean requiresUser = true;
        if (testAnnotation != null) {
            requiresUser =
                    Arrays.asList(testAnnotation.groups()).contains(REQUIRE_USER_GROUP);
        }

        if (requiresUser) {
            testUser.set(TestUserProvider.createNewTestUser());
        }
    }

    private void cleanupTestUser() {
        UserAccount user = testUser.get();
        if (user != null) {
            TestUserProvider.deleteUser(user);
            testUser.remove();
        }
    }

    private void logTestResult(ITestResult result) {
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
            ExtentReportManager.fail("Test FAILED: " + errorMsg);

        } else if (result.getStatus() == ITestResult.SUCCESS) {
            LOG.info("Test PASSED: " + result.getName());
            ExtentReportManager.pass("Test PASSED successfully");

        } else if (result.getStatus() == ITestResult.SKIP) {
            LOG.warn("Test SKIPPED: " + result.getName());
            ExtentReportManager.skip("Test SKIPPED: " + result.getThrowable());
        }
    }
}