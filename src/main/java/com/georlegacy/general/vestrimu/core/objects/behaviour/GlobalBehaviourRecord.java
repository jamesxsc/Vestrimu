package com.georlegacy.general.vestrimu.core.objects.behaviour;

import com.georlegacy.general.vestrimu.core.objects.base.JSONSerializable;

import java.util.ArrayList;
import java.util.List;

// Usage Delayed
public class GlobalBehaviourRecord extends JSONSerializable<GlobalBehaviourRecord> {

    private List<UserBehaviourRecord> userRecords;

    private List<GuildBehaviourRecord> guildRecords;

    protected GlobalBehaviourRecord() {
        super(GlobalBehaviourRecord::new);

        this.userRecords = new ArrayList<UserBehaviourRecord>();
        this.guildRecords = new ArrayList<GuildBehaviourRecord>();
    }

}
