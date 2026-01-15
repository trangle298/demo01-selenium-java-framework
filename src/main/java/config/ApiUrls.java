package config;

public class ApiUrls {

    public static final String baseURL = "https://movie0706.cybersoft.edu.vn";

    public static final String CINEMAS =
            baseURL + "/api/QuanLyRap/LayThongTinHeThongRap";

    public static String showtimesByCinemaId(String cinemaId) {
        return CINEMAS + "?maHeThongRap=" + cinemaId + "&maNhom=GP09";
    }

    public static final String MOVIES =
            baseURL + "/api/QuanLyPhim/LayDanhSachPhim?maNhom=GP09";

    public static String showtimesByMovieId(String movieId) {
        return baseURL + "/api/QuanLyRap/LayThongTinLichChieuPhim?MaPhim=" + movieId;
    }

    public static String showtimeDataByShowtimeId(String showtimeId) {
        return baseURL + "/api/QuanLyDatVe/LayDanhSachPhongVe?MaLichChieu=" + showtimeId;
    }

    public static String userInfoByUsername(String username) {
           return baseURL + "/api/QuanLyNguoiDung/TimKiemNguoiDung?MaNhom=GP09&tuKhoa=" + username;
    }

}
