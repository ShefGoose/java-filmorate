package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class MovieBirthdayValidator implements ConstraintValidator<AfterMovieBirthday, LocalDate> {

    private static final LocalDate MOVIE_BIRTHDAY = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext cxt) {
        return date == null || date.isAfter(MOVIE_BIRTHDAY);
    }
}
