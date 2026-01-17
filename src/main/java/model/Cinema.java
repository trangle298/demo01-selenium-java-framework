package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Cinema/theater system data model from API response.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cinema {
    private String maHeThongRap;
    private String tenHeThongRap;
    private String biDanh;
    private String logo;
}
