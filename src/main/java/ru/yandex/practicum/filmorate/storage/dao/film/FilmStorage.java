package ru.yandex.practicum.filmorate.storage.dao.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> readAll();

    Film create(Film film);

    Film update(Film film);

    void delete(int id);

    Film read(int id);

    List<Film> getPopular(int count);
}
