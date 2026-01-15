package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Showtime {

    private MovieDetails thongTinPhim;
    private List<SeatingDetails> danhSachGhe;

    public MovieDetails getThongTinPhim() {
        return thongTinPhim;
    }
    public List<SeatingDetails> getDanhSachGhe() {
        return danhSachGhe;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MovieDetails {
        private Integer maLichChieu;
        private String tenCumRap;
        private String tenPhim;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SeatingDetails {
        private String maGhe;
        private String tenGhe;
        private String loaiGhe;
        private boolean daDat;
    }
}
