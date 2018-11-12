package com.thunderbot.zirionneft.command;

import com.thunderbot.zirionneft.handler.obj.IHelp;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import com.thunderbot.zirionneft.BotUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class Help {
    private static HashMap<String, IHelp> helpCommands = new HashMap<>();

    static {
        helpCommands.put("help", Help::help);
        helpCommands.put("weather", Weather::help);
        helpCommands.put("wr", Weather::help);
        helpCommands.put("tr", Translate::help);
        helpCommands.put("translate", Translate::help);
        helpCommands.put("stats", Info::help);
        helpCommands.put("set", Set::help);
        helpCommands.put("profile", Profile::help);
    }

    public static void run(MessageReceivedEvent event, List<String> args) {
        if (args.size() == 0)
            helpCommands.get("help").help(event);
        else if(helpCommands.containsKey(args.get(0)))
            helpCommands.get(args.get(0)).help(event);
    }

    public static void help(MessageReceivedEvent event) {
        BotUtils.sendMessage(event.getChannel(),
                ":zap: **Commands List** :zap:\n\n" +
                        "**Social** - `profile, stats, coins`\n" +
                        "**Utils** - `weather, translate`\n" +
                        "**Manage** - `set`\n" +
                        "**Other** - `about, support, help`"
        );
    }
}
