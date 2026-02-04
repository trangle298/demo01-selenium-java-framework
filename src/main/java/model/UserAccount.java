package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import model.enums.UserType;

/**
 * Base user account data model.
 * Shared between API requests and UI form inputs.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserAccount {
    protected String taiKhoan;
    protected String hoTen;
    protected String email;
    protected String soDt;
    protected String matKhau;

    @lombok.Builder.Default
    protected final String maLoaiNguoiDung = UserType.CUSTOMER.getLabel();

    public String getUsername() {
        return taiKhoan;
    }

    public String getFullName() {
        return hoTen;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return soDt;
    }

    public String getPassword() {
        return matKhau;
    }

    public String getUserType() {
        return maLoaiNguoiDung;
    }

    public UserAccount setPassword(String matKhau) {
        this.matKhau = matKhau;
        return this;
    }

    public UserAccount setEmail(String email) {
        this.email = email;
        return this;
    }

    public UserAccount setFullName(String hoTen) {
        this.hoTen = hoTen;
        return this;
    }

    public UserAccount setUsername(String taiKhoan) {
        this.taiKhoan = taiKhoan;
        return this;
    }
}