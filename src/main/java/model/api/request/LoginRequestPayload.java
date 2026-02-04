package model.api.request;

import lombok.Data;

@Data
public class LoginRequestPayload {
    private final String taiKhoan;
    private final String matKhau;
}
