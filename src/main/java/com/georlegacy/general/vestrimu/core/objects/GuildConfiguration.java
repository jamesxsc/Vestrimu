package com.georlegacy.general.vestrimu.core.objects;

import lombok.Getter;
import lombok.Setter;

public class GuildConfiguration {

    @Getter @Setter
    private String id;

    @Getter @Setter
    private String botaccessroleid;

    public GuildConfiguration(String id, String botaccessroleid) {
        this.id = id;
        this.botaccessroleid = botaccessroleid;
    }

}
