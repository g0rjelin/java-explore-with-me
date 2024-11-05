package ru.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.dto.ViewStatsRequestDto;
import ru.practicum.service.StatsService;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import static ru.practicum.ewm.stats.utils.Constants.DATE_TIME_FORMATTER;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@RestController
public class StatsController {
    final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(code = HttpStatus.CREATED)
    public EndpointHitDto addEndpointHit(@RequestBody EndpointHitDto endpointHitDto) {
        return statsService.addEndpointHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam String start,
                                       @RequestParam String end,
                                       @RequestParam(required = false) List<String> uris,
                                       @RequestParam(required = false, defaultValue = "false") boolean unique) {
        ViewStatsRequestDto viewStatsRequestDto = ViewStatsRequestDto.builder()
                .start(DATE_TIME_FORMATTER.parse(java.net.URLDecoder.decode(start, StandardCharsets.UTF_8),
                        Instant::from))
                .end(DATE_TIME_FORMATTER.parse(java.net.URLDecoder.decode(end, StandardCharsets.UTF_8),
                        Instant::from))
                .uris(uris)
                .unique(unique)
                .build();
        return statsService.getStats(viewStatsRequestDto);
    }
}
