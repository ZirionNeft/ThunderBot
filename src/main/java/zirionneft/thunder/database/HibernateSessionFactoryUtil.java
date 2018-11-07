package zirionneft.thunder.database;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.internal.SessionFactoryRegistry;
import zirionneft.thunder.database.entity.*;

public class HibernateSessionFactoryUtil {
    private static SessionFactory sessionFactory;

    public static SessionFactory configureSessionFactory() throws HibernateException {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();

                configuration.addAnnotatedClass(Guild.class);
                configuration.addAnnotatedClass(User.class);
                configuration.addAnnotatedClass(GuildManager.class);

                configuration.configure("hibernate.cfg.xml");

                sessionFactory = configuration.buildSessionFactory();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return sessionFactory;
    }
}
