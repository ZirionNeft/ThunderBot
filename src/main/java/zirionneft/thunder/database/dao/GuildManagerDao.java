package zirionneft.thunder.database.dao;

import zirionneft.thunder.database.entity.Guild;
import zirionneft.thunder.database.entity.GuildManager;
import zirionneft.thunder.database.entity.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.List;
import java.util.function.Consumer;

public class GuildManagerDao implements IGuildManagerDao {
    private EntityManager entityManager;

    @Override
    public GuildManager get(int id) {
        return entityManager.find(GuildManager.class, id);
    }

    @Override
    public GuildManager getByInfo(long userId, long guildId) {
        Query q = entityManager.createQuery("SELECT e FROM GuildManager e WHERE e.guildId=:guild AND e.userId=:user")
                .setParameter("guild", guildId)
                .setParameter("user", userId);
        return (GuildManager) q.getSingleResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<GuildManager> getAll() {
        return entityManager.createQuery("SELECT e FROM GuildManager e").getResultList();
    }

    @Override
    public void save(GuildManager guildManager) {
        executeInsideTransaction(entityManager -> entityManager.persist(guildManager));
    }

    @Override
    public void update(GuildManager guildManager) {
        executeInsideTransaction(entityManager -> entityManager.merge(guildManager));
    }

    @Override
    public void delete(GuildManager guildManager) {
        executeInsideTransaction(entityManager -> entityManager.remove(guildManager));
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
