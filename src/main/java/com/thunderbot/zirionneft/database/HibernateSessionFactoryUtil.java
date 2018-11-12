package com.thunderbot.zirionneft.database;

import com.thunderbot.zirionneft.Thunder;
import com.thunderbot.zirionneft.database.entity.Guild;
import com.thunderbot.zirionneft.database.entity.User;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Timer;
import java.util.TimerTask;

public class HibernateSessionFactoryUtil {
    static Logger logger = Logger.getLogger("HibernateSessionFactoryUtil.class");

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
