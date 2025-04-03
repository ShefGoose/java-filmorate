package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.likes.LikeStorage;
import ru.yandex.practicum.filmorate.storage.dao.user.UserStorage;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        Film needUpdateFilm = filmStorage.read(film.getId());

        if (film.getName() != null) {
            if (film.getName().isEmpty()) {
                throw new ValidationException("Название фильма не должно быть пустым");
            }
            needUpdateFilm.setName(film.getName());
        }

        if (film.getDescription() != null) {
            needUpdateFilm.setDescription(film.getDescription());
        }

        if (film.getDuration() != null) {
            needUpdateFilm.setDuration(film.getDuration());
        }

        if (film.getReleaseDate() != null) {
            needUpdateFilm.setReleaseDate(film.getReleaseDate());
        }

        if (film.getMpa() != null) {
            needUpdateFilm.setMpa(film.getMpa());
        }
        if (film.getGenres() != null) {
            needUpdateFilm.setGenres(film.getGenres());
        }

        return filmStorage.update(needUpdateFilm);
    }

    public Film read(int id) {
        return filmStorage.read(id);
    }

    public List<Film> readAll() {
        return filmStorage.readAll();
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopular(count);
    }

    public void addLike(int filmId, int userId) {
        filmStorage.read(filmId);
        userStorage.read(userId);
        likeStorage.add(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        filmStorage.read(filmId);
        userStorage.read(userId);
        likeStorage.delete(filmId, userId);
    }
}
