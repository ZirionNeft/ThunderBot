package com.thunderbot.zirionneft.command;

import com.thunderbot.zirionneft.BotUtils;
import com.thunderbot.zirionneft.Thunder;
import com.thunderbot.zirionneft.database.entity.Guild;
import com.thunderbot.zirionneft.database.service.GuildService;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.List;

public class About {
    public static void run(MessageReceivedEvent event, List<String> args) {
        EmbedBuilder builder = new EmbedBuilder();

        Guild guild = GuildService.getGuild(event.getGuild().getLongID());
        String prefix = guild.getBotPrefix();
        GuildService.saveGuild(guild);

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
