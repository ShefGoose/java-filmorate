package ru.yandex.practicum.filmorate.exception;

public class NoSuchElement extends RuntimeException{
    public NoSuchElement(String message) {
        super(message);
    }
}
