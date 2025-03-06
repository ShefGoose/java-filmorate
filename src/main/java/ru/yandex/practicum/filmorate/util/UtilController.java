package ru.yandex.practicum.filmorate.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.Set;

public class UtilController {
    public static void merge(Film film, Film updateFilm) {
        BeanUtils.copyProperties(film, updateFilm, getNullPropertyNames(film));
    }

    private static String[] getNullPropertyNames(Film film) {
        final BeanWrapper src = new BeanWrapperImpl(film);
        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : src.getPropertyDescriptors()) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public static void merge(User user, User updateUser) {
        BeanUtils.copyProperties(user, updateUser, getNullPropertyNames(user));
    }

    private static String[] getNullPropertyNames(User user) {
        final BeanWrapper src = new BeanWrapperImpl(user);
        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : src.getPropertyDescriptors()) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
