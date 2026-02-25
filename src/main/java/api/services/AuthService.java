package api.services;

import api.ApiClient;
import api.ApiConfig;
import api.ApiConstants;
import config.ConfigManager;
import model.api.request.LoginRequestPayload;
import model.enums.UserType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthService {

    private static final Logger LOG = LogManager.getLogger(AuthService.class);

    private static final ApiClient apiClient =
            new ApiClient(ApiConfig.getBaseUri());

    private static String cachedAdminToken;

    public static String getAdminToken() {
        if (cachedAdminToken == null) {
            LoginRequestPayload payload =
                    new LoginRequestPayload(
                            ConfigManager.getUsername(UserType.ADMIN),
                            ConfigManager.getPassword(UserType.ADMIN)
                    );

            cachedAdminToken = apiClient
                    .withBody(payload)
                    .post(ApiConstants.USER_LOGIN_ENDPOINT)
                    .jsonPath()
                    .getString("accessToken");

            LOG.info("New Admin Token: " + cachedAdminToken);
        }
        return cachedAdminToken;
    }
}