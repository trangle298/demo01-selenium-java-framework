package model.enums;

public enum OrderEntryField {
    PURCHASE_DATETIME("Ngày đặt"),
    MOVIE_NAME("Tên phim"),
    PRICE("Giá vé"),
    CINEMA_BRANCH_NAME(""),
    THEATER_NAME("Rạp"),
    SEAT_NUMBERS("Ghế số");

    private final String label;

    OrderEntryField(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}