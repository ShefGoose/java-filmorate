package ru.yandex.practicum.filmorate.exception;

public class ConstantException {
    public static final String FILM_NOT_FOUND = "Фильм ID_%d не найден";
    public static final String FILM_ALREADY_EXISTS = "Фильм ID_%d уже существует";
    public static final String LIKE_ALREADY_EXISTS = "Лайк фильму ID_%d от пользователя ID_%d уже существует";
    public static final String LIKE_NOT_FOUND = "Лайк фильму ID_%d от пользователя ID_%d не найден";
    public static final String USER_NOT_FOUND = "Пользователь ID_%d не найден";
    public static final String USER_ALREADY_EXISTS = "Пользователь ID_%d уже существует";
    public static final String FRIENDSHIP_ALREADY_EXIST = "Запрос на дружбу от пользователя ID_%d "
            + "к пользователю ID_%d уже существует";
    public static final String FRIENDSHIP_NOT_FOUND = "Запрос на дружбу от пользователя ID_%d "
            + "к пользователю ID_%d не найден";
    public static final String GENRE_NOT_FOUND = "Жанр ID_%d не найден";
    public static final String MPA_NOT_FOUND = "Рейтинг MPA ID_%d не найден";
    public static final String UNABLE_TO_ADD_YOURSELF =
            "Пользователь ID_%d не может добавить сам себя в друзья";
    public static final String UNABLE_TO_DELETE_YOURSELF =
            "Пользователь ID_%d не может удалить себя из друзей";
    public static final String UNABLE_FRIENDS_AMONG_THEMSELVES =
            "Пользователь ID_%d не может запросить общих друзей между собой";
}
