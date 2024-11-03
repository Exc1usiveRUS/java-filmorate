package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.*;
import ru.yandex.practicum.filmorate.validation.MinimumDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    @Size(max = 200)
    private String description;
    @MinimumDate
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private final Set<Integer> likes = new HashSet<>();

}
