package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.UserIdException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

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

    public List<User> get() {
        log.info("All users {}", usersMap.toString());
        List<User> users = new ArrayList<>(usersMap.values());
        return users;
    }

    public User create(User user) {
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        idGenerate();
        user.setId(id);
        user.setFriendsId(new HashSet<>());
        usersMap.put(id, user);
        log.info("Пользователь {} создан.", user.toString());
        return user;
    }

    public User update(User user) {
        if (usersMap.containsKey(user.getId())) {
            user.setFriendsId(new HashSet<>());
            usersMap.put(user.getId(), user);
            log.info("Пользователь {} обновлен.", user.toString());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return user;
    }

    @Override
    public User findById(Integer id) {
        Optional<User> user = Optional.ofNullable(usersMap.get(id));
        if (user.isPresent()) {
            log.info("User with id {} was found", id);
            return user.get();
        } else {
            log.error("Id not found {} ", id);
            throw new UserIdException("User with id: " + id + " not found");
        }
    }
}
