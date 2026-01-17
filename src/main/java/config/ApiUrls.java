package config;

/**
 * Centralized API URL configuration for the movie booking system.
 * Contains base URL and endpoint builders for all API operations.
 *
 * <p>Organized by domain:
 * <ul>
 *   <li>Cinema - Theater/cinema related endpoints</li>
 *   <li>Movie - Film information endpoints</li>
 *   <li>Showtime - Screening schedule endpoints</li>
 *   <li>User - User account endpoints</li>
 * </ul>
 */
public class ApiUrls {

    /** Base URL for all API endpoints */
    public static final String baseURL = "https://movie0706.cybersoft.edu.vn";

    /** Get all cinema systems endpoint */
    public static final String CINEMAS =
            baseURL + "/api/QuanLyRap/LayThongTinHeThongRap";

    /**
     * Get showtimes by cinema ID.
     *
     * @param cinemaId The cinema system ID (e.g., "CGV", "BHDStar")
     * @return Full API URL for fetching showtimes by cinema
     */
    public static String showtimesByCinemaId(String cinemaId) {
        return CINEMAS + "?maHeThongRap=" + cinemaId + "&maNhom=GP09";
    }

    /** Get all movies endpoint */
    public static final String MOVIES =
            baseURL + "/api/QuanLyPhim/LayDanhSachPhim?maNhom=GP09";

    /**
     * Get showtimes by movie ID.
     *
     * @param movieId The movie ID
     * @return Full API URL for fetching showtimes for a specific movie
     */
    public static String showtimesByMovieId(String movieId) {
        return baseURL + "/api/QuanLyRap/LayThongTinLichChieuPhim?MaPhim=" + movieId;
    }

    /**
     * Get showtime details including seat availability.
     *
     * @param showtimeId The showtime/screening ID
     * @return Full API URL for fetching seat layout and availability
     */
    public static String showtimeDataByShowtimeId(String showtimeId) {
        return baseURL + "/api/QuanLyDatVe/LayDanhSachPhongVe?MaLichChieu=" + showtimeId;
    }

    /**
     * Search for user by username.
     *
     * @param username The username to search for
     * @return Full API URL for user search endpoint
     */
    public static String userInfoByUsername(String username) {
           return baseURL + "/api/QuanLyNguoiDung/TimKiemNguoiDung?MaNhom=GP09&tuKhoa=" + username;
    }

}
