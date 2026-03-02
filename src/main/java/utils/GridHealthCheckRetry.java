package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class GridHealthCheckRetry {

    private static final Logger LOG = LogManager.getLogger(GridHealthCheckRetry.class);
    private static final int MAX_RETRIES = 5;
    private static final int RETRY_INTERVAL_MS = 3000;
    private static final int TIMEOUT_MS = 5000;

    public static boolean waitUntilGridReady(String statusEndpoint) {
        int attempt = 0;

        while (attempt < MAX_RETRIES) {
            attempt++;
            try {
                LOG.info("[GridHealthCheck] Attempt " + attempt + " checking: " + statusEndpoint);

                Response response = RestAssured
                        .given()
                        .relaxedHTTPSValidation()
                        .config(RestAssured.config()
                                .httpClient(
                                        RestAssured.config()
                                                .getHttpClientConfig()
                                                .setParam("http.connection.timeout", TIMEOUT_MS)
                                                .setParam("http.socket.timeout", TIMEOUT_MS)))
                        .when()
                        .get(statusEndpoint);

                if (response.statusCode() == 200) {
                    Boolean ready = response.jsonPath().getBoolean("value.ready");
                    String message = response.jsonPath().getString("value.message");

                    LOG.info("[GridHealthCheck] Status Ready: " + ready + ", Message: " + message);
                    if (ready) {
                        return true;
                    }
                }

            } catch (Exception e) {
                LOG.error("[GridHealthCheck] Attempt " + attempt + " failed: " + e.getMessage());
            }
            sleep(RETRY_INTERVAL_MS);
        }
        return false;
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }
}