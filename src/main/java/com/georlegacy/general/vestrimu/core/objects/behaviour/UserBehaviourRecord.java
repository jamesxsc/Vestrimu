package com.georlegacy.general.vestrimu.core.objects.behaviour;

import com.georlegacy.general.vestrimu.core.objects.base.JSONSerializable;

import java.util.ArrayList;
import java.util.List;

// Usage Delayed
public class UserBehaviourRecord extends JSONSerializable<UserBehaviourRecord> {

    private List<Punishment> punishments;

    protected UserBehaviourRecord() {
        super(UserBehaviourRecord::new);

        this.punishments = new ArrayList<Punishment>();
    }

}
