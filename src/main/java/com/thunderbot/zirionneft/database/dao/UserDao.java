package com.thunderbot.zirionneft.database.dao;

import com.thunderbot.zirionneft.Thunder;
import com.thunderbot.zirionneft.database.entity.User;
import org.hibernate.Criteria;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class UserDao implements IUserDao {
    private EntityManager entityManager = Thunder.getHibernateSession();

    @Override
    public Optional<User> get(long userId) {
        return Optional.ofNullable(entityManager.find(User.class, userId));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<User> getAll() {
        return entityManager.createQuery("SELECT e FROM User e").getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<User> getMatchesBroadcastTime(String time) {
        return entityManager.createQuery("SELECT e FROM User e WHERE e.broadcastTime=:time").setParameter("time", time).getResultList();
    }

    @Override
    public void save(User user) {
        executeInsideTransaction(entityManager -> entityManager.persist(user));
    }

    @Override
    public void update(User user) {
        executeInsideTransaction(entityManager -> entityManager.merge(user));
    }

    @Override
    public void delete(User user) {
        executeInsideTransaction(entityManager -> entityManager.remove(user));
    }

    private void executeInsideTransaction(Consumer<EntityManager> action) {
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            action.accept(entityManager);
            tx.commit();
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }
}
