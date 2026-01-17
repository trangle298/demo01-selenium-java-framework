package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Showtimes organized by movie from API response.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShowtimesByMovie {

    private List<ShowtimesPerCinema> heThongRapChieu;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ShowtimesPerCinema {
        private String maHeThongRap;
        private String tenHeThongRap;
        private List<ShowtimesPerBranch> cumRapChieu;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ShowtimesPerBranch {
        private String maCumRap;
        private String tenCumRap;
        private List<ShowtimeDetails> lichChieuPhim;
    }
}
