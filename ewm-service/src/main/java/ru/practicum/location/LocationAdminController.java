package ru.practicum.location;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.location.dto.LocationFullDto;
import ru.practicum.location.dto.NewLocationDto;
import ru.practicum.location.dto.UpdateLocationDto;
import ru.practicum.location.service.LocationService;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/admin/locations")
public class LocationAdminController {
    final LocationService locationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<LocationFullDto> getLocations(
            @RequestParam(required = false) Float lat,
            @RequestParam(required = false) Float lon,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Float radius,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        return locationService.getLocations(lat, lon, radius, from, size);
    }

    @GetMapping("/{locId}")
    @ResponseStatus(HttpStatus.OK)
    public LocationFullDto getLocationByLocId(@PathVariable(name = "locId") @Positive Long locationId) {
        return locationService.getLocationByLocId(locationId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationFullDto createLocation(@RequestBody @Valid NewLocationDto newLocationDto) {
        return locationService.createLocation(newLocationDto);
    }

    @PatchMapping("/{locId}")
    @ResponseStatus(HttpStatus.OK)
    public LocationFullDto updateLocation(@PathVariable(name = "locId") @Positive Long locationId,
                                      @RequestBody @Valid UpdateLocationDto updateLocationDto) {
        return locationService.updateLocation(locationId, updateLocationDto);
    }

    @DeleteMapping("/{locId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLocation(@PathVariable(name = "locId") @Positive Long locationId) {
        locationService.deleteLocation(locationId);
    }
}
