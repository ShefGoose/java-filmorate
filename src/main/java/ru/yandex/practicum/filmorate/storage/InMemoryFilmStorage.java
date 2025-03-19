package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private static final LocalDate MOVIE_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private static final int MAX_SIZE_DESCRIPTION = 200;

    @Override
    public List<Film> readAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film read(int id) {
        return films.get(id);
    }

    @Override
    public Film create(Film film) {
        log.info("Получены следущие параметры фильма: название: {}, описание: {}, продолжительность: {}, дата релиза {}",
                film.getName(), film.getDescription(),
                film.getDuration(), film.getReleaseDate());
        validateFilm(film);
        film.setId(getNextId());
        film.setLikes(0);
        film.setUserLikes(new TreeSet<>());
        films.put(film.getId(), film);
        log.info("Фильм с названием: {}, успешно добавлен", film.getName());
        return film;
    }

    @Override
    public Film update(Film film) {
        log.info("Получены следущие параметры фильма: {}", film.toString());

        if (film.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        Film updateFilm = films.get(film.getId());
        if (updateFilm == null) {
            throw new NoSuchElementException("Фильм с id = " + film.getId() + " не найден");
        }

        if (!(film.getName() == null)) updateFilm.setName(film.getName());
        if (!(film.getDescription() == null)) updateFilm.setDescription(film.getDescription());
        if (!(film.getDuration() == null))  updateFilm.setDuration(film.getDuration());
        if (!(film.getReleaseDate() == null))  updateFilm.setReleaseDate(film.getReleaseDate());

        validateUpdateFilm(updateFilm);
        films.put(updateFilm.getId(), updateFilm);
        log.info("Все переданные не null значения фильма с id: {}, успешно обновлены", film.getId());
        return updateFilm;
    }

    @Override
    public void delete(int id) {
        films.remove(id);
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

    private void validateUpdateFilm(Film film) {
        if (film.getName().isEmpty()) {
            log.warn("Название фильма: {}", film.getName());
            throw new ValidationException("Название фильма не должно быть пустым");
        }
        if (film.getDescription().length() > MAX_SIZE_DESCRIPTION) {
            log.warn("Длина описания фильма: {}", film.getDescription().length());
            throw new ValidationException("Длина описания превышает 200 символов");
        }
        validateFilm(film);
        if (film.getDuration() <= 0) {
            log.warn("Продолжительность фильма: {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }

}
