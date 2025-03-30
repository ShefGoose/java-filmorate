package ru.yandex.practicum.filmorate.storage.dao.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface FilmStorage {

    List<Film> readAll();

    Film create(Film film);

    Film update(Film film);

    void delete(int id);

    Film read(int id);

    void addGenres(int filmId, Set<Genre> genres);

    void updateGenres(int filmId, Set<Genre> genres);

    Set<Genre> getGenres(int filmId);

    boolean contains(int id);
}
