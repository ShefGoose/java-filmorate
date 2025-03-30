package ru.yandex.practicum.filmorate.storage.likes;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mapper.LikeRowMapper;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static java.lang.String.format;

@Component
@AllArgsConstructor
public class LikeStorage {
    private final JdbcTemplate jdbc;
    private final LikeRowMapper mapper;

    public void addLike(Integer filmId, Integer userId) {
        String query = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbc.update(query, filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        String query = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbc.update(query, filmId, userId);
    }

    public int count(int id) {
        String query = "SELECT COUNT(*) FROM film_likes WHERE film_id= ?";
        return Objects.requireNonNull(
                jdbc.queryForObject(format(query, id), Integer.class));
    }

    public boolean contains(int filmId, int userId) {
        String query = "SELECT * FROM film_likes WHERE film_id= ? AND user_id= ?";
        try {
            jdbc.queryForObject(format(query, filmId, userId), mapper);
            return true;
        } catch (EmptyResultDataAccessException ex) {
            return false;
        }
    }

}
