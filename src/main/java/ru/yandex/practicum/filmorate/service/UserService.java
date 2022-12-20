package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Integer userId, Integer friendId) {
        if (userId <= 0 || friendId <= 0) {
            log.debug("Check user id {} check friend id {}", userId, friendId);
            throw new UserIdException("User with id: " + userId + " or user friend with id: " + friendId + " not found");
        }
        userStorage.findById(userId).getFriendsId().add(friendId);
        userStorage.findById(friendId).getFriendsId().add(userId);

    }

    public void deleteFriend(Integer userId, Integer friendId) {
        if (userId <= 0 || friendId <= 0) {
            log.debug("Check user id {} check friend id {}", userId, friendId);
            throw new UserIdException("User with id: " + userId + " or user friend with id: " + friendId + " not found");
        }
        userStorage.findById(userId).getFriendsId().remove(friendId);
        userStorage.findById(friendId).getFriendsId().remove(userId);
    }

    public List<User> getAllFriends(Integer userId) {
        if (userId <= 0) {
            log.error("Invalid user ID: ", userId);
            throw new UserIdException("User id " + userId + " not found");
        }
        return userStorage.findById(userId).getFriendsId()
                .stream().map(userStorage::findById).collect(Collectors.toList());
    }

    public List<User> getMutualFriends(Integer userId, Integer otherId) {
        Set<Integer> setMutualFriends = new HashSet<>(userStorage.findById(otherId).getFriendsId());
        setMutualFriends.retainAll(userStorage.findById(userId).getFriendsId());
        return setMutualFriends
                .stream()
                .map(id -> userStorage.findById(id))
                .collect(Collectors.toList());
    }

    public List<User> get() {
        return userStorage.get();
    }

    public User findById(int userId) {
        return userStorage.findById(userId);
    }

    public User create(User user) {
        setNameIfLoginEmpty(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        setNameIfLoginEmpty(user);
        return userStorage.update(user);
    }

    private void setNameIfLoginEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
