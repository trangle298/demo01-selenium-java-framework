package model;

import lombok.Data;

@Data
public class RegisterRequest {

    private final String username;
    private final String password;
    private final String confirmPassword;
    private final String fullName;
    private final String email;

}
