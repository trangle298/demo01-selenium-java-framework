package helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.TestUser;
import model.TestUserType;

import java.io.InputStream;
import java.util.Map;

public class TestUserProvider {

    private static final Map<String, TestUser> users;

    static {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = TestUserProvider.class.getClassLoader().getResourceAsStream("test-users.json");

            users = mapper.readValue(
                    inputStream,
                    mapper.getTypeFactory().constructMapType(
                            Map.class,
                            String.class,
                            TestUser.class
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to load test users", e);
        }
    }

    public static TestUser getUser(TestUserType type) {
        TestUser user = users.get(type.name());
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + type);
        }
        return user;
    }

}
