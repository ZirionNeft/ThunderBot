package com.thunderbot.zirionneft.handler;

import org.apache.log4j.Logger;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import com.thunderbot.zirionneft.database.entity.User;
import com.thunderbot.zirionneft.database.service.UserService;

public class Calculations {
    static Logger logger = Logger.getLogger("Calculations.class");

    @EventSubscriber
    public void experience(MessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return;

        User authorEntity = UserService.getUser(event.getAuthor().getLongID());
        String message = event.getMessage().getContent();

        Double oldExperience = authorEntity.getExp();
        authorEntity.setExp(oldExperience + message.split(" ").length*0.001);
        UserService.updateUser(authorEntity);
    }
}
