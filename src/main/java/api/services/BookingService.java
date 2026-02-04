package api.services;

import api.ApiClient;
import api.ApiConfig;
import api.ApiConstants;
import model.api.response.ShowtimeBooking;

import java.util.Map;

public class BookingService {

    private final ApiClient apiClient;

    public BookingService() {
        this.apiClient = new ApiClient(ApiConfig.getBaseUri());
    }

    public ShowtimeBooking getShowtimeBookingData(String showtimeId) {
        return apiClient.withQueryParam("maLichChieu", showtimeId)
                .getAndDeserialize(ApiConstants.SHOWTIME_BOOKING_DATA_ENDPOINT, ShowtimeBooking.class);
    }

    public Map<String, Object> getShowtimeIdToBookingDataMap(String showtimeId) {
        ShowtimeBooking showtime = getShowtimeBookingData(showtimeId);
        return Map.of(showtimeId, showtime);
    }

}