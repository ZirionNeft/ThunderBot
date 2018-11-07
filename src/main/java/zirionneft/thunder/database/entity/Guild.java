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
    private Long guildId;

    @Column(name = "botPrefix")
    private String botPrefix = Thunder.getSettingsInstance().getOne("thunder_chat_prefix");

    @Column(name = "managerRoleId")
    private Long managerRoleId = null;

    public Guild() {}

    public Guild(long guildId) {
        this.guildId = guildId;
    }

    public void setBotPrefix(String botPrefix) {
        this.botPrefix = botPrefix;
    }

    public void setManagerRoleId(Long managerRoleId) {
        this.managerRoleId = managerRoleId;
    }

    public Long getGuildId() {
        return guildId;
    }

    public Long getManagerRoleId() {
        return managerRoleId;
    }

    public String getBotPrefix() {
        return botPrefix;
    }
}
