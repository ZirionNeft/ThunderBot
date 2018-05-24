package thunder;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RequestBuffer;

public class BotUtils {
    static IDiscordClient getBuiltDiscrodClient(String token) {
        IDiscordClient client = null;
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.withToken(token);

        try {
             client = clientBuilder.build();
        } catch (DiscordException e) {
            System.out.println((String) Thunder.settings.getMsg("auth_fail"));
            e.printStackTrace();
        }
        return client;
    }

    static void sendMessage(IChannel channel, String message) {
        RequestBuffer.request(() -> {
           try {
               channel.sendMessage(message);
           } catch (DiscordException e) {
               e.printStackTrace();
           }
        });
    }
}
