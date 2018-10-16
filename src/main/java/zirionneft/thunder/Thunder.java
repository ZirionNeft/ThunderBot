package zirionneft.thunder;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import sx.blah.discord.api.IDiscordClient;
import zirionneft.thunder.handler.Calculations;
import zirionneft.thunder.handler.Commands;
import zirionneft.thunder.handler.Events;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Thunder {
    static Logger logger = Logger.getLogger("Thunder.class");

    private static IDiscordClient client;
    private static Settings settings;

    public static void main(String[] args) {
        BasicConfigurator.configure();
        settings = new Settings();
        settings.init();

        Database.init();

        logger.info("Weather API powered by " + settings.getOne("thunder_weather_service"));

        if (settings.getKey("discord_test_key").isEmpty()) {
            client = BotUtils.getBotDiscordClient(settings.getKey("discord_key"));
        } else {
            client = BotUtils.getBotDiscordClient(settings.getKey("discord_test_key"));
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
}
