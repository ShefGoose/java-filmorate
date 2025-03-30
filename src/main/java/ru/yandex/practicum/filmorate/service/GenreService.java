package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.genre.GenreStorage;

import java.util.List;
import java.util.NoSuchElementException;

import static ru.yandex.practicum.filmorate.exception.ConstantException.GENRE_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public List<Genre> readAll() {
        return genreStorage.readAll();
    }

    public Genre read(int id) {
        if (!genreStorage.contains(id)) {
            log.warn(String.format(GENRE_NOT_FOUND, id));
            throw new NoSuchElementException(String.format(GENRE_NOT_FOUND, id));
        }
        return genreStorage.read(id);
    }
}
