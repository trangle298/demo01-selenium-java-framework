package listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG listener for logging test execution events.
 * Logs test start, success, failure, and skip events to Log4j.
 */
public class TestListener implements ITestListener {

    public static final Logger LOG = LogManager.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        LOG.info("===== START TEST: " + methodName + " =====");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        double elapsed = (result.getEndMillis() - result.getStartMillis()) / 1000.0;
        LOG.info(String.format("===== PASSED TEST: %s (%.1fs) =====", methodName, elapsed));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        double elapsed = (result.getEndMillis() - result.getStartMillis()) / 1000.0;
        LOG.error(String.format("===== FAILED TEST: %s (%.1fs) =====", methodName, elapsed), result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        double elapsed = (result.getEndMillis() - result.getStartMillis()) / 1000.0;
        LOG.warn(String.format("===== SKIPPED TEST: %s (%.1fs) =====", methodName, elapsed));
    }

    @Override
    public void onStart(ITestContext context) {
        LOG.info(">>> START TEST SUITE: " + context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        LOG.info(">>> FINISH TEST SUITE: " + context.getName());
    }
}
