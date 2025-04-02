package ru.yandex.practicum.filmorate.exception;

public class ConstantException {
    public static final String FILM_NOT_FOUND = "Фильм ID_%d не найден";
    public static final String USER_NOT_FOUND = "Пользователь ID_%d не найден";
    public static final String GENRE_NOT_FOUND = "Жанр ID_%d не найден";
    public static final String MPA_NOT_FOUND = "Рейтинг MPA ID_%d не найден";
    public static final String UNABLE_TO_ADD_YOURSELF =
            "Пользователь ID_%d не может добавить сам себя в друзья";
    public static final String UNABLE_TO_DELETE_YOURSELF =
            "Пользователь ID_%d не может удалить себя из друзей";
    public static final String UNABLE_FRIENDS_AMONG_THEMSELVES =
            "Пользователь ID_%d не может запросить общих друзей между собой";
}
