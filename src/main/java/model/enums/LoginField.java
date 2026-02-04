package model.enums;

public enum LoginField {
    USERNAME("taiKhoan"),
    PASSWORD("matKhau");

    private final String fieldId;

    LoginField(String fieldId) {
        this.fieldId = fieldId;
    }

    public String getFieldId() {
        return fieldId;
    }

}
