package api.services;

import api.ApiClient;
import api.ApiConfig;
import utils.DateTimeNormalizer;
import io.restassured.common.mapper.TypeRef;
import api.ApiConstants;
import model.api.response.CinemaSystem;
import model.api.response.CinemaBranch;
import model.api.response.CinemaSystemSchedule;
import model.api.response.MovieSchedule;

import java.util.List;
import java.util.Map;

public class CinemaService {

    private final ApiClient apiClient;

    public CinemaService() {
        this.apiClient = new ApiClient(ApiConfig.getBaseUri());
    }

    public List<CinemaSystem> getCinemaSystem() {
        return apiClient
                .getAndDeserialize(ApiConstants.CINEMA_SYSTEM_ENDPOINT, new TypeRef<>() {});
    }

    public List<CinemaBranch> getCinemaBranches(String cinemaId) {
        return apiClient.withQueryParam("maHeThongRap", cinemaId)
                .getAndDeserialize(ApiConstants.CINEMA_BRANCH_ENDPOINT, new TypeRef<>() {});
    }

    public List<CinemaSystemSchedule> getShowtimesForCinemaSystem(String cinemaId) {
        return apiClient.withQueryParam("maHeThongRap", cinemaId)
                .getAndDeserialize(ApiConstants.CINEMA_SHOWTIME_ENDPOINT, new TypeRef<>() {});
    }

    /**
     * Get showtimes for a specific movie.
     *
     * @param movieId The movie ID
     * @return ShowtimesByMovie containing showtime information grouped by cinema
     */
    public MovieSchedule getShowtimesForMovie(String movieId) {
        return apiClient
                .withQueryParam("maPhim", movieId)
                .getAndDeserialize(ApiConstants.MOVIE_SHOWTIME_ENDPOINT, MovieSchedule.class);
    }

    /**
     * Get showtimes for a specific movie at a specific cinema branch.
     *
     * @param movieId The movie ID
     * @param branchId The cinema branch ID
     * @return List of showtime details for the specified movie and branch
     */
    public List<MovieSchedule.ShowtimeDetails> getShowtimesForMovieAndBranch(String movieId, String branchId) {
        MovieSchedule movieSchedule = getShowtimesForMovie(movieId);
        String cleanBranchId = branchId.trim();

        return movieSchedule.getHeThongRapChieu().stream()
                .flatMap(cinemaSys -> cinemaSys.getCumRapChieu().stream())
                .filter(branch -> branch.getMaCumRap().trim().equals(cleanBranchId))
                .flatMap(branch -> branch.getLichChieuPhim().stream())
                .toList();
    }

    /**
     * Get showtime IDs and normalized datetimes for a specific movie at a specific cinema branch.
     * Datetimes are normalized to standard format (dd/MM/yyyy HH:mm) for UI comparison.
     *
     * @param movieId The movie ID
     * @param branchId The cinema branch ID
     * @return Map where key is showtime ID and value is normalized datetime string
     */
    public Map<String, String> getShowtimeIdToDatetimeMap(String movieId, String branchId) {
        return getShowtimesForMovieAndBranch(movieId, branchId).stream()
                .collect(java.util.stream.Collectors.toMap(
                        MovieSchedule.ShowtimeDetails::getMaLichChieu,
                        showtime -> DateTimeNormalizer.normalize(showtime.getNgayChieuGioChieu())
                ));
    }

}
