package thunder;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.DiscordException;

import java.util.*;

public class CommandHandler {
    static String PREFIX = (String)Thunder.settings.getOne("thunder_chat_prefix");

    @EventSubscriber
    public void onMessageReceived(MessageReceivedEvent event) {
        try {
            String command[] = event.getMessage().getContent().split(" ");

            if (command.length == 0)
                return;

            if (!command[0].startsWith(PREFIX))
                return;

            //Command String
            String cmd = command[0].substring(1);

            // Arguments Array
            List<String> argsList = new ArrayList<>(Arrays.asList(command));
            argsList.remove(0);

            switch (cmd) {
                case "help":
                    helpCommand(event);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void helpCommand(MessageReceivedEvent event) {
        BotUtils.sendMessage(event.getChannel(), "```Some information```");
    }
}
