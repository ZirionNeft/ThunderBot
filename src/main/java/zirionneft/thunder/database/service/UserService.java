package zirionneft.thunder.database.service;

import zirionneft.thunder.database.dao.UserDao;
import zirionneft.thunder.database.entity.User;

import java.util.List;
import java.util.Optional;

public class UserService {
    private static UserDao userDao;

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
