package thunder;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import sx.blah.discord.api.IDiscordClient;

public class Thunder {
    static Logger logger = Logger.getLogger("Thunder.class");

    static IDiscordClient client;
    static Settings settings;

    public static void main(String[] args) {
        BasicConfigurator.configure();

        settings = new Settings();
        settings.init();

        Database.init();

        logger.info("Weather API powered by " + settings.getOne("thunder_weather_service"));

        client = BotUtils.getBotDiscordClient(settings.getApiKey("discord_key"));

        client.getDispatcher().registerListener(new CommandsHandler());
        client.getDispatcher().registerListener(new EventsHandler());

        client.login();
    }
}
