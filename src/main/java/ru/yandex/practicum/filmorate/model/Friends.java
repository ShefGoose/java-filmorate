package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Friends {
    @NotNull
    private Integer userid;
    @NotNull
    private Integer friendId;
    @NotNull
    private boolean status;
}
