package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class ReviewLike {
    private int reviewId;
    private int userId;
    private Boolean like;
}
