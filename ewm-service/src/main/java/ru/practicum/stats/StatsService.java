package ru.practicum.stats;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.event.model.Event;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.dto.ViewStatsRequestDto;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Service
public class StatsService {
    public static final String EVENT_URI = "/events/%d";

    final StatsClient statsClient;

    public void create(EndpointHitDto endpointHitDto) {
        statsClient.create(endpointHitDto);
    }

    public long getViewsFromStartToNow(Instant start, Long eventId) {
        ViewStatsRequestDto viewStatsRequestDto = ViewStatsRequestDto.builder()
                .start(start)
                .end(Instant.now().plusSeconds(1L)) //добавлена секунда для корректного отбора статистики в БД
                .uris(List.of(String.format(EVENT_URI, eventId)))
                .unique(true)
                .build();
        List<ViewStatsDto> hits = statsClient.getStats(viewStatsRequestDto);
        return hits.isEmpty() ? 0 : hits.getFirst().getHits();
    }

    public Map<Long, Long> getViewsForEvents(List<Event> events) {
        if (events.isEmpty()) {
            return new HashMap<>();
        } else {
            ViewStatsRequestDto viewStatsRequestDto = ViewStatsRequestDto.builder()
                    .start(events.stream().min(Comparator.comparing(Event::getCreatedOn)).map(Event::getCreatedOn).orElse(Instant.EPOCH))
                    .end(Instant.now().plusSeconds(1L)) //добавлена секунда для корректного отбора статистики в БД
                    .uris(events.stream().map(event -> String.format(EVENT_URI, event.getId())).toList())
                    .unique(true)
                    .build();
            List<ViewStatsDto> viewStatsDtos = statsClient.getStats(viewStatsRequestDto);
            return viewStatsDtos.stream()
                    .collect(Collectors.toMap(viewStatsDto -> Long.parseLong(viewStatsDto.getUri().split("/")[2]), ViewStatsDto::getHits));
        }
    }
}
