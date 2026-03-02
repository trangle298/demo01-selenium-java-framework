package helpers.providers;

import api.services.UserService;
import io.restassured.response.Response;
import model.UserAccount;
import model.api.request.RegisterRequestPayload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static helpers.providers.UserAccountTestDataGenerator.generateRegisterRequestPayload;

public class TestUserProvider {

    private static final Logger LOG = LogManager.getLogger(TestUserProvider.class);

    public static UserAccount createNewTestUser() {
        RegisterRequestPayload registerPayload = generateRegisterRequestPayload();

        // TEMP: Remove all non-alphabetic chars in full name due to known backend bug
        // to make sure account form is displayed for this test
        registerPayload.setFullName(registerPayload.getFullName().replaceAll("[^a-zA-Z]", ""));
        LOG.info(
                "Temp fix for known issue: Modify Full Name to remove all non-alphabetic chars before register to have account form displayed");

        UserService userService = new UserService();
        userService.sendRegisterRequest(registerPayload);

        return new UserAccount(
                registerPayload.getUsername(),
                registerPayload.getFullName(),
                registerPayload.getEmail(),
                registerPayload.getPhoneNumber(),
                registerPayload.getPassword(),
                registerPayload.getUserType());
    }

    public static void deleteUser(UserAccount user) {
        UserService userService = new UserService();
        try {
            Response response = userService.sendDeleteUserRequest(user.getUsername());

            int status = response.statusCode();

            if (status == 200 || status == 404) {
                LOG.info("User cleanup completed: " + user.getUsername());
            } else {
                LOG.warn("Unexpected delete status: " + status);
            }
        } catch (Exception e) {
            LOG.warn("Cleanup failed for user " + user.getUsername(), e);
        }
    }
}
