package helpers;

import org.openqa.selenium.WebDriver;
import org.testng.Reporter;
import org.testng.asserts.SoftAssert;
import reports.ExtentReportManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static reports.ExtentReportManager.captureScreenshot;

/**
 * Soft assertion utilities for use across all test classes and helper classes.
 *
 * Can be called from:
 * - Test classes (directly or via BaseTest)
 * - Helper classes (AuthVerificationHelper, BookingVerificationHelper, etc.)
 *
 * DESIGN PATTERN: Centralized soft assertion logic with ExtentReport integration
 * and automatic screenshot capture.
 *
 * WHEN TO USE SOFT ASSERTIONS (this class):
 * - Multiple related checks in one test
 * - Form validation (check all field errors, not just first)
 * - UI state verification (multiple elements)
 * - E2E flows with multiple verification points
 *
 * WHEN TO USE HARD ASSERTIONS (TestNG Assert):
 * - Prerequisites/setup failures (login, navigation)
 * - Single-check tests
 * - Critical path failures (if it fails, rest is meaningless)
 *
 * IMPORTANT: Always call softAssert.assertAll() at the end of your test!
 *
 * For detailed guidance, see ASSERTION_STRATEGY_GUIDE.md in the project root.
 */
public class SoftAssertionHelper {

    private static final Logger LOG = LogManager.getLogger(SoftAssertionHelper.class);

    /**
     * Verify a condition is true with soft assertion.
     * AUTOMATICALLY CAPTURES SCREENSHOT when assertion fails.
     *
     * @param condition The boolean condition to verify
     * @param description Description of what is being verified
     * @param driver WebDriver instance for screenshot capture
     * @param softAssert The SoftAssert instance to collect failures
     * @return true if passed, false if failed
     */
    public static boolean verifySoftTrue(boolean condition, String description, WebDriver driver, SoftAssert softAssert) {
        if (condition) {
            ExtentReportManager.pass(description);
            LOG.info("Assertion PASSED: " + description);
            return true;
        } else {
            ExtentReportManager.fail(description + " - FAILED");
            captureScreenshot(driver, Reporter.getCurrentTestResult().getName());
            softAssert.fail(description);
            return false;
        }
    }

    /**
     * Verify a condition is false with soft assertion.
     * AUTOMATICALLY CAPTURES SCREENSHOT when assertion fails.
     *
     * @param condition The boolean condition to verify (should be false)
     * @param description Description of what is being verified
     * @param driver WebDriver instance for screenshot capture
     * @param softAssert The SoftAssert instance to collect failures
     * @return true if passed (was false), false if failed (was true)
     */
    public static boolean verifySoftFalse(boolean condition, String description, WebDriver driver, SoftAssert softAssert) {
        if (!condition) {
            ExtentReportManager.pass(description);
            LOG.info("Assertion PASSED: " + description);
            return true;
        } else {
            ExtentReportManager.fail(description + " - FAILED");
            captureScreenshot(driver, Reporter.getCurrentTestResult().getName());
            softAssert.fail(description);
            return false;
        }
    }

    /**
     * Verify two strings are equal with soft assertion.
     * AUTOMATICALLY CAPTURES SCREENSHOT when assertion fails.
     *
     * @param actual The actual string value
     * @param expected The expected string value
     * @param fieldToCheck Description of what field is being checked
     * @param driver WebDriver instance for screenshot capture
     * @param softAssert The SoftAssert instance to collect failures
     */
    public static void verifySoftEquals(String actual, String expected, String fieldToCheck, WebDriver driver, SoftAssert softAssert) {
        if (actual.equals(expected)) {
            ExtentReportManager.pass(fieldToCheck + " is correct");
            LOG.info("Assertion PASSED: " + fieldToCheck + " is correct");
        } else {
            String message = fieldToCheck + " is incorrect: actual='" + actual + "', expected='" + expected + "'";
            ExtentReportManager.fail(message);
            captureScreenshot(driver, Reporter.getCurrentTestResult().getName());
            softAssert.fail(message);
        }
    }
}
