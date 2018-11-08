package com.thunderbot.zirionneft.handler;

import com.thunderbot.zirionneft.BotUtils;
import com.thunderbot.zirionneft.Thunder;
import com.thunderbot.zirionneft.command.Weather;
import com.thunderbot.zirionneft.database.entity.Guild;
import com.thunderbot.zirionneft.database.entity.User;
import com.thunderbot.zirionneft.database.service.GuildService;
import com.thunderbot.zirionneft.database.service.UserService;
import com.thunderbot.zirionneft.handler.obj.CommandStamp;
import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Events {
    private static final int PRESENCE_PERIOD = 7; // in minutes

    private int presenceQueue = 0;
    static Logger logger = Logger.getLogger("Events.class");

    @EventSubscriber
    public void onThunderReady(ReadyEvent event) {
        IDiscordClient clientInstance = Thunder.getClientInstance();

        Timer presenceTimer = new Timer();
        Timer everyMinuteTimer = new Timer();

        ArrayList<String> phrases = new ArrayList<>();
        phrases.add("Mention me!");
        phrases.add(Thunder.getClientInstance().getGuilds().size() + " Guilds");
        phrases.add("https://thunder-bot.com");

        presenceTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Thunder.getClientInstance().changePresence(
                        StatusType.ONLINE,
                        ActivityType.PLAYING,
                        phrases.get(presenceQueue)
                );

                presenceQueue++;
                if (presenceQueue >= phrases.size())
                    presenceQueue = 0;
            }
        }, 500, PRESENCE_PERIOD*60*1000);

        everyMinuteTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimeZone utcTimezone = TimeZone.getTimeZone("UTC");
                Calendar utcCalendar = Calendar.getInstance(utcTimezone);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

                sdf.setTimeZone(utcTimezone);
                String currentTime = sdf.format(utcCalendar.getTime());

                try {
                    List<User> matchedUserList = UserService.getMatchesBroadcastTime(currentTime);

                    for (User user : matchedUserList) {
                        try {
                            IUser discordUser = clientInstance.getUserByID(user.getUserId());
                            IPrivateChannel PMChannel = clientInstance.getOrCreatePMChannel(discordUser);
                            EmbedObject embedObject = Weather.embedWeatherBuilder(user.getCity(), user.getBroadcastTime());

                            BotUtils.sendMessage(PMChannel, embedObject);
                        } catch (IOException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (NullPointerException e) {
                    logger.warn("PM-Broadcast Timer: Matches List are empty.");
                }

                Date date = new Date();
                ArrayList<IUser> toRemove = new ArrayList<>();

                CommandStamp.getCommandStamps().forEach((user, stamp) -> {
                    if (date.getTime() >= stamp.getDate().getTime()) {
                        toRemove.add(user);
                        logger.info(user.getName() + " will be removed from StampList");
                    }
                });
                CommandStamp.massRemoveCommandStamps(toRemove);
            }
        }, 0, 60*1000);
    }
}
