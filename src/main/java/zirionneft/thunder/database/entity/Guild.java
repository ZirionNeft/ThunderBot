package zirionneft.thunder.database.entity;

import zirionneft.thunder.Settings;
import zirionneft.thunder.Thunder;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "thunder_guilds")
public class Guild {
    @Id
    @Column(name = "guildId", nullable = false)
    private long guildId;

    @Column(name = "botPrefix")
    private String botPrefix = Thunder.getSettingsInstance().getOne("thunder_chat_prefix");

    @Column(name = "managerRoleId")
    private long managerRoleId;

    @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GuildManager> managers = new HashSet<>();

    public Guild() {}

    public Guild(long guildId) {
        this.guildId = guildId;
    }

    public void setBotPrefix(String botPrefix) {
        this.botPrefix = botPrefix;
    }

    public void setManagerRoleId(long managerRoleId) {
        this.managerRoleId = managerRoleId;
    }

    public long getGuildId() {
        return guildId;
    }

    public long getManagerRoleId() {
        return managerRoleId;
    }

    public String getBotPrefix() {
        return botPrefix;
    }
}
