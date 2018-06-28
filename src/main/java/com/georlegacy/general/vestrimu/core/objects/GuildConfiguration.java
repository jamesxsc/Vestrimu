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

    public GuildConfiguration(String id, String botaccessroleid, String prefix) {
        this.id = id;
        this.botaccessroleid = botaccessroleid;
        this.prefix = prefix;
    }

}
