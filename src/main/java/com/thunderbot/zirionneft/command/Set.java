package com.thunderbot.zirionneft.command;

import org.apache.log4j.Logger;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import com.thunderbot.zirionneft.BotUtils;
import com.thunderbot.zirionneft.Thunder;
import com.thunderbot.zirionneft.database.entity.Guild;
import com.thunderbot.zirionneft.database.service.GuildService;
import com.thunderbot.zirionneft.handler.obj.CommandStamp;
import com.thunderbot.zirionneft.handler.obj.CommandState;

import java.util.*;

public class Set {
    static Logger logger = Logger.getLogger("Set.java");

    public static void run(MessageReceivedEvent event, List<String> args) {
        IUser author = event.getAuthor();
        IGuild guild = event.getGuild();
        EnumSet<Permissions> authorGuildPermissions = author.getPermissionsForGuild(event.getGuild());

        if (!authorGuildPermissions.contains(Permissions.MANAGE_SERVER) &&
            !authorGuildPermissions.contains(Permissions.ADMINISTRATOR) &&
            !isHaveManagerRole(author, guild)
        ) {
            BotUtils.sendLocaleMessage(event.getChannel(), "general_user_permissions_error");
            return;
        }

        try {
            if (args.isEmpty() || args.get(0).equals("help")) {
                help(event);
            }

            else if (args.get(0).equals("prefix")) {
                if (args.size() == 2) {
                    if (args.get(1).length() > 0 && args.get(1).length() < 5) {
                        Guild guildEntity = GuildService.getGuild(guild.getLongID());
                        guildEntity.setBotPrefix(args.get(1));
                        GuildService.updateGuild(guildEntity);

                        BotUtils.sendLocaleMessage(event.getChannel(), "general_settings_successful", "Prefix");
                    } else
                        BotUtils.sendLocaleMessage(event.getChannel(), "manage_set_prefix_length_tip");
                } else
                    BotUtils.sendLocaleMessage(event.getChannel(), "general_command_usage", "`set prefix 'prefix'`");
            }

            else if (args.get(0).equals("role")) {
                if (!authorGuildPermissions.contains(Permissions.MANAGE_SERVER) && !authorGuildPermissions.contains(Permissions.ADMINISTRATOR)) {
                    BotUtils.sendLocaleMessage(event.getChannel(), "general_user_permissions_error");
                    return;
                }

                if (args.size() == 2) {
                    if (args.get(1).equals("remove")) {
                        CommandStamp commandStamp = new CommandStamp(event, CommandState.ACCEPT_ROLE_REMOVE);
                        commandStamp.generateCaptcha();
                        CommandStamp.addCommandStamp(author, commandStamp);

                        BotUtils.sendLocaleMessage(event.getChannel(), "general_captcha", commandStamp.getFormattedCaptcha());
                    } else {
                        List<IRole> roles = event.getMessage().getRoleMentions();

                        if (roles.size() == 1) {
                            Guild guildEntity = GuildService.getGuild(guild.getLongID());
                            guildEntity.setManagerRoleId(roles.get(0).getLongID());
                            GuildService.updateGuild(guildEntity);

                            BotUtils.sendLocaleMessage(event.getChannel(), "general_settings_successful", "Change Role Manager");
                        } else
                            BotUtils.sendLocaleMessage(event.getChannel(), "general_role_not_found_error", args.get(0));
                    }
                } else
                    BotUtils.sendLocaleMessage(event.getChannel(), "general_command_usage", "`set role 'role_mention'|remove`");
            }

            else {
                help(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void help(MessageReceivedEvent event) {
        BotUtils.sendLocaleMessage(event.getChannel(), "manage_set_help_message");
    }

    public static void removeManagerRole(MessageReceivedEvent event) {
        Guild guildEntity = GuildService.getGuild(event.getGuild().getLongID());
        guildEntity.setManagerRoleId(null);
        GuildService.updateGuild(guildEntity);
    }

    private static boolean isHaveManagerRole(IUser user, IGuild guild) {
        Guild guildEntity = GuildService.getGuild(guild.getLongID());
        if (guildEntity.getManagerRoleId() == null)
            return false;
        return user.hasRole(Thunder.getClientInstance().getRoleByID(guildEntity.getManagerRoleId()));
    }
}
