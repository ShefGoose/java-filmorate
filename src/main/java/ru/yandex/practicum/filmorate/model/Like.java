package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Like {
    @NotNull
    private Integer filmId;
    @NotNull
    private Integer userId;
}
