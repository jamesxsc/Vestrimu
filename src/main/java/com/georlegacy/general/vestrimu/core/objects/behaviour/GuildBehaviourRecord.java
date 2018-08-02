package com.georlegacy.general.vestrimu.core.objects.behaviour;

import com.georlegacy.general.vestrimu.core.objects.base.JSONSerializable;
import com.georlegacy.general.vestrimu.util.Constants;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GuildBehaviourRecord extends JSONSerializable<GuildBehaviourRecord> {

    @Getter private Map<String, MemberBehaviourRecord> memberRecords;

    public GuildBehaviourRecord(boolean b) {
        super(GuildBehaviourRecord::new);

        this.memberRecords = new HashMap<String, MemberBehaviourRecord>();
    }

    private GuildBehaviourRecord() {}

}
