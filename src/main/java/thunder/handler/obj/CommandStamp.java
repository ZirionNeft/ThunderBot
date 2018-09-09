package thunder.handler.obj;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class CommandStamp {
    private MessageReceivedEvent event;
    private Class<?> className;
    private CommandState state;
    private String data = null;

    CommandStamp(MessageReceivedEvent event, Class<?> className, CommandState state, String data) {
        this.event = event;
        this.className = className;
        this.state = state;
        this.data = data;
    }

    CommandStamp(MessageReceivedEvent event, Class<?> className, CommandState state) {
        this.event = event;
        this.className = className;
        this.state = state;
    }

    public CommandState getState() {
        return this.state;
    }

    public void setState(CommandState state) {
        this.state = state;
    }

    public Class<?> getClassName() {
        return this.className;
    }
}
