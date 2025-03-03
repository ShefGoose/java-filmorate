package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
@Data
public class User  {
    private Integer id;
    @Email(message = "Неверный формат Email")
    @NotBlank(message = "Почта не может быть пустой")
    private String email;
    @NotBlank(message = "Логин не может быть пустым")
    private String login;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
