package com.georlegacy.general.vestrimu.core.objects.behaviour;

import com.georlegacy.general.vestrimu.core.objects.base.JSONSerializable;

import java.util.ArrayList;
import java.util.List;

public class MemberBehaviourRecord extends JSONSerializable<MemberBehaviourRecord> {

    private List<Punishment> punishments;

    public MemberBehaviourRecord() {
        super(MemberBehaviourRecord::new);

        this.punishments = new ArrayList<Punishment>();
    }

}
