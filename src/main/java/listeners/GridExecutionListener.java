package listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IExecutionListener;

import config.TestConfig;
import config.enums.RunOn;
import utils.GridHealthCheckRetry;

public class GridExecutionListener implements IExecutionListener {

    private static final Logger LOG = LogManager.getLogger(GridExecutionListener.class);

    @Override
    public void onExecutionStart() {
        RunOn runOn = TestConfig.getRunOn();

        if (runOn == RunOn.GRID) {

            LOG.info("=== GLOBAL EXECUTION START: GRID HEALTH CHECK ===");
            LOG.info(">>> runOn resolved to: {}", runOn);

            boolean ready = GridHealthCheckRetry.waitUntilGridReady(TestConfig.getHubUrl() + "/status");

            if (!ready) {
                LOG.error("Selenium Grid is NOT ready after retries.");
                LOG.error("ABORTING ENTIRE TEST EXECUTION NOW.");

                throw new RuntimeException("FATAL: Selenium Grid is not ready. Aborting execution.");
            }

            LOG.info("Selenium Grid is ready. Proceeding with test execution.");
            LOG.info("==================================================");
        }
    }

    @Override
    public void onExecutionFinish() {
        LOG.info("=== GLOBAL EXECUTION FINISH ===");
    }
}