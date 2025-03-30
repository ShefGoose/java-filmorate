package ru.yandex.practicum.filmorate.storage.genre;


import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;


import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Repository
@AllArgsConstructor
public class GenreStorage {
    private final JdbcTemplate jdbc;
    private final GenreRowMapper mapper;

    public List<Genre> readAll() {
        String query = "SELECT * FROM genres";
        return jdbc.query(query,mapper);
    }

    public Genre read(int id) {
        String query = "SELECT * FROM genres WHERE id = ?";
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
