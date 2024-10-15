package ru.practicum;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.dto.ViewStatsRequestDto;

import java.nio.charset.StandardCharsets;
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

    public List<ViewStatsDto> getStats(ViewStatsRequestDto viewStatsRequestDto) {
        String startStr = URLEncoder.encode(DATE_TIME_FORMATTER.format(viewStatsRequestDto.getStart()), StandardCharsets.UTF_8);
        String endStr = URLEncoder.encode(DATE_TIME_FORMATTER.format(viewStatsRequestDto.getEnd()), StandardCharsets.UTF_8);
        List<String> uris = viewStatsRequestDto.getUris();
        Boolean unique = viewStatsRequestDto.isUnique();
        StringBuilder uriBuilder = new StringBuilder(STATS_ENDPOINT);
        uriBuilder.append("start=").append(startStr);
        uriBuilder.append("end=").append(endStr);
        if (!Objects.isNull(uris) && !uris.isEmpty()) {
            uriBuilder.append("uris=").append(String.join(",", uris));
        }
        uriBuilder.append("unique=").append(unique);

        return restClient.get()
                .uri(uriBuilder.toString())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

}
