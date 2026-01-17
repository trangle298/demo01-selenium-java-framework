package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Basic showtime details from API response.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShowtimeDetails {
    private String maLichChieu;
}
