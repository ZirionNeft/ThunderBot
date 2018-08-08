package thunder.command;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import thunder.BotUtils;

import java.util.List;

public class Help {
    public static void run(MessageReceivedEvent event, List<String> args) {
        String message =
        ":zap: **Commands List** :zap:\n"
        + "```"
        + ">weather help\n"
        + ">translate help\n"
        + ">settings\n"
        + "```";

        BotUtils.sendMessage(event.getChannel(), message);
    }
}
