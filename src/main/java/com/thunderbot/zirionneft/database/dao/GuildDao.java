package com.thunderbot.zirionneft.database.dao;

import com.thunderbot.zirionneft.Thunder;
import com.thunderbot.zirionneft.database.entity.Guild;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class GuildDao implements IGuildDao {
    private EntityManager entityManager = Thunder.getHibernateSession();

    @Override
    public Optional<Guild> get(long guildId) {
        return Optional.ofNullable(entityManager.find(Guild.class, guildId));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Guild> getAll() {
        return entityManager.createQuery("SELECT e FROM Guild e").getResultList();
    }

    @Override
    public void save(Guild guild) {
        executeInsideTransaction(entityManager -> entityManager.persist(guild));
    }

    @Override
    public void update(Guild guild) {
        executeInsideTransaction(entityManager -> entityManager.merge(guild));
    }

    @Override
    public void delete(Guild guild) {
        executeInsideTransaction(entityManager -> entityManager.remove(guild));
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
