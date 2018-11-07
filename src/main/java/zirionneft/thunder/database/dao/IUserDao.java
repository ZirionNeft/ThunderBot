package zirionneft.thunder.database.dao;

import zirionneft.thunder.database.entity.User;

import java.util.List;
import java.util.Optional;

public interface IUserDao {

    Optional<User> get(long userId);

    List<User> getAll();

    void save(User user);

    void update(User user);

    void delete(User user);
}
