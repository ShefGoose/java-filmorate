package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Getter
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film addLike(Integer userId, Integer filmId) {
        log.info("Получены следущие параметры запроса для добавления лайка фильму: Id пользователя: {}, Id фильма: {}",
                userId, filmId);
        if (userStorage.read(userId) == null) {
            throw new NoSuchElementException("Такого пользователя не существует");
        }

        Film filmWithId = filmStorage.read(filmId);

        if (filmWithId == null) {
            throw new NoSuchElementException("Такого фильма не существует");
        }
        if (filmWithId.getUserLikes().contains(userId)) {
            throw new IllegalArgumentException("Этот пользователь уже ставил лайк");
        }

        increaseCountLikesFilm(userId, filmWithId);
        return filmWithId;
    }

    public Film deleteLike(Integer userId, Integer filmId) {
        log.info("Получены следущие параметры запроса для удаления лайка фильму: Id пользователя: {}, Id фильма: {}",
                userId, filmId);
        Film filmWithId = filmStorage.read(filmId);

        if (filmWithId == null) {
            throw new NoSuchElementException("Такого фильма не существует");
        }
        if (!filmWithId.getUserLikes().contains(userId)) {
            throw new NoSuchElementException("Этот пользователь не ставил лайк");
        }

        decreaseCountLikesFilm(userId, filmWithId);
        return filmWithId;
    }

    public List<Film> readPopularFilms(int count) {
        return filmStorage.readAll().stream()
                .sorted(Comparator.comparing(Film::getLikes).reversed())
                .limit(count)
                .toList();
    }

    private void increaseCountLikesFilm(Integer userId, Film film) {
        film.getUserLikes().add(userId);
        film.setLikes(film.getLikes() + 1);
    }

    private void decreaseCountLikesFilm(Integer userId, Film film) {
        film.getUserLikes().remove(userId);
        film.setLikes(film.getLikes() - 1);
    }
}
