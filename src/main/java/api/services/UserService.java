package api.services;

import api.ApiClient;
import api.ApiConfig;
import api.ApiConstants;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import model.api.request.RegisterRequestPayload;
import model.UserAccount;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.InvalidParameterException;
import java.util.List;

public class UserService {

    private final ApiClient apiClient;
    private static final Logger LOG = LogManager.getLogger(UserService.class);

    public UserService() {
        this.apiClient = new ApiClient(ApiConfig.getBaseUri());
    }

    public boolean isAccountExisting(String username) {
        UserAccount user = getUserDetails(username);
        if (user == null) {
            return false;
        }
        return true;
    }

    public UserAccount getUserDetails(String username) {
        if (username == null || username.isEmpty()) {
            throw new InvalidParameterException("Username is null or empty");
        }

        List<UserAccount> users = this.apiClient
                .withQueryParam("MaNhom", "GP09")
                .withQueryParam("tuKhoa", username)
                .getAndDeserialize(ApiConstants.USER_SEARCH_ENDPOINT, new TypeRef<>() {});

        if (users.size() > 1) {
            throw new RuntimeException("Found more than one user with username: " + username);
        }

        if (users == null || users.isEmpty()) {
            LOG.info("No user found for username " + username);
            return null;
        }

        return users.getFirst();
    }

    public void sendRegisterRequest(RegisterRequestPayload request) {
        this.apiClient
                .withBody(request)
                .post(ApiConstants.USER_REGISTER_ENDPOINT)
                .then()
                .statusCode(200);
    }

    public Response sendDeleteUserRequest(String username) {

        String adminToken = AuthService.getAdminToken();

        return apiClient
                .withAuthToken(adminToken)
                .withQueryParam("TaiKhoan", username)
                .delete(ApiConstants.USER_DELETE_ENDPOINT);
    }

}
