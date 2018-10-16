package zirionneft.thunder.command;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import zirionneft.thunder.BotUtils;
import zirionneft.thunder.Thunder;

import java.util.List;

public class Support {
    public static void run(MessageReceivedEvent event, List<String> args) {
        BotUtils.sendMessage(event.getChannel(), "https://discord.gg/"+ Thunder.getSettingsInstance().getKey("support_server_key"));
    }
}
