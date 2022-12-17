package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.FilmIdException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> filmMap = new HashMap<>();
    private int id = 0;

    public void idGenerate() {
        if (filmMap.size() != id) {
            id = filmMap.size();
        }
        id++;
    }

    @Override
    public Collection<Film> getFilms() {
        log.info("All films {}", filmMap.toString());
        return filmMap.values();
    }

    @Override
    public Film create(Film film) {
        idGenerate();
        film.setId(id);
        film.setLikeId(new HashSet<>());
        filmMap.put(id, film);
        log.info("Film create {}", film.toString());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (filmMap.containsKey(film.getId())) {
            filmMap.remove(film.getId());
            film.setLikeId(new HashSet<>());
            filmMap.put(film.getId(), film);
            log.info("Film was update {}", film.toString());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return film;
    }

    @Override
    public Film findById(Integer id) {
        if (!filmMap.containsKey(id)) {
            log.error("Film with id: {} not found", id);
            throw new FilmIdException("Film with id: " + id + " not found");
        }
        return filmMap.get(id);
    }
}
