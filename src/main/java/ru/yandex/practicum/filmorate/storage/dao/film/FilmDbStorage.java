package ru.yandex.practicum.filmorate.storage.dao.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.mpa.MpaStorage;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.exception.ConstantException.FILM_NOT_FOUND;


@Repository
@Slf4j
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbc;
    private final MpaStorage mpaStorage;

    @Override
    public List<Film> readAll() {
        String query = getReadQuery();
        return executeFilmQuery(query);
    }

    @Override
    public Film read(int id) {
        String query = getReadQuery() + "WHERE f.id = ?";

        List<Film> result = executeFilmQuery(query, id);
        if (result.isEmpty()) {
            log.warn("Не найден фильм ID_{}", id);
            throw new NoSuchElementException(String.format(FILM_NOT_FOUND, id));
        }
        log.info("Найден фильм ID_{}", id);
        return result.getFirst();
    }


    @Override
    public Film create(Film film) {
        if (film.getMpa() != null) {
            mpaStorage.read(film.getMpa().getId());
        }

        validateFilmGenres(film);

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
        addGenres(film.getId(), film.getGenres());


        log.info("Успешно добавлен фильм: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        log.info("Обновляем фильм ID_{}", film.getId());
        String query = "UPDATE films SET name = ?, "
                + "description = ?, "
                + "duration = ?, "
                + "release_date = ?, "
                + "rating_id = ? WHERE id = ?";
        mpaStorage.read(film.getMpa().getId());
        validateFilmGenres(film);

        jdbc.update(query, film.getName(), film.getDescription(), film.getDuration(),
                Date.valueOf(film.getReleaseDate()), film.getMpa().getId(), film.getId());

        updateGenres(film.getId(), film.getGenres());


        log.info("Обновлен фильм: {}", film);
        return film;
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
    public List<Film> getPopular(int count) {
        String query = "SELECT "
                + "f.id AS film_id, "
                + "f.name AS film_name, "
                + "f.description, "
                + "f.release_date, "
                + "f.duration, "
                + "m.id AS mpa_id, "
                + "m.name AS mpa_name, "
                + "m.description AS mpa_description, "
                + "g.id AS genre_id, "
                + "g.name AS genre_name, "
                + "fl.likes_count "
                + "FROM films f "
                + "JOIN MPA m ON f.rating_id = m.id "
                + "LEFT JOIN film_genres fg ON f.id = fg.film_id "
                + "LEFT JOIN genres g ON fg.genre_id = g.id "
                + "JOIN (SELECT film_id, COUNT(*) as likes_count "
                + "FROM film_likes "
                + "GROUP BY film_id) fl ON fl.film_id = f.id "
                + "ORDER BY fl.likes_count DESC "
                + "LIMIT ?";

        return executeFilmQuery(query, count);
    }

    private void addGenres(int filmId, Set<Genre> genres) {
        if (genres.isEmpty()) {
            log.info("Список жанров пуст, ничего добавлять не нужно");
            return;
        }

        log.info("Добавляем фильму ID_{}, жанры ({})", filmId, genres);

        List<Object[]> batchParams = genres.stream()
                .map(genre -> new Object[]{filmId, genre.getId()})
                .collect(Collectors.toList());

        String query = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

        int[] updateCounts = jdbc.batchUpdate(query, batchParams);

        if (updateCounts.length > 0) {
            log.info("Фильму ID_{} успешно добавлены {} жанров", filmId, updateCounts.length);
        }
    }

    private void updateGenres(int filmId, Set<Genre> genres) {
        jdbc.update("DELETE FROM film_genres WHERE film_id=?", filmId);
        addGenres(filmId, genres);
        log.info("Обновлены жанры у фильма ID_{}: {}", filmId, genres);
    }


    private void validateFilmGenres(Film film) {
        Set<Integer> filmGenresIds = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        String query = "SELECT id FROM genres";
        List<Integer> existingGenresIds = jdbc.queryForList(query, Integer.class);

        Set<Integer> existingGenresIdsSet = new HashSet<>(existingGenresIds);
        Set<Integer> invalidGenresIds = new HashSet<>(filmGenresIds);
        invalidGenresIds.removeAll(existingGenresIdsSet);

        if (!invalidGenresIds.isEmpty()) {
            String errorMessage = String.format("Недействительные жанры: %s", invalidGenresIds);
            log.warn(errorMessage);
            throw new NoSuchElementException(errorMessage);
        }
    }

    private String getReadQuery() {
        return "SELECT "
                + "f.id AS film_id, "
                + "f.name AS film_name, "
                + "f.description, "
                + "f.release_date, "
                + "f.duration, "
                + "m.id AS mpa_id, "
                + "m.name AS mpa_name, "
                + "m.description AS mpa_description, "
                + "g.id AS genre_id, "
                + "g.name AS genre_name "
                + "FROM films f "
                + "JOIN MPA m ON f.rating_id = m.id "
                + "LEFT JOIN film_genres fg ON f.id = fg.film_id "
                + "LEFT JOIN genres g ON fg.genre_id = g.id ";
    }

    private List<Film> executeFilmQuery(String query, Object... params) {
        List<Film> allFilms = new ArrayList<>();
        Map<Integer, Film> filmMap = new HashMap<>();

        jdbc.query(query, (rs, rowNum) -> {
            Integer filmId = rs.getInt("film_id");

            Film film = filmMap.get(filmId);
            if (film == null) {
                film = new Film();
                film.setId(filmId);
                film.setName(rs.getString("film_name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate((rs.getDate("release_date")).toLocalDate());
                film.setDuration(rs.getInt("duration"));

                Mpa mpa = new Mpa();
                mpa.setId(rs.getInt("mpa_id"));
                mpa.setName(rs.getString("mpa_name"));
                mpa.setDescription(rs.getString("mpa_description"));
                film.setMpa(mpa);

                film.setGenres(new LinkedHashSet<>());
                filmMap.put(filmId, film);
                allFilms.add(film);
            }

            if (rs.getInt("genre_id") != 0) {
                Genre genre = new Genre();
                genre.setId(rs.getInt("genre_id"));
                genre.setName(rs.getString("genre_name"));
                film.getGenres().add(genre);
            }
            return film;
        }, params);

        if (allFilms.isEmpty()) {
            log.warn("Фильмы не найдены");
            return Collections.emptyList();
        }

        log.info("Найдено {} фильмов", allFilms.size());
        return allFilms;
    }
}
