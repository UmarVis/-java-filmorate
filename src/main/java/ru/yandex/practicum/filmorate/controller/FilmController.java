package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConstraintViolationException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@RestController
@Validated
@RequestMapping("/films")
public class FilmController {

    private static final LocalDate RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> get() {
        return filmService.get();
    }

    @GetMapping("/{filmId}")
    public Film findById(@PathVariable int filmId) {
        return filmService.findById(filmId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(value = "count", defaultValue = "10")
                                       @Positive int count) {
        return filmService.getPopularFilms(count);
    }

    @PostMapping
    public Film create(@RequestBody @Valid Film film) {
        validate(film);
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film) {
        validate(film);
        return filmService.update(film);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        filmService.removeLike(id, userId);
    }

    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}