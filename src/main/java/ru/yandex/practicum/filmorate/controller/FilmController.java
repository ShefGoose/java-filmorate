package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<Film> getListFilms() {
        return filmService.getFilmStorage().readAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film postFilm(@Valid @RequestBody Film film) {
        return filmService.getFilmStorage().create(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        return  filmService.getFilmStorage().update(film);
    }

    @PutMapping ("{id}/like/{userId}")
    public Film likeFilm(@PathVariable("id") int id,
                         @PathVariable("userId") int userId) {
        return filmService.addLike(userId, id);
    }

    @DeleteMapping ("{id}/like/{userId}")
    public Film deleteLikeFilm(@PathVariable("id") int id,
                         @PathVariable("userId") int userId) {
        return filmService.deleteLike(userId, id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(name = "count", defaultValue = "10")
                                      @Positive(message = "Количество фильмов должно быть положительным числом")
                                          int count) {
        return filmService.readPopularFilms(count);
    }
}
