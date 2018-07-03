package thunder;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.*;


import java.awt.*;
import java.util.List;

public class EventsHandler {
    private static IDiscordClient client = Thunder.client;

    @EventSubscriber
    public void onThunderReady(ReadyEvent event) {
        client.changePresence(StatusType.ONLINE, ActivityType.PLAYING, "Use " + Thunder.settings.getOne("thunder_chat_prefix") + "help");

        List<IGuild> guilds = client.getGuilds();
        /*guilds.forEach(guild -> {
            List<IRole> role = guild.getRolesByName("Thunder");
            if (role.isEmpty()) {
                IRole tRole = guild.createRole();
                tRole.changeHoist(false);
                tRole.changeName("Thunder");
                tRole.changeColor(new Color(50,150,200));

                guild.getUserByID(client.getOurUser().getLongID()).addRole(tRole);
            }
        });*/

    }
}
