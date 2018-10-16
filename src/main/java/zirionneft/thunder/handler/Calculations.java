package zirionneft.thunder.handler;

import org.apache.log4j.Logger;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import zirionneft.thunder.BotUtils;
import zirionneft.thunder.Database;

public class Calculations {
    static Logger logger = Logger.getLogger("Calculations.class");

    @EventSubscriber
    public void experience(MessageReceivedEvent event) {
        String message = event.getMessage().getContent();

        String exp = BotUtils.rounding(message.split(" ").length*0.001, 2).replace(",",".");
        Database.updateUserExp(event.getAuthor(), exp);
    }
}
