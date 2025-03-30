package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.mpa.MpaStorage;

import java.util.List;
import java.util.NoSuchElementException;

import static ru.yandex.practicum.filmorate.exception.ConstantException.MPA_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;

    public List<Mpa> readAll() {
        return mpaStorage.readAll();
    }

    public Mpa read(int id) {
        if (!mpaStorage.contains(id)) {
            log.warn(String.format(MPA_NOT_FOUND, id));
            throw new NoSuchElementException(String.format(MPA_NOT_FOUND, id));
        }
        return mpaStorage.read(id);
    }
}
