package thunder;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import sx.blah.discord.api.IDiscordClient;
import thunder.handler.Commands;
import thunder.handler.Events;

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

        client = BotUtils.getBotDiscordClient(settings.getApiKey("discord_key"));

        client.getDispatcher().registerListener(new Commands());
        client.getDispatcher().registerListener(new Events());

        client.login();
    }

    public static IDiscordClient getClientInstance() {
        return client;
    }

    public static Settings getSettingsInstance() {
        return settings;
    }
}
