package com.georlegacy.general.vestrimu.core.objects;

import lombok.Getter;
import lombok.Setter;

public class GuildConfiguration {

    @Getter @Setter
    private String id;

    @Getter @Setter
    private String botaccessroleid;

    @Getter @Setter
    private String prefix;

    @Getter @Setter
    private boolean requireaccessforhelp;

    public GuildConfiguration(String id, String botaccessroleid, String prefix, boolean requireaccessforhelp) {
        this.id = id;
        this.botaccessroleid = botaccessroleid;
        this.prefix = prefix;
        this.requireaccessforhelp = requireaccessforhelp;
    }

}
