package com.thunderbot.zirionneft.handler;

import com.thunderbot.zirionneft.Thunder;
import com.thunderbot.zirionneft.database.HibernateSessionFactoryUtil;
import org.apache.log4j.Logger;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import com.thunderbot.zirionneft.database.entity.User;
import com.thunderbot.zirionneft.database.service.UserService;

import java.util.Timer;
import java.util.TimerTask;

public class Calculations {
    static Logger logger = Logger.getLogger("Calculations.class");

    @EventSubscriber
    public void updateExperience(MessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return;

        User authorEntity = UserService.getUser(event.getAuthor().getLongID());
        String message = event.getMessage().getContent();

        Long oldExperience = authorEntity.getExp();
        Long newExperience = oldExperience + message.length();
        Long nextLevelExperience = nextLevel(authorEntity.getLevel());

        while (newExperience >= nextLevelExperience) {
            authorEntity.setLevel(authorEntity.getLevel() + 1);

            newExperience -= nextLevelExperience;
            nextLevelExperience = nextLevel(authorEntity.getLevel());

            /*BotUtils.sendLocaleMessage(
                    event.getChannel(),
                    "social_profile_level_up",
                    event.getAuthor().getName(),
                    authorEntity.getLevel()
            );*/
            if (newExperience < nextLevelExperience) {
                authorEntity.setExp(newExperience);
                UserService.updateUser(authorEntity);
            }
        }

        authorEntity.setExp(newExperience);
    }

    public static Long nextLevel(int currentLevel) {
        return 200 + Math.round(Math.pow(1.5, currentLevel)*100);
    }
}
