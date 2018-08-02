package com.georlegacy.general.vestrimu.core.objects.behaviour;

import com.georlegacy.general.vestrimu.core.objects.base.JSONSerializable;

import java.util.ArrayList;
import java.util.List;

public class GuildBehaviourRecord extends JSONSerializable<GuildBehaviourRecord> {

    private List<MemberBehaviourRecord> memberRecords;

    public GuildBehaviourRecord() {
        super(GuildBehaviourRecord::new);

        this.memberRecords = new ArrayList<MemberBehaviourRecord>();
    }

}
