package model.enums;
                                                                    
public enum UserType {
    CUSTOMER("KhachHang"),
    ADMIN("QuanTri");

    private final String label;

    UserType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}