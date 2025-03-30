package ru.yandex.practicum.filmorate.storage.dao.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.LikeRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

import static ru.yandex.practicum.filmorate.exception.ConstantException.USER_NOT_FOUND;

@Repository("UserDbStorage")
@Slf4j
@AllArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final UserMapper mapper;
    private final LikeRowMapper likeMapper;

    @Override
    public User read(int id) {
        String query = "SELECT * "
                + "FROM users "
                + "WHERE id = ?";
        User user = jdbc.queryForObject(query, mapper, id);
        log.info("Возвращён пользователь: {}", user);
        return user;
    }

    @Override
    public List<User> readAll() {
        String query = "SELECT * "
                + "FROM users";
        List<User> users = jdbc.query(query, mapper);
        log.info("Возвращены все пользователи: {}", users);
        return users;
    }

    @Override
    public User create(User user) {
        log.info("Добавляем пользователя: {}", user);

        String query = "INSERT INTO users (email, login, name, birthday) "
                + "VALUES (?, ?, ?, ?)";

        KeyHolder userKeyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, user.getEmail());
            pst.setString(2, user.getLogin());
            pst.setString(3, user.getName());
            pst.setDate(4, Date.valueOf(user.getBirthday()));
            return pst;
        }, userKeyHolder);

        int generateId = Objects.requireNonNull(userKeyHolder.getKey()).intValue();
        user.setId(generateId);

        String queryResult = "SELECT * FROM users WHERE id = ?";

        User result = jdbc.queryForObject(queryResult, mapper, user.getId());
        log.info("Успешно добавлен пользователь: {}", result);
        return result;
    }

    @Override
    public User update(User user) {
        log.info("Обновляем данные пользователя: {}", user);
        if (user.getEmail() != null) {
            if (user.getEmail().isEmpty()) {
                throw new ValidationException("Email пользователя не должен быть пустым");
            }
            jdbc.update("UPDATE users SET email = ? WHERE id = ?", user.getEmail(), user.getId());
        }

        if (user.getLogin() != null) {
            if (user.getLogin().isEmpty()) {
                throw new ValidationException("Логин не может быть пустым");
            }
            jdbc.update("UPDATE users SET login = ? WHERE id = ?", user.getLogin(), user.getId());
        }

        if (user.getName() != null) {
            if (!user.getName().isEmpty()) {
                jdbc.update("UPDATE users SET name = ? WHERE id = ?", user.getName(), user.getId());
            } else {
                if (user.getLogin() != null && !user.getLogin().isEmpty()) {
                    jdbc.update("UPDATE users SET name = ? WHERE id = ?", user.getLogin(), user.getId());
                }
            }
        }

        if (user.getBirthday() != null) {
            jdbc.update("UPDATE users SET birthday = ? WHERE id = ?",
                    Date.valueOf(user.getBirthday()), user.getId());
        }

        User result = read(user.getId());
        log.info("Успешно обновлены данные пользователя: {}", result);
        return result;
    }

    @Override
    public void delete(int id) {
        User user = read(id);
        String query = "DELETE FROM users WHERE id = ?";
        if (jdbc.update(query, id) == 0) {
            log.warn(String.format(USER_NOT_FOUND, id));
            throw new NoSuchElementException(String.format(USER_NOT_FOUND, id));
        }
    }

    @Override
    public boolean contains(int id) {
        try {
            read(id);
            log.info("Найден пользователь ID_{}", id);
            return true;
        } catch (EmptyResultDataAccessException ex) {
            log.warn("Не найден пользователь ID_{}", id);
            return false;
        }
    }
}
