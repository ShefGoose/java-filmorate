package ru.yandex.practicum.filmorate.storage.dao.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
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


    @Override
    public User read(int id) {
        String query = "SELECT * "
                + "FROM users "
                + "WHERE id = ?";
        List<User> users = jdbc.query(query, mapper, id);
        if (users.isEmpty()) {
            log.warn("Не найден пользователь ID_{}", id);
            throw new NoSuchElementException(String.format(USER_NOT_FOUND, id));
        } else {
            log.info("Возвращён пользователь: {}", users.getFirst());
            return users.getFirst();
        }
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

        log.info("Успешно добавлен пользователь: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        log.info("Обновляем данные пользователя: {}", user);
        String query = "UPDATE users SET email = ?, "
                + "login = ?, "
                + "name = ?, "
                + "birthday = ? "
                + "WHERE id = ?";
        jdbc.update(query, user.getEmail(), user.getLogin(), user.getName(),
                Date.valueOf(user.getBirthday()), user.getId());

        log.info("Успешно обновлены данные пользователя: {}", user);
        return user;
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


}
