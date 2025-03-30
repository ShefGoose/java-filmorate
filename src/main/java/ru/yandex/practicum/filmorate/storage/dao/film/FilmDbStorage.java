package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.*;

import static ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage.MAX_SIZE_DESCRIPTION;
import static ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage.MOVIE_BIRTHDAY;

@Component("FilmDbStorage")
@Slf4j
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbc;
    private final FilmRowMapper mapper;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

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
        log.info("Получены следущие параметры фильма: название: {}, описание: {}, продолжительность: {}, дата релиза {}",
                film.getName(), film.getDescription(),
                film.getDuration(), film.getReleaseDate());
        validateFilm(film);
        SimpleJdbcInsert insertFilm = new SimpleJdbcInsert(jdbc)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        film.setId(insertFilm.executeAndReturnKey(film.toParameters()).intValue());
        film.setMpa(mpaStorage.read(film.getMpa().getId()));
        for (Genre genre : film.getGenres()) {
            genre.setName(genreStorage.read(genre.getId()).getName());
        }
        genreStorage.delete(film);
        genreStorage.add(film);
        log.info("Фильм с названием: {}, успешно добавлен", film.getName());
        return film;
    }

    @Override
    public Film update(Film film) {
        log.info("Получены следущие параметры фильма: {}", film.toString());

        if (film.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        Film updateFilm = films.get(film.getId());
        if (updateFilm == null) {
            throw new NoSuchElementException("Фильм с id = " + film.getId() + " не найден");
        }

        if (!(film.getName() == null)) updateFilm.setName(film.getName());
        if (!(film.getDescription() == null)) updateFilm.setDescription(film.getDescription());
        if (!(film.getDuration() == null))  updateFilm.setDuration(film.getDuration());
        if (!(film.getReleaseDate() == null))  updateFilm.setReleaseDate(film.getReleaseDate());

        validateUpdateFilm(updateFilm);
        films.put(updateFilm.getId(), updateFilm);
        log.info("Все переданные не null значения фильма с id: {}, успешно обновлены", film.getId());
        return updateFilm;
    }

    @Override
    public void delete(int id) {
        films.remove(id);
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(MOVIE_BIRTHDAY)) {
            log.warn("Дата релиза фильма: {}: {}", film.getName(),
                    film.getReleaseDate());
            throw new ValidationException("Дата релиза раньше 12 декабря 1895 года");
        }
    }

    private void validateUpdateFilm(Film film) {
        if (film.getName().isEmpty()) {
            log.warn("Название фильма: {}", film.getName());
            throw new ValidationException("Название фильма не должно быть пустым");
        }
        if (film.getDescription().length() > MAX_SIZE_DESCRIPTION) {
            log.warn("Длина описания фильма: {}", film.getDescription().length());
            throw new ValidationException("Длина описания превышает 200 символов");
        }
        validateFilm(film);
        if (film.getDuration() <= 0) {
            log.warn("Продолжительность фильма: {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }

}
