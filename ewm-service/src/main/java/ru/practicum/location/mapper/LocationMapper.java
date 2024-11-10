package ru.practicum.location.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.dto.LocationFullDto;
import ru.practicum.location.dto.NewLocationDto;
import ru.practicum.location.model.Location;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class LocationMapper {
    public static LocationDto toLocationDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

    public static LocationFullDto toLocationFullDto(Location location) {
        return LocationFullDto.builder()
                .id(location.getId())
                .name(location.getName())
                .lat(location.getLat())
                .lon(location.getLon())
                .radius(location.getRadius())
                .build();
    }

    public static List<LocationFullDto> toLocationFullDto(List<Location> locations) {
        List<LocationFullDto> locationDtos = new ArrayList<>();
        for (Location location : locations) {
            locationDtos.add(toLocationFullDto(location));
        }
        return locationDtos;
    }

    public static Location toNewLocation(NewLocationDto newLocationDto) {
        return Location.builder()
                .name(newLocationDto.getName())
                .lat(newLocationDto.getLat())
                .lon(newLocationDto.getLon())
                .radius(newLocationDto.getRadius())
                .build();
    }
}
