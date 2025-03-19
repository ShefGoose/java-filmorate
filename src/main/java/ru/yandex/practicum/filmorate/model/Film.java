package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class Film {
    private Integer id;
    @NotBlank(message = "Имя фильма не должно быть пустым")
    private String name;
    @Size(max = 200, message = "Длина описания превышает 200 символов")
    @NotNull(message = "Описание фильма не может быть null")
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Дата релиза фильма не может быть null")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    @NotNull(message = "Продолжительность фильма не может быть null")
    private Integer duration;
    @JsonIgnore
    private Set<Integer> userLikes;
    private Integer likes;

    public void addLike(Integer userId) {
        userLikes.add(userId);
        likes++;
    }

    public void deleteLike(Integer userId) {
        userLikes.remove(userId);
        likes--;
    }
}
