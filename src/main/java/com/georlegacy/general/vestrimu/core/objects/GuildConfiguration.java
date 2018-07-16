package com.georlegacy.general.vestrimu.core.objects;

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

    public GuildConfiguration(String id, String botaccessroleid, String primarywebhookid, String prefix, boolean admin_mode, boolean requireaccessforhelp) {
        this.id = id;
        this.botaccessroleid = botaccessroleid;
        this.primarywebhookid = primarywebhookid;
        this.prefix = prefix;
        this.requireaccessforhelp = requireaccessforhelp;
        this.admin_mode = admin_mode;
    }

}
