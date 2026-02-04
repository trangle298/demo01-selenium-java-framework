package model.enums;

public enum UserType {
    CUSTOMER("KhachHang", "basic"),
    ADMIN("QuanTri", "admin");

    private final String label;
    private final String credentialPrefix;

    UserType(String label, String credentialPrefix) {
        this.label = label;
        this.credentialPrefix = credentialPrefix;
    }

    public String getLabel() {
        return label;
    }

    public String usernameKey() {
        return credentialPrefix + ".username";
    }

    public String passwordKey() {
        return credentialPrefix + ".password";
    }

    public String emailKey() {
        return credentialPrefix + ".email";
    }
}