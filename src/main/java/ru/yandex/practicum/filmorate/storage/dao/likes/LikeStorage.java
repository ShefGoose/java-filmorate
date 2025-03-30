package ru.yandex.practicum.filmorate.storage.dao.likes;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.mapper.LikeRowMapper;

import java.util.Objects;

@Component
@Slf4j
@AllArgsConstructor
public class LikeStorage {
    private final JdbcTemplate jdbc;
    private final LikeRowMapper mapper;

    public void add(Integer filmId, Integer userId) {
        String query = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbc.update(query, filmId, userId);
        log.info("Фильму ID_{} добавлен лайк от пользователя ID_{}", filmId, userId);
    }

    public void delete(Integer filmId, Integer userId) {
        String query = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbc.update(query, filmId, userId);
        log.info("У фильма ID_{} удалён лайк от пользователя ID_{}", filmId, userId);
    }

    public int count(int id) {
        String query = "SELECT COUNT(*) FROM film_likes WHERE film_id= ?";
        Integer count = Objects.requireNonNull(
                jdbc.queryForObject(query, Integer.class, id));
        log.info("Подсчитано количество лайков для фильма ID_{}: {}", id, count);
        return count;
    }

    public boolean contains(int filmId, int userId) {
        String query = "SELECT * FROM film_likes WHERE film_id= ? AND user_id= ?";
        try {
            jdbc.queryForObject(query, mapper, filmId, userId);
            log.info("Найден лайк у фильма ID_{} от пользователя ID_{}", filmId, userId);
            return true;
        } catch (EmptyResultDataAccessException ex) {
            log.warn("Не найден лайк у фильма ID_{} от пользователя ID_{}", filmId, userId);
            return false;
        }
    }
}
