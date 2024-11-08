package ru.practicum.compilation.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class UpdateCompilationRequest {
    List<@Positive Long> events;
    Boolean pinned;
    @Size(min = 1, max = 50)
    String title;
}
