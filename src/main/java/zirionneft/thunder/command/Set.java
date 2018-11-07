package zirionneft.thunder.command;

import org.apache.log4j.Logger;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import zirionneft.thunder.BotUtils;
import zirionneft.thunder.Database;
import zirionneft.thunder.database.entity.Guild;
import zirionneft.thunder.database.entity.GuildManager;
import zirionneft.thunder.database.service.GuildManagerService;
import zirionneft.thunder.database.service.GuildService;
import zirionneft.thunder.database.service.UserService;
import zirionneft.thunder.handler.Commands;
import zirionneft.thunder.handler.obj.CommandStamp;
import zirionneft.thunder.handler.obj.CommandState;

import java.util.*;

public class Set {
    static Logger logger = Logger.getLogger("Set.java");

    public static void run(MessageReceivedEvent event, List<String> args) {
        IUser author = event.getAuthor();
        IGuild guild = event.getGuild();
        EnumSet<Permissions> userPerms = author.getPermissionsForGuild(event.getGuild());

        if (!userPerms.contains(Permissions.MANAGE_SERVER) &&
            !userPerms.contains(Permissions.ADMINISTRATOR) &&
            !GuildManagerService.isGuildManager(author.getLongID(), guild.getLongID()) &&
            !checkRole(author, guild)
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
                        BotUtils.sendLocaleMessage(event.getChannel(), "general_settings_successful", "Prefix");
                    } else {
                        BotUtils.sendLocaleMessage(event.getChannel(), "manage_set_prefix_length_tip");
                    }
                } else {
                    BotUtils.sendLocaleMessage(event.getChannel(), "general_command_usage", "`set prefix 'prefix'`");
                }
            }

            else if (args.get(0).equals("role")) {
                if (args.size() == 2) {
                    List<IRole> roles = event.getMessage().getRoleMentions();

                    if (args.get(1).equals("0")) {
                        sendStatusMessage(event.getChannel(), Database.updateGuildManageRole(event.getGuild(), null), "Role remove");
                    } else if (!roles.isEmpty()) {
                        sendStatusMessage(event.getChannel(), Database.updateGuildManageRole(event.getGuild(), roles.get(0)), "Manager role");
                    } else if(!event.getGuild().getRolesByName(args.get(1)).isEmpty()) {
                        sendStatusMessage(event.getChannel(), Database.updateGuildManageRole(event.getGuild(), event.getGuild().getRolesByName(args.get(1)).get(0)), "Manager role");
                    } else {
                        BotUtils.sendLocaleMessage(event.getChannel(), "general_role_not_found_error");
                    }
                } else {
                    BotUtils.sendLocaleMessage(event.getChannel(), "general_command_usage", "`set role 'role'`");
                }
            }

            else if (args.get(0).equals("manager")) {
                if (args.size() >= 2) {
                    List<IUser> users = event.getMessage().getMentions();
                    if (users.size() >= 1) {
                        for (IUser u : users) {
                            if (!u.isBot()) {
                                GuildManager gm = GuildManagerService.getGuildManager(u.getLongID(), guild.getLongID());
                                if (gm == null) {
                                    GuildManagerService.saveGuildManager(
                                            new GuildManager(
                                                    GuildService.getGuild(guild.getLongID()),
                                                    UserService.getUser(u.getLongID())
                                            )
                                    );
                                    BotUtils.sendLocaleMessage(event.getChannel(), "general_settings_successful", "Manager set - " + u.getName());
                                } else {
                                    BotUtils.sendLocaleMessage(event.getChannel(), "manage_set_already_manager_error", u.getName());
                                }
                            }
                        }
                    } else {
                        BotUtils.sendLocaleMessage(event.getChannel(), "general_user_not_found_error", args.get(0));
                    }
                } else {
                    BotUtils.sendLocaleMessage(event.getChannel(), "general_command_usage", "`set manager 'user_mention' [user_mentions...]`");
                }
            }

            else if (args.get(0).equals("remove")) {
                List<IUser> users = event.getMessage().getMentions();
                List<IRole> roles = event.getMessage().getRoleMentions();

                if (args.size() >= 2 && (users.size() + roles.size() > 0)) {
                    String[] commandStampData = new String[2];
                    if (users.size() > 0)
                        commandStampData[0] = users.toString();
                    if (roles.size() > 0)
                        commandStampData[1] = roles.toString();

                    CommandStamp commandStamp = new CommandStamp(event, CommandState.ACCEPT_REMOVE, commandStampData);
                    commandStamp.generateCaptcha();
                    Commands.addCommandStamp(author, commandStamp);

                    BotUtils.sendLocaleMessage(event.getChannel(), "general_captcha", commandStamp.getFormattedCaptcha());
                } else {
                    BotUtils.sendLocaleMessage(event.getChannel(), "general_command_usage", "`set remove 'user_or_role_mention' [user_or_role_mentions...]`");
                }
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

    public static List<IUser> removeManagerUsers(MessageReceivedEvent event, List<IUser> users) {
        List<IUser> notRemovedUsers = new ArrayList<>();
        Long guildId = event.getGuild().getLongID();

        for(IUser u : users) {
            if (!u.isBot()) {
                GuildManager guildManager = GuildManagerService.getGuildManager(guildId, u.getLongID());
                if (guildManager != null) {
                    GuildManagerService.deleteGuildManager(guildManager);
                } else
                    notRemovedUsers.add(u);
            }
        }

        return notRemovedUsers;
    }

    public static void remove(MessageReceivedEvent event) {
        sendStatusMessage(event.getChannel(), Database.removeGuildManager(event.getGuild(), event.getAuthor()), "Remove user manager");
    }

    private static void sendStatusMessage(IChannel channel, boolean status, String type) {
        if(status) {
            BotUtils.sendLocaleMessage(channel, "general_settings_successful", type);
        } else {
            BotUtils.sendLocaleMessage(channel, "general_unknown_error");
        }
    }

    private static boolean checkRole(IUser user, IGuild guild) {
        if (Database.getGuildManageRole(guild) == null)
            return false;
        return user.hasRole(Database.getGuildManageRole(guild));
    }
}
