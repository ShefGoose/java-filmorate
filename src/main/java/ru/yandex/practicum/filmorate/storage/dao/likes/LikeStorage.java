package ru.yandex.practicum.filmorate.storage.dao.likes;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class LikeStorage {
    private final JdbcTemplate jdbc;

    public void add(Integer filmId, Integer userId) {
        String query = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";

        try {
            jdbc.update(query, filmId, userId);
            log.info("Фильму ID_{} добавлен лайк от пользователя ID_{}", filmId, userId);
        } catch (DuplicateKeyException e) {
            log.warn("Пользователь ID_{} уже ставил лайк фильму ID_{}", userId, filmId);
            throw e;
        }
    }

    public void delete(Integer filmId, Integer userId) {
        String query = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        int result = jdbc.update(query, filmId, userId);
        if (result == 0) {
            log.warn("У фильма ID_{} нет лайка от пользователя ID_{}", filmId, userId);
        } else {
            log.info("У фильма ID_{} удалён лайк от пользователя ID_{}", filmId, userId);
        }
    }
}
