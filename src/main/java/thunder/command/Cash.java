package thunder.command;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

public class Cash {
    public static void run(MessageReceivedEvent event, List<String> args) {
        IUser author = event.getAuthor();

        if (args.isEmpty()) {

        }
    }

    public static void help(MessageReceivedEvent event) {

    }
}
