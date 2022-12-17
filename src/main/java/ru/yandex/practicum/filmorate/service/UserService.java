package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Getter
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
        userStorage.getUserById(userId).getFriendsId().add(friendId);
        userStorage.getUserById(friendId).getFriendsId().add(userId);

    }

    public void deleteFriend(Integer userId, Integer friendId) {
        if (userId <= 0 || friendId <= 0) {
            log.debug("Check user id {} check friend id {}", userId, friendId);
            throw new UserIdException("User with id: " + userId + " or user friend with id: " + friendId + " not found");
        }
        userStorage.getUserById(userId).getFriendsId().remove(friendId);
        userStorage.getUserById(friendId).getFriendsId().remove(userId);
    }

    public List<User> getAllFriends(Integer userId) {
        if (userId <= 0) {
            log.error("Invalid user ID: ", userId);
            throw new UserIdException("User id " + userId + " not found");
        }
        List<User> friendsList = new ArrayList<>();
        for (Integer users : userStorage.getUserById(userId).getFriendsId()) {
            friendsList.add(userStorage.getUserById(users));
        }
        return friendsList;
    }

    public List<User> getMutualFriends(Integer userId, Integer otherId) {
        List<User> mutualFriends = new ArrayList<>();
        for (User user : getAllFriends(userId)) {
            for (User user1 : getAllFriends(otherId)) {
                if (user.equals(user1)) {
                    mutualFriends.add(user);
                }
            }
        }
        return mutualFriends;
    }
}
