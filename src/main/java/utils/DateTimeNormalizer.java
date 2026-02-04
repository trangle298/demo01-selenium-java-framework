package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Utility class to normalize datetime strings from different sources (UI and API)
 * into a standard format for comparison.
 */
public class DateTimeNormalizer {

    // Standard format for normalized datetime
    public static final DateTimeFormatter STANDARD_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Common datetime formats from API and UI
    // Note: UI should ideally use a consistent format, but we handle known variations here.
    private static final List<DateTimeFormatter> KNOWN_FORMATS = List.of(
            DateTimeFormatter.ofPattern("dd/MM/yyyy ~ HH:mm"),  // UI format: 24/12/2021 ~ 13:35
            DateTimeFormatter.ofPattern("dd/MM/yyyy -HH:mm"),   // Alternate UI format: 17/10/2021 -08:10
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"),    // Alternate UI format: 17/10/2021 08:10
            DateTimeFormatter.ofPattern("dd-MM-yyyy ~ HH:mm"),  // Alternate UI format:  01-01-2019  ~  14:10
            DateTimeFormatter.ofPattern("dd-MM-yyyy | HH:mm"),  // Alternate UI format: 11-01-2026 | 16:48
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,              // API format: 2021-10-17T08:43:00
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")     // Joint from API date and time fields
    );

    /**
     * Normalize datetime string to standard format: dd/MM/yyyy HH:mm
     * Automatically detects the input format from known patterns.
     *
     * @param datetime Datetime string in any known format
     * @return Normalized datetime string in format: dd/MM/yyyy HH:mm
     * @throws IllegalArgumentException if datetime cannot be parsed
     */
    public static String normalize(String datetime) {
        if (datetime == null || datetime.trim().isEmpty()) {
            throw new IllegalArgumentException("Datetime string cannot be null or empty");
        }

        String cleanedDatetime = datetime.trim();

        // Try each known format
        for (DateTimeFormatter formatter : KNOWN_FORMATS) {
            try {
                LocalDateTime parsedDateTime = LocalDateTime.parse(cleanedDatetime, formatter);
                return parsedDateTime.format(STANDARD_FORMAT);
            } catch (DateTimeParseException e) {
                // Continue to next format in INPUT_FORMATS list
            }
        }

        throw new IllegalArgumentException("Unable to parse datetime: " + datetime + ". Check supported formats.");
    }

    public static String normalize(LocalDateTime datetime) {
        if (datetime == null) {
            throw new IllegalArgumentException("Datetime object cannot be null");
        }
        return datetime.format(STANDARD_FORMAT);
    }

}
