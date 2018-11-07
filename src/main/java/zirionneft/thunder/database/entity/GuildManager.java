package zirionneft.thunder.database.entity;

import org.hibernate.annotations.GeneratorType;
import zirionneft.thunder.handler.obj.UserPermissionsLevel;

import javax.persistence.*;

@Entity
@Table(name = "thunder_guild_managers")
public class GuildManager {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "guild_id", nullable = false)
    private long guildId;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "permissions_level")
    private UserPermissionsLevel permissionsLevel = UserPermissionsLevel.MODERATOR;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guild", nullable = false)
    private Guild guild;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user", nullable = false)
    private User user;

    public GuildManager(){}

    public GuildManager(long guildId, long userId) {
        this.guildId = guildId;
        this.userId = userId;
    }

    public GuildManager(Guild guild, User user) {
        this.guild = guild;
        this.user = user;
        this.guildId = guild.getGuildId();
        this.userId = user.getUserId();
    }

    public Guild getGuild() {
        return guild;
    }

    public User getUser() {
        return user;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPermissionsLevel(UserPermissionsLevel permissionsLevel) {
        this.permissionsLevel = permissionsLevel;
    }

    public long getUserId() {
        return userId;
    }

    public long getGuildId() {
        return guildId;
    }

    public int getId() {
        return id;
    }

    public UserPermissionsLevel getPermissionsLevel() {
        return permissionsLevel;
    }
}
