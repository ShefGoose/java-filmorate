package ru.yandex.practicum.filmorate.storage.dao.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.FilmGenreMapper;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

import static ru.yandex.practicum.filmorate.exception.ConstantException.FILM_NOT_FOUND;


@Repository
@Slf4j
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbc;
    private final FilmRowMapper mapper;
    private final FilmGenreMapper filmGenreMapper;

    @Override
    public List<Film> readAll() {
        String query = "SELECT * FROM films";
        return jdbc.query(query,mapper);
    }

    @Override
    public Film read(int id) {
        String query = "SELECT * FROM films WHERE id = ?";
        return jdbc.queryForObject(query, mapper, id);
    }

    @Override
    public Film create(Film film) {
        log.info("Добавляем фильм({})", film);

        String query = "INSERT INTO films (name, description, release_date, duration, rating_id) "
               + "VALUES(?, ?, ?, ?, ?)";

        KeyHolder filmKeyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, film.getName());
            pst.setString(2, film.getDescription());
            pst.setDate(3, Date.valueOf(film.getReleaseDate()));
            pst.setInt(4, film.getDuration());
            pst.setInt(5, film.getMpa().getId());
            return pst;
        }, filmKeyHolder);

        int generateId = Objects.requireNonNull(filmKeyHolder.getKey()).intValue();
        film.setId(generateId);

        String queryResult = "SELECT * FROM films WHERE id = ?";

        Film resultFilm = jdbc.queryForObject(queryResult, mapper, film.getId());
        log.info("Успешно добавлен фильм: {}", resultFilm);
        return resultFilm;
    }

    @Override
    public Film update(Film film) {
        log.info("Обновляем фильм({})", film);

        if (film.getName() != null) {
            if (film.getName().isEmpty()) {
                throw new ValidationException("Название фильма не должно быть пустым");
            }
            jdbc.update("UPDATE films SET name = ? WHERE id = ?",
                    film.getName(),
                    film.getId());
        }

        if (film.getDescription() != null) {
            jdbc.update("UPDATE films SET description = ? WHERE id = ?",
                    film.getDescription(),
                    film.getId());
        }

        if (film.getDuration() != null) {
            jdbc.update("UPDATE films SET duration = ? WHERE id = ?",
                    film.getDuration(),
                    film.getId());
        }

        if (film.getReleaseDate() != null) {
            jdbc.update("UPDATE films SET release_date = ? WHERE id = ?",
                    Date.valueOf(film.getReleaseDate()),
                    film.getId());
        }

        if (film.getMpa() != null) {
            jdbc.update("UPDATE films SET rating_id = ? WHERE id = ?",
                    film.getMpa().getId(),
                    film.getId());
        }

        Film updateFilm = read(film.getId());
        log.info("Обновлен фильм: {}", updateFilm);
        return updateFilm;
    }

    @Override
    public void delete(int id) {
        Film film = read(id);
        String query = "DELETE FROM films WHERE id = ?";
        if (jdbc.update(query, id) == 0) {
            log.warn(String.format(FILM_NOT_FOUND, id));
            throw new NoSuchElementException(String.format(FILM_NOT_FOUND, id));
        }
    }

    @Override
    public void addGenres(int filmId, Set<Genre> genres) {
        log.info("Добавляем фильму ID_{}, жанры ({})", filmId, genres);
        String query = "INSERT INTO film_genres (film_id, genre_id)  VALUES (?, ?)";
        for (Genre genre : genres) {
            jdbc.update(query, filmId, genre.getId());
            log.info("Фильму ID_{} добавлен жанр ID_{}", filmId, genre.getId());
        }
    }

    @Override
    public void updateGenres(int filmId, Set<Genre> genres) {
        jdbc.update("DELETE FROM film_genres WHERE film_id=?", filmId);
        addGenres(filmId, genres);
        log.info("Обновлены жанры у фильма ID_{}: {}", filmId, genres);
    }

    @Override
    public Set<Genre> getGenres(int filmId) {
        String query = "SELECT f.genre_id, g.name FROM film_genres AS f "
                + "LEFT JOIN genres AS g ON f.genre_id = g.id "
                + "WHERE f.film_id = ? ";
        Set<Genre> genres = new LinkedHashSet<>(jdbc.query(query, filmGenreMapper, filmId));
        log.info("Возвращены все жанры для фильма ID_{}: {}", filmId, genres);
        return genres;
    }

    @Override
    public boolean contains(int id) {
        try {
            read(id);
            log.info("Найден фильм ID_{}", id);
            return true;
        } catch (EmptyResultDataAccessException ex) {
            log.warn("Не найден фильм ID_{}", id);
            return false;
        }
    }
}
