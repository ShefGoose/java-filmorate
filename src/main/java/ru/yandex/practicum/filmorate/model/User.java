package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class User  {
    private Integer id;
    @Email(message = "Неверный формат Email")
    @NotBlank(message = "Почта не может быть пустой")
    private String email;
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S*$", message = "Логин не может содержать пробелы")
    private String login;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Дата рождения не может быть null")
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
    private Set<Integer> friends;
}
