package thunder;

import sx.blah.discord.api.IDiscordClient;

public class Thunder {
    static IDiscordClient client;
    static Settings settings;
    static Database db;

    public static void main(String[] args) {
        settings = new Settings();
        settings.init();

        db = new Database();
        db.init();


        System.out.println("[INFO] Weather API powered by " + settings.getOne("thunder_weather_service"));

        client = BotUtils.getBuiltDiscrodClient(BotUtils.getToken("discord"));

        client.getDispatcher().registerListener(new CommandHandler());
        client.getDispatcher().registerListener(new EventsHandler());

        client.login();

    }
}
