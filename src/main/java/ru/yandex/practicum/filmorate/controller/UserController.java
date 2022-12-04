package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private Set<User> userSet = new HashSet<>();
    private int id = 0;

    public int idGenerate() {
        return ++id;
    }

    @GetMapping
    public Collection<User> users() {
        return userSet;
    }

    @PostMapping
    public User newUser(@RequestBody User user) {
        validate(user);
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(idGenerate());
        userSet.add(user);
        log.info("Пользователь {} создан.", user.toString());
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        validate(user);
        for (User users : userSet) {
            if (user.getId() == users.getId()) {
                userSet.remove(users);
                userSet.add(user);
                log.info("Пользователь {} обновлен.", user.toString());
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        }
        return user;
    }

    public void validate(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.info("Отсутствует электронная почта {}", user.toString());
            throw new ValidationException("Электронная почта не может быть пустой или должна содержать символ @");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.info("Отсутствует логин {}", user.toString());
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Введена не корректная дата рождения {}", user.toString());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}