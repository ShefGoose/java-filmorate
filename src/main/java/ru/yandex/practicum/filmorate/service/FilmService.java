package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class FilmService {

    private static final int COUNT_POPULAR_FILMS = 10;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }


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

        filmWithId.getUserLikes().add(userId);
        filmWithId.setLikes(filmWithId.getLikes() + 1);
        return filmStorage.update(filmWithId);
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

        filmWithId.getUserLikes().remove(userId);
        filmWithId.setLikes(filmWithId.getLikes() - 1);
        return filmStorage.update(filmWithId);
    }

    public List<Film> readPopularFilms(int count) {
        if (count == 0) {
            count = COUNT_POPULAR_FILMS;
        }
        return filmStorage.readAll().stream()
                .sorted(Comparator.comparing(Film::getLikes).reversed())
                .limit(count)
                .toList();
    }
}
