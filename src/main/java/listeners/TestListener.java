package listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    public static final Logger logger = LogManager.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        logger.info("===== START TEST: " + methodName + " =====");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        logger.info("===== PASSED TEST: " + methodName + " =====");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        logger.error("===== FAILED TEST: " + methodName + " =====", result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        logger.warn("===== SKIPPED TEST: " + methodName + " =====");
    }

    @Override
    public void onStart(ITestContext context) {
        logger.info(">>> START TEST SUITE: " + context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info(">>> FINISH TEST SUITE: " + context.getName());
    }
}
