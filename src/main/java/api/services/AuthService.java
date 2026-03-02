package api.services;

import api.ApiClient;
import api.ApiConfig;
import api.ApiConstants;
import config.TestConfig;
import io.restassured.response.Response;
import model.api.request.LoginRequestPayload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthService {

        private static final Logger LOG = LogManager.getLogger(AuthService.class);

        private static final ApiClient apiClient = new ApiClient(ApiConfig.getBaseUri());

        private static String cachedAdminToken;

        public static synchronized String getAdminToken() {
                if (cachedAdminToken == null) {
                        LoginRequestPayload payload = new LoginRequestPayload(
                                        TestConfig.getUsername(),
                                        TestConfig.getPassword());

                        Response response = apiClient
                                        .withBody(payload)
                                        .post(ApiConstants.USER_LOGIN_ENDPOINT);

                        if (response.statusCode() != 200) {
                                throw new RuntimeException(
                                                "Admin login failed. Status: " + response.statusCode() +
                                                                ", Body: " + response.body().asString() + "payload: " + payload);
                        }

                        cachedAdminToken = response.jsonPath().getString("accessToken");
                        LOG.info("Admin token acquired successfully (length: " + cachedAdminToken.length() + ")");
                }
                return cachedAdminToken;
        }
}