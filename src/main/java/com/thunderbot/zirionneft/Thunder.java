package com.thunderbot.zirionneft;

import com.thunderbot.zirionneft.database.HibernateSessionFactoryUtil;
import com.thunderbot.zirionneft.handler.Calculations;
import com.thunderbot.zirionneft.handler.Commands;
import com.thunderbot.zirionneft.handler.Events;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import sx.blah.discord.api.IDiscordClient;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

public class Thunder {
    static Logger logger = Logger.getLogger("Thunder.class");

    private static IDiscordClient client;
    private static Settings settings;

    private static SessionFactory sessionFactory;

    public static void main(String[] args) {
        BasicConfigurator.configure();

        try {
            System.setProperty("java.library.path", System.getProperty("user.dir") + "\\target\\classes\\libs");
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            logger.error("Failed to get permissions to set library path");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            logger.error("Failed to get field handle to set library path");
        }
        System.loadLibrary("webp-imageio");

        sessionFactory = HibernateSessionFactoryUtil.configureSessionFactory();

        settings = new Settings();
        settings.init();

        logger.info("Weather API powered by " + settings.getOne("thunder_weather_service"));

        if (args.length == 0) {
            client = BotUtils.getBotDiscordClient(settings.getKey("discord_key"));
        } else if (args[0].equals("test")){
            client = BotUtils.getBotDiscordClient(settings.getKey("discord_test_key"));
        } else {
            logger.error("Discord API keys in 'keys.properties' not provided!");
            System.exit(0);
        }

        client.getDispatcher().registerListener(new Commands());
        client.getDispatcher().registerListener(new Events());
        client.getDispatcher().registerListener(new Calculations());

        client.login();
    }

    public static IDiscordClient getClientInstance() {
        return client;
    }

    public static Settings getSettingsInstance() {
        return settings;
    }

    public static String getVersion() {
        try {
            Model model;
            MavenXpp3Reader reader = new MavenXpp3Reader();
            if ((new File("pom.xml")).exists())
                model = reader.read(new FileReader("pom.xml"));
            else
                model = reader.read(
                        new InputStreamReader(
                                Thunder.class.getResourceAsStream(
                                        "/META-INF/maven/Thunder/zirionneft.thunder/pom.xml"
                                )
                        )
                );
            return model.getVersion();
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    public static Session getHibernateSession() throws HibernateException {
        return sessionFactory.openSession();
    }
}
