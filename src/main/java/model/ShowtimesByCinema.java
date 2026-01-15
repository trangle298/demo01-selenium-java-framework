package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShowtimesByCinema {

    private String maHeThongRap;
    private String tenHeThongRap;
    private List<ShowtimesPerBranch> lstCumRap;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ShowtimesPerBranch {
        private String maCumRap;
        private String tenCumRap;
        private List<ShowtimesPerMovie> danhSachPhim;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ShowtimesPerMovie {
        private String maPhim;
        private String tenPhim;
        private List<ShowtimeDetails> lstLichChieuTheoPhim;
    }
}
