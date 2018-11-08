package com.thunderbot.zirionneft.database;

import com.thunderbot.zirionneft.database.entity.Guild;
import com.thunderbot.zirionneft.database.entity.User;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateSessionFactoryUtil {
    private static SessionFactory sessionFactory;

    public static SessionFactory configureSessionFactory() throws HibernateException {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();

                configuration.addAnnotatedClass(Guild.class);
                configuration.addAnnotatedClass(User.class);

                configuration.configure("hibernate.cfg.xml");

                sessionFactory = configuration.buildSessionFactory();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return sessionFactory;
    }
}
