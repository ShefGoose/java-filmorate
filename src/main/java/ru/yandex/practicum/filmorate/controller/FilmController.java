package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmController(FilmService filmService, FilmStorage filmStorage) {
        this.filmService = filmService;
        this.filmStorage = filmStorage;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getListFilms() {
        return  filmStorage.readAll();

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film postFilm(@Valid @RequestBody Film film) {
        return filmStorage.create(film);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film updateFilm(@RequestBody Film film) {
        return filmStorage.update(film);
    }

    @PutMapping ("{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Film likeFilm(@PathVariable("id") int id,
                         @PathVariable("userId") int userId) {
        return filmService.addLike(userId, id);
    }

    @DeleteMapping ("{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Film deleteLikeFilm(@PathVariable("id") int id,
                         @PathVariable("userId") int userId) {
        return filmService.deleteLike(userId, id);
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "0") int count) {
        return filmService.readPopularFilms(count);
    }
}
