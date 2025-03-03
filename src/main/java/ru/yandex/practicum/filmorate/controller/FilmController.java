package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private static final LocalDate MOVIE_BIRTHDAY = LocalDate.of(1895, 12, 28);

    @GetMapping
    public List<Film> listFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film postFilm(@Valid @RequestBody Film film) {
        validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (film.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        validateFilm(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        }
        throw new NoSuchElementException("Фильм с id = " + film.getId() + " не найден");
    }

    private Integer getNextId() {
        Integer currentMaxId = films.keySet().stream().mapToInt(id -> id).max().orElse(0);
        return ++currentMaxId;
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(MOVIE_BIRTHDAY)) {
            log.warn("Дата релиза фильма: {}: {}", film.getName(),
                    film.getReleaseDate());
            throw new ValidationException("Дата релиза раньше 12 декабря 1895 года");
        }
        }
    }

