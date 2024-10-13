package ru.practicum;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import java.net.URLEncoder;
import java.util.Objects;

import static ru.practicum.ewm.stats.utils.Constants.DATE_TIME_FORMATTER;

public class StatsClient {
    private final RestClient restClient;
    private static final String BASE_URL = "http://localhost:9090";
    private static final String HIT_ENDPOINT = "/hit";
    private static final String STATS_ENDPOINT = "/stats";

    public StatsClient() {
        this.restClient = RestClient.create(BASE_URL);
    }

    public EndpointHitDto create(EndpointHitDto endpointHitDto) {
        return restClient.post()
                .uri(HIT_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(endpointHitDto)
                .retrieve()
                .body(EndpointHitDto.class);
    }

    public List<ViewStatsDto> getStats(Instant start, Instant end, List<String> uris, Boolean unique) {
        String startStr = URLEncoder.encode(DATE_TIME_FORMATTER.format(start), StandardCharsets.UTF_8);
        String endStr = URLEncoder.encode(DATE_TIME_FORMATTER.format(end), StandardCharsets.UTF_8);
        StringBuilder uriBuilder = new StringBuilder(STATS_ENDPOINT);
        uriBuilder.append("start=").append(startStr);
        uriBuilder.append("end=").append(endStr);
        if (!Objects.isNull(uris) && !uris.isEmpty()) {
            uriBuilder.append("uris=").append(String.join(",", uris));
        }
        if (!Objects.isNull(unique)) {
            uriBuilder.append("unique=").append(unique);
        }

        return restClient.get()
                .uri(uriBuilder.toString())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public List<ViewStatsDto> getStats(Instant start, Instant end, List<String> uris) {
        return getStats(start, end, uris, null);
    }

    public List<ViewStatsDto> getStats(Instant start, Instant end, Boolean unique) {
        return getStats(start, end, null, unique);
    }

    public List<ViewStatsDto> getStats(Instant start, Instant end) {
        return getStats(start, end, null, null);
    }
}
