package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.FilmIdException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
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
        validate(film);
        film.setId(id);
        film.setLikeId(new HashSet<>());
        filmMap.put(id, film);
        log.info("Film create {}", film.toString());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        validate(film);
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
    public Film getFilmById(Integer id) {
        if (!filmMap.containsKey(id)) {
            log.error("Film with id: {} not found", id);
            throw new FilmIdException("Film with id: " + id + " not found");
        }
        return filmMap.get(id);
    }

    private void validate(Film film) {
        if (film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}
