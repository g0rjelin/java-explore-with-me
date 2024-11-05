package ru.practicum.compilation.dto;

import jakarta.validation.constraints.NotBlank;
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
public class NewCompilationDto {
    List<@Positive Long> events;
    @Builder.Default
    boolean pinned = false;
    @Size(min = 1, max = 50)
    @NotBlank
    String title;
}
