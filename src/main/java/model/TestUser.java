package model;

import lombok.Data;

@Data
public class TestUser {

    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phoneNr;
//
//    @JsonCreator
//    public TestUser(
//            @JsonProperty("taiKhoan") String taiKhoan,
//            @JsonProperty("matKhau") String matKhau,
//            @JsonProperty("hoTen") String hoTen,
//            @JsonProperty("email") String email,
//            @JsonProperty("soDt") String soDt
//    ) {
//        this.taiKhoan = taiKhoan;
//        this.matKhau = matKhau;
//        this.hoTen = hoTen;
//        this.email = email;
//        this.soDt = soDt;
//    }

}
