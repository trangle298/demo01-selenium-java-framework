package model;

import lombok.Data;

/**
 * Model for login form data.
 */
@Data
public class LoginRequest {
    private final String taiKhoan;
    private final String matKhau;
}
