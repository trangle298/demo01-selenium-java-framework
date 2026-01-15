package model;

import lombok.Data;

@Data
public class LoginRequest {
    private final String taiKhoan;
    private final String matKhau;
}
