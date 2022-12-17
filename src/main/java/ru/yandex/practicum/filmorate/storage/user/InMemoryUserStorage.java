package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.UserIdException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Integer, User> usersMap = new HashMap<>();
    private Integer id = 0;

    public void idGenerate() {
        if (usersMap.size() != id) {
            id = usersMap.size();
        }
        id++;
    }

    public Collection<User> users() {
        log.info("All users {}", usersMap.toString());
        return usersMap.values();
    }

    public User newUser(User user) {
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        idGenerate();
        user.setId(id);
        validate(user);
        user.setFriendsId(new HashSet<>());
        usersMap.put(id, user);
        log.info("Пользователь {} создан.", user.toString());
        return user;
    }

    public User updateUser(User user) {
        validate(user);
        for (User users : usersMap.values()) {
            if (user.getId() == users.getId()) {
                usersMap.remove(users);
                user.setFriendsId(new HashSet<>());
                usersMap.put(user.getId(), user);
                log.info("Пользователь {} обновлен.", user.toString());
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        }
        return user;
    }

    @Override
    public User getUserById(Integer id) {
        if (usersMap.containsKey(id)) {
            log.info("User with id {} was found", id);
            return usersMap.get(id);
        }
        log.error("Id not found{}", id);
        throw new UserIdException("User with id: " + id + " not found");
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
