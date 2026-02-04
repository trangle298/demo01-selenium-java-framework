package api;

/**
 * Centralized API path configuration for the movie booking system.
 * Contains base URI and path segments for all API operations.
 *
 * <p>Path segments are organized by API base path and are relative to the baseUri.
 * Service classes should use these constants with ApiClient which handles baseUri automatically.
 *
 * <p>API structure (grouped by backend API modules):
 * <ul>
 *   <li>QuanLyNguoiDung - User management (registration, search)</li>
 *   <li>QuanLyPhim - Movie management (movie list, details)</li>
 *   <li>QuanLyRap - Cinema management (systems, branches, showtimes)</li>
 *   <li>QuanLyDatVe - Booking management (seat availability, bookings)</li>
 * </ul>
 */
public class ApiConstants {

    // Base Api Uri pattern, %s will be replaced by host name mapped to environment
    public static final String baseUri_PATTERN = "https://%s.cybersoft.edu.vn";

    // ===== QuanLyNguoiDung (User Management) =====
    private static final String USER_BASE = "/api/QuanLyNguoiDung";
    public static final String USER_REGISTER_ENDPOINT = USER_BASE + "/DangKy";
    public static final String USER_LOGIN_ENDPOINT = USER_BASE + "/DangNhap";
    public static final String USER_SEARCH_ENDPOINT = USER_BASE + "/TimKiemNguoiDung";
    public static final String USER_DELETE_ENDPOINT = USER_BASE + "/XoaNguoiDung";

    // ===== QuanLyPhim (Movie Management) =====
    private static final String MOVIE_BASE = "/api/QuanLyPhim";
    public static final String MOVIE_LIST_ENDPOINT = MOVIE_BASE + "/LayDanhSachPhim";

    // ===== QuanLyRap (Cinema Management - includes showtimes) =====
    private static final String CINEMA_BASE = "/api/QuanLyRap";
    /** Get all cinema systems */
    public static final String CINEMA_SYSTEM_ENDPOINT = CINEMA_BASE + "/LayThongTinHeThongRap";
    /** Get cinema branches by system */
    public static final String CINEMA_BRANCH_ENDPOINT = CINEMA_BASE + "/LayThongTinCumRapTheoHeThong";
    /** Get showtimes by cinema system */
    public static final String CINEMA_SHOWTIME_ENDPOINT = CINEMA_BASE + "/LayThongTinLichChieuHeThongRap";
    /** Get showtimes by movie */
    public static final String MOVIE_SHOWTIME_ENDPOINT = CINEMA_BASE + "/LayThongTinLichChieuPhim";

    // ===== QuanLyDatVe (Booking Management) =====
    private static final String BOOKING_BASE = "/api/QuanLyDatVe";
    /** Get showtime details with seat availability */
    public static final String SHOWTIME_BOOKING_DATA_ENDPOINT = BOOKING_BASE + "/LayDanhSachPhongVe";

}