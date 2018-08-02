package com.georlegacy.general.vestrimu.core.objects.behaviour;

import com.georlegacy.general.vestrimu.core.objects.base.JSONSerializable;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class MemberBehaviourRecord extends JSONSerializable<MemberBehaviourRecord> {

    @Getter private List<Punishment> punishments;

    public MemberBehaviourRecord(boolean b) {
        super(MemberBehaviourRecord::new);

        this.punishments = new ArrayList<Punishment>();
    }

    private MemberBehaviourRecord() {}

}
