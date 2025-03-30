package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mapper.MpaRowMapper;

import java.util.List;
import java.util.NoSuchElementException;

@Component
@AllArgsConstructor
public class MpaStorage {
    private final JdbcTemplate jdbc;
    private final MpaRowMapper mapper;

    public List<Mpa> readAll() {
        String query = "SELECT * FROM MPA";
        return jdbc.query(query,mapper);
    }

    public Mpa read(int id) {
        String query = "SELECT * FROM MPA WHERE id = ?";
        return jdbc.queryForObject(query, mapper, id);
    }

    public boolean contains(int id) {
        try {
            read(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }
}
