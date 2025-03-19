package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice("ru.yandex.practicum.filmorate.controller")
public class CustomErrorHandler extends ResponseEntityExceptionHandler {
    //400
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
                                                                  final HttpHeaders headers,
                                                                  final HttpStatusCode status,
                                                                  final WebRequest request) {
        final List<String> errors = new ArrayList<>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Ошибка валидации", errors);
        return handleExceptionInternal(ex, apiError, headers, apiError.getStatus(), request);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ApiError handleValidationException(final ValidationException e) {
        return new ApiError(HttpStatus.BAD_REQUEST, "Ошибка валидации", e.getLocalizedMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiError handleIllegalArgumentException(final IllegalArgumentException e) {
        return new ApiError(HttpStatus.BAD_REQUEST, "Неверное действие пользователя", e.getLocalizedMessage());
    }

    //404
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public ApiError handleNoSuchElement(final NoSuchElementException e) {
        return new ApiError(HttpStatus.NOT_FOUND, "Обьект не найден", e.getLocalizedMessage());
    }

    //500
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiError handleAll(final Exception ex, final WebRequest request) {
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(), "Произошла ошибка");
    }
}
