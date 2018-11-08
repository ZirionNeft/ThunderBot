package com.thunderbot.zirionneft.database.service;

import com.thunderbot.zirionneft.database.dao.UserDao;
import com.thunderbot.zirionneft.database.entity.User;

import java.util.List;
import java.util.Optional;

public class UserService {
    private static UserDao userDao = new UserDao();

    public static User getUser(long userId) {
        Optional<User> user = userDao.get(userId);
        return user.orElseGet(() -> {
            User u = new User(userId);
            saveUser(u);
            return u;
        });
    }

    public static List<User> getAllUsers() {
        return userDao.getAll();
    }

    public static List<User> getMatchesBroadcastTime(String time) {
        return userDao.getMatchesBroadcastTime(time);
    }

    public static void updateUser(User user) {
        userDao.update(user);
    }

    public static void saveUser(User user) {
        userDao.save(user);
    }

    public static void deleteUser(User user) {
        userDao.delete(user);
    }
}
