package zirionneft.thunder.handler;

import org.json.simple.parser.ParseException;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;
import zirionneft.thunder.BotUtils;
import zirionneft.thunder.Database;
import zirionneft.thunder.Thunder;
import zirionneft.thunder.command.Weather;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class Events {
    private static final int PRESENCE_PERIOD = 7; // in minutes

    private int presenceQueue = 0;
    static Logger logger = Logger.getLogger("Events.class");

    @EventSubscriber
    public void onThunderReady(ReadyEvent event) {
        Timer presenceTimer = new Timer();
        Timer weatherSendingTimer = new Timer();

        ArrayList<String> phrases = new ArrayList<String>();
        phrases.add("Mention me!");
        phrases.add(Thunder.getClientInstance().getGuilds().size() + " Guilds");
        phrases.add("https://thunder-bot.com");

        presenceTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Thunder.getClientInstance().changePresence(StatusType.ONLINE, ActivityType.PLAYING, phrases.get(presenceQueue));

                presenceQueue++;
                if (presenceQueue >= phrases.size())
                    presenceQueue = 0;
            }
        }, 500, PRESENCE_PERIOD*60*1000);

        weatherSendingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimeZone tz = TimeZone.getTimeZone("UTC");
                Calendar cal = Calendar.getInstance(tz);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                sdf.setTimeZone(tz);
                String currentTime = sdf.format(cal.getTime());

                ArrayList<IUser> usersList = Database.getBroadcastMatchList(currentTime);
                for (IUser user : usersList) {
                    try {
                        IPrivateChannel PMChannel = Thunder.getClientInstance().getOrCreatePMChannel(user);
                        ArrayList<String> row = Database.getWeatherRow(user);
                        EmbedObject embedObject = Weather.embedWeatherBuilder(row);
                        BotUtils.sendMessage(PMChannel, embedObject);
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                }

                Date date = new Date();
                ArrayList<IUser> toRemove = new ArrayList<>();
                Commands.getCommandStamps().forEach((user, stamp) -> {
                    if (date.getTime() >= stamp.getDate().getTime()) {
                        toRemove.add(user);
                        logger.info(user.getName() + " will be removed from StampList");
                    }
                });
                Commands.removeCommandStamp(toRemove);
            }
        }, 0, 60*1000);
    }

    @EventSubscriber
    public void onNewGuild(GuildCreateEvent event) {
        if(Database.insertGuildConfigRow(event.getGuild()))
            logger.info(event.getGuild().getName() + ": Guild row has been created in database!");

        List<IUser> checkList = event.getGuild().getUsers();
        checkList.removeIf(IUser::isBot);

        int rowsCount = Database.addUsersRows(checkList);
        if (rowsCount > 0)
            logger.info(event.getGuild().getName() + ": " + rowsCount + " new user rows has been added in database!");
    }

    @EventSubscriber
    public void onNewGuildMember(UserJoinEvent event) {
        if (!event.getUser().isBot()) {
            List<IUser> arrayList = new ArrayList<>();
            arrayList.add(event.getUser());
            int rowsCount = Database.addUsersRows(arrayList);
            if (rowsCount > 0)
                logger.info(event.getGuild().getName() + ": " + rowsCount + " new user rows has been added in database!");
        }
    }
}
