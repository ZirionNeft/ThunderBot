package thunder.handler.obj;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public interface Help {
    void help(MessageReceivedEvent event);
}