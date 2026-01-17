package model;

import lombok.Data;

/**
 * Model for showtime filter dropdown options.
 */
@Data
public class FilterDropdownOptions {
        private final String movieTitle;
        private final String cinemaName;
        private final String showtimeId;
}
