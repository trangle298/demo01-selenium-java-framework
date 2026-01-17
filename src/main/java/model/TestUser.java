package model;

import lombok.Data;

/**
 * Test user model for loading test user data from test-users.json.
 * Uses Lombok @Data to auto-generate getters, setters, toString(), equals(), and hashCode().
 * Jackson deserializes JSON directly since field names match JSON property names.
 */
@Data
public class TestUser {

    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phoneNr;

}
