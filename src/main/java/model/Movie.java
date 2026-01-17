package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Movie data model from API response.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Movie {
    private String maPhim;
    private String tenPhim;
}
