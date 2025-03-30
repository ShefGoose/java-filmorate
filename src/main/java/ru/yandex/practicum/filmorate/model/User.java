package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.Marker;

import java.time.LocalDate;

@Data
public class User  {
    @Null(groups = Marker.OnCreate.class, message = "При создании id пользователя должен быть null")
    @NotNull(groups = Marker.OnUpdate.class, message = "Для обновления не указан id пользователя")
    private Integer id;
    @Email(groups = {Marker.OnCreate.class,Marker.OnUpdate.class}, message = "Неверный формат Email")
    @NotBlank(groups = Marker.OnCreate.class, message = "Почта не может быть пустой")
    private String email;
    @NotBlank(groups = Marker.OnCreate.class, message = "Логин не может быть пустым")
    @Pattern(groups = {Marker.OnCreate.class,Marker.OnUpdate.class}, regexp = "^\\S*$",
            message = "Логин не может содержать пробелы")
    private String login;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(groups = Marker.OnCreate.class, message = "Дата рождения не может быть null")
    @Past(groups = {Marker.OnCreate.class,Marker.OnUpdate.class}, message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    public String getName() {
        if (name == null || name.isBlank()) {
            name = login;
        }
        return name;
    }
}
