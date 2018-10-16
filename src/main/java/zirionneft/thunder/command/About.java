package zirionneft.thunder.command;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;
import zirionneft.thunder.*;

import java.util.HashMap;
import java.util.List;

public class About {
    public static void run(MessageReceivedEvent event, List<String> args) {
        EmbedBuilder builder = new EmbedBuilder();

        String prefix;
        HashMap<Long, String> prefixes = Database.getGuildPrefixes();
        if (prefixes.containsKey(event.getGuild().getLongID()))
            prefix = prefixes.get(event.getGuild().getLongID());
        else
            prefix = Thunder.getSettingsInstance().getOne("thunder_chat_prefix");

        builder.withThumbnail(Thunder.getClientInstance().getOurUser().getAvatarURL());
        builder.withAuthorName("Thunder");
        builder.withColor(255, 255, 255);

        builder.withDescription("Open source bot with some fun and useful features!\n\n" +
                "Use mention `@Thunder help` or `help` with prefix to get info about commands\n\n" +
                "**" + event.getGuild().getName() + "**'s commands prefix is `" + prefix + "`");

        builder.appendField("Bot version", Thunder.getVersion(), true);
        builder.appendField("Owner", Thunder.getClientInstance().getApplicationOwner().getName(), true);

        builder.withImage("https://github.com/ZirionNeft/ThunderBot/blob/master/images/logo-github-banner.png?raw=true");

        BotUtils.sendMessage(event.getChannel(), builder.build());
    }
}
