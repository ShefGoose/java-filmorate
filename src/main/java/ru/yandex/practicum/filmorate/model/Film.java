package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.AfterMovieBirthday;
import ru.yandex.practicum.filmorate.validation.Marker;

import java.time.LocalDate;
import java.util.*;

@Data
public class Film {
    @Null(groups = Marker.OnCreate.class, message = "При создании id фильма должен быть null")
    @NotNull(groups = Marker.OnUpdate.class, message = "Для обновления не указан id")
    private Integer id;
    @NotBlank(groups = Marker.OnCreate.class, message = "Имя фильма не должно быть пустым")
    private String name;
    @Size(groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, max = 200,
            message = "Длина описания превышает 200 символов")
    @NotNull(groups = Marker.OnCreate.class, message = "Описание фильма не может быть null")
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(groups = Marker.OnCreate.class, message = "Дата релиза фильма не может быть null")
    @AfterMovieBirthday(groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private LocalDate releaseDate;
    @Positive(groups = {Marker.OnCreate.class, Marker.OnUpdate.class},
            message = "Продолжительность фильма должна быть положительным числом")
    @NotNull(groups = Marker.OnCreate.class,  message = "Продолжительность фильма не может быть null")
    private Integer duration;
    private Set<Genre> genres = new HashSet<>();
    @NotNull(groups = Marker.OnCreate.class, message = "У фильма должен быть указан рейтинг MPA")
    private Mpa mpa;

}
