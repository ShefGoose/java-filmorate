package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validation.Marker;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
public class FilmControllerTest {
    private Validator validator;
    private Film film;
    private Mpa mpa;

    @Autowired
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        film = new Film();
        mpa = new Mpa();
        mpa.setId(1);
    }

    @Test
    void shouldValidateFilmSuccess() {
        film.setName("NewFilm");
        film.setDescription("Good Film");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2000, 12, 1));
        film.setMpa(mpa);

        Set<ConstraintViolation<Film>> violations = validator.validate(film, Marker.OnCreate.class);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldValidateNameFilmFail() {
        film.setName(null);
        film.setDescription("Good Film");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2000, 12, 1));
        film.setMpa(mpa);

        Set<ConstraintViolation<Film>> violations = validator.validate(film, Marker.OnCreate.class);

        assertFalse(violations.isEmpty());
        assertThat(violations).hasSize(1);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Имя фильма не должно быть пустым");
    }

    @Test
    void shouldValidateDescriptionFilmFail() {
        film.setName("NewFilm");
        film.setDescription(StringUtils.repeat("d", 201));
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2000, 12, 1));
        film.setMpa(mpa);

        Set<ConstraintViolation<Film>> violations = validator.validate(film, Marker.OnCreate.class);

        assertFalse(violations.isEmpty());
        assertThat(violations).hasSize(1);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Длина описания превышает 200 символов");
    }

    @Test
    void shouldValidateDurationFilmFail() {
        film.setName("NewFilm");
        film.setDescription("Good Film");
        film.setDuration(-20);
        film.setReleaseDate(LocalDate.of(2000, 12, 1));
        film.setMpa(mpa);

        Set<ConstraintViolation<Film>> violations = validator.validate(film, Marker.OnCreate.class);

        assertFalse(violations.isEmpty());
        assertThat(violations).hasSize(1);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Продолжительность фильма должна быть положительным числом");
    }

    @Test
    void shouldValidateReleaseDateFilmFail() {
        film.setName("NewFilm");
        film.setDescription("Good Film");
        film.setDuration(100);
        film.setReleaseDate(null);
        film.setMpa(mpa);

        Set<ConstraintViolation<Film>> violations = validator.validate(film, Marker.OnCreate.class);

        assertFalse(violations.isEmpty());
        assertThat(violations).hasSize(1);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Дата релиза фильма не может быть null");
    }

    @Test
    void shouldValidateDurationNullFail() {
        film.setName("NewFilm");
        film.setDescription("Good Film");
        film.setDuration(null);
        film.setReleaseDate(LocalDate.of(2000, 12, 1));
        film.setMpa(mpa);

        Set<ConstraintViolation<Film>> violations = validator.validate(film, Marker.OnCreate.class);

        assertFalse(violations.isEmpty());
        assertThat(violations).hasSize(1);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Продолжительность фильма не может быть null");
    }

    @Test
    void shouldValidateDescriptionNullFail() {
        film.setName("NewFilm");
        film.setDescription(null);
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2000, 12, 1));
        film.setMpa(mpa);

        Set<ConstraintViolation<Film>> violations = validator.validate(film, Marker.OnCreate.class);

        assertFalse(violations.isEmpty());
        assertThat(violations).hasSize(1);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Описание фильма не может быть null");
    }

    @Test
    void shouldValidateBirthdayFilmFail() {
        film.setName("NewFilm");
        film.setDescription("Good Film");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(1700,12,1));
        film.setMpa(mpa);

        Set<ConstraintViolation<Film>> violations = validator.validate(film, Marker.OnCreate.class);

        assertFalse(violations.isEmpty());
        assertThat(violations).hasSize(1);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Дата релиза фильма не может быть раньше 12 декабря 1895 года");
    }

    @Test
    void shouldValidateMpaFilmFail() {
        film.setName("NewFilm");
        film.setDescription("Good Film");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2000, 12, 1));
        film.setMpa(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(film, Marker.OnCreate.class);

        assertFalse(violations.isEmpty());
        assertThat(violations).hasSize(1);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "У фильма должен быть указан рейтинг MPA");
    }
}
