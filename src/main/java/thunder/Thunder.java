package thunder;

import sx.blah.discord.api.IDiscordClient;

public class Thunder {
    static Thunder bot;
    static IDiscordClient client;
    static Settings settings;

    public static void main(String[] args) {
        settings = new Settings();
        settings.init();

        if (args.length < 1)
            throw new IllegalArgumentException((String) settings.getMsg("args_not_found"));

        client = BotUtils.getBuiltDiscrodClient(args[0]);

        client.getDispatcher().registerListener(new CommandHandler());

        client.login();
    }

    private Thunder() {

    }
    public IDiscordClient getClient() {
        return this.client;
    }
}
