package com.thunderbot.zirionneft.handler.obj;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.List;

public interface ICommand {
    void run(MessageReceivedEvent event, List<String> args);
}