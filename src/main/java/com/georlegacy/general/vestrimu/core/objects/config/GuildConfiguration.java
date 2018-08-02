package com.georlegacy.general.vestrimu.core.objects.config;

import com.georlegacy.general.vestrimu.core.objects.behaviour.GuildBehaviourRecord;
import lombok.Getter;

public class GuildConfiguration {

    @Getter
    private String id;

    @Getter
    private String botaccessroleid;

    @Getter
    private String primarywebhookid;

    @Getter
    private String prefix;

    @Getter
    private boolean requireaccessforhelp;

    @Getter
    private boolean admin_mode;

    @Getter
    private String guild_behaviour_record;

    public GuildConfiguration setBotaccessroleid(String botaccessroleid) {
        this.botaccessroleid = botaccessroleid;
        return this;
    }

    public GuildConfiguration setPrimarywebhookid(String primarywebhookid) {
        this.primarywebhookid = primarywebhookid;
        return this;
    }

    public GuildConfiguration setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public GuildConfiguration setRequireaccessforhelp(boolean requireaccessforhelp) {
        this.requireaccessforhelp = requireaccessforhelp;
        return this;
    }

    public GuildConfiguration setAdmin_mode(boolean admin_mode) {
        this.admin_mode = admin_mode;
        return this;
    }

    public GuildConfiguration setGuild_behaviour_record(String guild_behaviour_record) {
        this.guild_behaviour_record = guild_behaviour_record;
        return this;
    }

    public GuildConfiguration(String id, String botaccessroleid, String primarywebhookid, String prefix, boolean admin_mode, boolean requireaccessforhelp, GuildBehaviourRecord guildRecord) {
        this.id = id;
        this.botaccessroleid = botaccessroleid;
        this.primarywebhookid = primarywebhookid;
        this.prefix = prefix;
        this.requireaccessforhelp = requireaccessforhelp;
        this.admin_mode = admin_mode;
        this.guild_behaviour_record = guildRecord.serialize().toString();
        this.behaviourRecord = guildRecord;
    }

    @Getter private transient GuildBehaviourRecord behaviourRecord;

}
