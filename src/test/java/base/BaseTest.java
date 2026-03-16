package base;

import config.TestConfig;
import config.enums.Browser;
import drivers.DriverManagerFactory;
import config.enums.RunOn;
import helpers.providers.TestUserProvider;
import model.UserAccount;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

import common.GlobalVariables;
import reports.ExtentReportManager;
import utils.GridHealthCheckRetry;

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

    protected static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static ThreadLocal<UserAccount> testUser = new ThreadLocal<>();

    private static final String REQUIRE_USER_GROUP = "requiresUser";

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        ThreadContext.put("testName", "suite");
        LOG.info("===== SUITE STARTED =====");
        RunOn runOn = TestConfig.getRunOn();
        if (runOn == RunOn.GRID) {
            // boolean ready = GridHealthCheck.isGridReady(TestConfig.getHubUrl() + "/status");
            boolean ready = GridHealthCheckRetry.waitUntilGridReady(TestConfig.getHubUrl() + "/status");
            if (!ready) {
                LOG.error("Grid is NOT ready. Aborting execution");
                System.exit(0);
            }
        }
        System.setProperty("runOutputDir", GlobalVariables.RUN_OUTPUT_DIR);
        ExtentReportManager.initializeExtentReports();
        LOG.info("Run output dir: " + GlobalVariables.RUN_OUTPUT_DIR);
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(Method method) {
        ThreadContext.put("testName", method.getName());
        ThreadContext.put("threadId", String.valueOf(Thread.currentThread().threadId()));
        ThreadContext.put("logTarget", "file");
        initializeWebDriver(TestConfig.getBrowser());
        ExtentReportManager.createTest(method.getName());
        setupTestUserIfNeeded(method);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(ITestResult result) {
        logTestResult(result);
        cleanupTestUser();
        cleanupWebDriver();
        ThreadContext.clearAll();
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        ThreadContext.put("testName", "suite");
        ExtentReportManager.flushReports();
        LOG.info("===== SUITE FINISHED =====");
        ThreadContext.clearAll();
    }

    protected WebDriver getDriver() {
        return driver.get();
    }

    protected UserAccount getTestUser() {
        return testUser.get();
    }

    // --- Private Helpers ----
    private void initializeWebDriver(Browser browser) {
        driver.set(DriverManagerFactory.getDriverManager(browser).createDriver());
        LOG.info("[Thread: " + Thread.currentThread().threadId() + "] WebDriver started: " + getDriver().toString());
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
            requiresUser = Arrays.asList(testAnnotation.groups()).contains(REQUIRE_USER_GROUP);
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
            // Capture screenshot for hard assertion failures (exceptions,
            // NoSuchElementException, etc.)
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
            ExtentReportManager.pass("Test PASSED successfully");

        } else if (result.getStatus() == ITestResult.SKIP) {
            ExtentReportManager.skip("Test SKIPPED: " + result.getThrowable());
        }
    }
}