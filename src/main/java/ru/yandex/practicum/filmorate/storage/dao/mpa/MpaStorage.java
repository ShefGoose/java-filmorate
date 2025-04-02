package ru.yandex.practicum.filmorate.storage.dao.mpa;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mapper.MpaRowMapper;

import java.util.List;
import java.util.NoSuchElementException;

import static ru.yandex.practicum.filmorate.exception.ConstantException.MPA_NOT_FOUND;

@Component
@Slf4j
@AllArgsConstructor
public class MpaStorage {
    private final JdbcTemplate jdbc;
    private final MpaRowMapper mapper;

    public List<Mpa> readAll() {
        String query = "SELECT * FROM MPA";
        List<Mpa> allMpa = jdbc.query(query,mapper);

        log.info("Возвращены все рейтинги MPA: {}", allMpa);

        return allMpa;
    }

    public Mpa read(int id) {
        String query = "SELECT * FROM MPA WHERE id = ?";
        List<Mpa> allMpa = jdbc.query(query, mapper, id);
        if (allMpa.isEmpty()) {
            log.warn("Рейтинг MPA ID_{} не найден.", id);
            throw new NoSuchElementException(String.format(MPA_NOT_FOUND, id));
        } else {
            log.info("Возвращён рейтинг MPA: {}", allMpa.getFirst());
            return allMpa.getFirst();
        }
    }
}
