package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class Review {
    private int reviewId;
    @NotNull(message = "Отзыв не может быть null")
    @NotBlank(message = "Отзыв не может быть пустым")
    private String content;
    @NotNull(message = "Отзыв должен быть позитивным или негативным")
    private Boolean isPositive;
    //@Positive
    @NotNull(message = "Фильм не может быть null")
    private Integer userId;
    @NotNull(message = "Фильм не может быть null")
   // @Positive
    private Integer filmId;
    private Integer useful;

    public boolean getIsPositive() {
        return isPositive;
    }

    public void setIsPositive(boolean isPositive) {
        this.isPositive = isPositive;
    }
}
