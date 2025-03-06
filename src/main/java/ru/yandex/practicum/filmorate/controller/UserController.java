package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.UtilController;

import java.time.LocalDate;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> listUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User postUser(@Valid @RequestBody User user) {
        log.info("Получены следущие параметры пользователя: Email: {}, логин: {}, имя: {}, дата рождения {}",
                user.getEmail(), user.getLogin(),
                user.getName(), user.getBirthday());
        validateUser(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь с именем: {}, успешно добавлен", user.getName());
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("Получены следущие параметры пользователя: {}", user.toString());

        if (user.getId() == null) {
            log.warn("Id пользователя: {}", (Object) null);
            throw new ValidationException("Id должен быть указан");
        }

        User updateUser = users.get(user.getId());
        if (updateUser == null) {
            throw new NoSuchElementException("Пользователь с id = " + user.getId() + " не найден");
        }

        UtilController.merge(user, updateUser);
        validateUpdateUser(updateUser);
        users.put(updateUser.getId(), updateUser);
        log.info("Все переданные не null значения пользователя с id: {}, успешно обновлены", user.getId());
        return updateUser;
    }

    private Integer getNextId() {
        Integer currentMaxId = users.keySet().stream().mapToInt(id -> id).max().orElse(0);
        return ++currentMaxId;
    }

    private void validateUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            log.warn("Получено пустое имя пользователя, вместо него будет использован логин: {}",
                    user.getLogin());
            user.setName(user.getLogin());
        }
    }

    private void validateUpdateUser(User user) {
        if (user.getEmail().isEmpty() || !user
                .getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            log.warn("Email пользователя: {}", user.getEmail());
            throw new ValidationException("Email пустой или имеет неверный формат");
        }
        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.warn("Логин пользователя: {}", user.getLogin());
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
        validateUser(user);
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения пользователя: {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
