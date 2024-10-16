package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.model.ViewStats;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ViewStatsMapper {
    public static ViewStatsDto toViewStatsDto(ViewStats viewStats) {
        return new ViewStatsDto(viewStats.getApp(), viewStats.getUri(), viewStats.getHits());
    }

    public static List<ViewStatsDto> toViewStatsDto(List<ViewStats> viewStatsList) {
        List<ViewStatsDto> viewStatsDtos = new ArrayList<>();
        for (ViewStats viewStats : viewStatsList) {
            viewStatsDtos.add(toViewStatsDto(viewStats));
        }
        return viewStatsDtos;
    }
}
