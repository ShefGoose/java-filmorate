package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation.Marker;

import java.util.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<Film> getListFilms() {
        return filmService.readAll();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) {
        return filmService.read(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(Marker.OnCreate.class)
    public Film postFilm(@Valid @RequestBody Film film) {
         return filmService.create(film);
    }

    @PutMapping
    @Validated(Marker.OnUpdate.class)
    public Film updateFilm(@Valid @RequestBody Film film) {
        return  filmService.update(film);
    }

    @PutMapping ("{id}/like/{userId}")
    public void likeFilm(@PathVariable("id") int id,
                         @PathVariable("userId") int userId) {
         filmService.addLike(id, userId);
    }

    @DeleteMapping ("{id}/like/{userId}")
    public void deleteLikeFilm(@PathVariable("id") int id,
                         @PathVariable("userId") int userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(name = "count", defaultValue = "10")
                                      @Positive(message = "Количество фильмов должно быть положительным числом")
                                          int count) {
        return filmService.getPopularFilms(count);
    }
}
