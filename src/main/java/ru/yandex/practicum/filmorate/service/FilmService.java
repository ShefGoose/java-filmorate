package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.dao.likes.LikeStorage;
import ru.yandex.practicum.filmorate.storage.dao.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.dao.user.UserStorage;

import java.util.List;
import java.util.NoSuchElementException;

import static ru.yandex.practicum.filmorate.exception.ConstantException.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final LikeStorage likeStorage;
    private final MpaStorage mpaStorage;

    public Film create(Film film) {
        checkFilmToAdd(film);
        Film result = filmStorage.create(film);
        filmStorage.addGenres(result.getId(), film.getGenres());
        result.setGenres(filmStorage.getGenres(result.getId()));
        result.setMpa(mpaStorage.read(film.getMpa().getId()));
        return result;
    }

    public Film update(Film film) {
        checkFilmToUpdate(film);
        Film result = filmStorage.update(film);
        if (!film.getGenres().isEmpty()) {
            filmStorage.updateGenres(result.getId(), film.getGenres());
            result.setGenres(filmStorage.getGenres(result.getId()));
        }
        result.setMpa(mpaStorage.read(result.getMpa().getId()));
        return result;
    }

    public Film read(int id) {
        if (!filmStorage.contains(id)) {
            log.warn("Не удалось вернуть фильм: {}.", String.format(FILM_NOT_FOUND, id));
            throw new NoSuchElementException(String.format(FILM_NOT_FOUND, id));
        }
        Film film = filmStorage.read(id);
        film.setGenres(filmStorage.getGenres(film.getId()));
        film.setMpa(mpaStorage.read(film.getMpa().getId()));
        return film;
    }

    public List<Film> readAll() {
        List<Film> films = filmStorage.readAll();
        for (Film film : films) {
            film.setGenres(filmStorage.getGenres(film.getId()));
            film.setMpa(mpaStorage.read(film.getMpa().getId()));
        }
        return films;
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> popularFilms = filmStorage.readAll().stream().sorted(this::likeCompare).limit(count).toList();
        log.info("Возвращены популярные фильмы: {}", popularFilms);
        return popularFilms;
    }

    public void addLike(int filmId, int userId) {
        checkLikeToAdd(filmId, userId);
        likeStorage.add(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        checkLikeToDelete(filmId, userId);
        likeStorage.delete(filmId, userId);
    }

    private int likeCompare(Film film, Film otherFilm) {
        return Integer.compare(likeStorage.count(otherFilm.getId()), likeStorage.count(film.getId()));
    }

    private void checkFilmToAdd(Film film) {
        if (film.getMpa() != null && !mpaStorage.contains(film.getMpa().getId())) {
            log.warn(String.format(MPA_NOT_FOUND, film.getMpa().getId()));
            throw new NoSuchElementException(String.format(MPA_NOT_FOUND, film.getMpa().getId()));
        }

        for (Genre genre : film.getGenres()) {
            if (!genreStorage.contains(genre.getId())) {
                log.warn(String.format(GENRE_NOT_FOUND, genre.getId()));
                throw new NoSuchElementException(String.format(GENRE_NOT_FOUND, genre.getId()));
            }
        }
    }

    private void checkFilmToUpdate(Film film) {
        if (!filmStorage.contains(film.getId())) {
            log.warn(String.format(FILM_NOT_FOUND, film.getId()));
            throw new NoSuchElementException(String.format(FILM_NOT_FOUND, film.getId()));
        }
        if (film.getMpa() != null && !mpaStorage.contains(film.getMpa().getId())) {
            log.warn(String.format(MPA_NOT_FOUND, film.getMpa().getId()));
            throw new NoSuchElementException(String.format(MPA_NOT_FOUND, film.getMpa().getId()));
        }
        for (Genre genre : film.getGenres()) {
            if (!genreStorage.contains(genre.getId())) {
                log.warn(String.format(GENRE_NOT_FOUND, genre.getId()));
                throw new NoSuchElementException(String.format(GENRE_NOT_FOUND, genre.getId()));
            }
        }
    }

    private void checkLikeToAdd(int filmId, int userId) {
        if (!filmStorage.contains(filmId)) {
            log.warn(String.format(FILM_NOT_FOUND, filmId));
            throw new NoSuchElementException(String.format(FILM_NOT_FOUND, filmId));
        }
        if (!userStorage.contains(userId)) {
            log.warn(String.format(USER_NOT_FOUND, userId));
            throw new NoSuchElementException(String.format(USER_NOT_FOUND, userId));
        }
        if (likeStorage.contains(filmId, userId)) {
            log.warn(String.format(LIKE_ALREADY_EXISTS, filmId, userId));
            throw new ValidationException(String.format(LIKE_ALREADY_EXISTS, filmId, userId));
        }
    }

    private void checkLikeToDelete(int filmId, int userId) {
        if (!filmStorage.contains(filmId)) {
            log.warn(String.format(FILM_NOT_FOUND, filmId));
            throw new NoSuchElementException(String.format(FILM_NOT_FOUND, filmId));
        }
        if (!userStorage.contains(userId)) {
            log.warn(String.format(USER_NOT_FOUND, userId));
            throw new NoSuchElementException(String.format(USER_NOT_FOUND, userId));
        }
        if (!likeStorage.contains(filmId, userId)) {
            log.warn(String.format(LIKE_NOT_FOUND, filmId, userId));
            throw new NoSuchElementException(String.format(LIKE_NOT_FOUND, filmId, userId));
        }
    }
}
