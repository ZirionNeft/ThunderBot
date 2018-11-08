package com.thunderbot.zirionneft.handler.obj;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public interface IHelp {
    void help(MessageReceivedEvent event);
}
