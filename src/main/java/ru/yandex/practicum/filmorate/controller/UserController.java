package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

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
        validateUser(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (user.getId() == null) {
            log.warn("Id пользователя с логином: {}: {}", user.getLogin(), null);
            throw new ValidationException("Id должен быть указан");
        }
        validateUser(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        }
        throw new NoSuchElementException("Пользователь с id = " + user.getId() + " не найден");
    }

    private Integer getNextId() {
        Integer currentMaxId = users.keySet().stream().mapToInt(id -> id).max().orElse(0);
        return ++currentMaxId;
    }
    private void validateUser(User user) {
        if (user.getLogin().contains(" ")) {
            log.warn("Получен следующий логин: {}", user.getLogin());
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            log.warn("Получено пустое имя пользователя, вместо него будет использован логин: {}",
                    user.getLogin());
            user.setName(user.getLogin());
        }
    }

}
