package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class NewUserRequest {
    @NotBlank
    @Size(min = 2, max = 250)
    final String name;

    @NotBlank
    @Size(min = 6, max = 254)
    @Email
    final String email;
}
