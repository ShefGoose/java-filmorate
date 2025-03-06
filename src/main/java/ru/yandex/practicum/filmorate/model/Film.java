package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    private Integer id;
    @NotBlank(message = "Имя фильма не должно быть пустым")
    private String name;
    @Size(max = 200, message = "Длина описания превышает 200 символов")
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Дата релиза фильма не может быть null")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    @NotNull(message = "Продолжительность фильма не может быть null")
    private Integer duration;
}
