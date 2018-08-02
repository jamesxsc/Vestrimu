package com.georlegacy.general.vestrimu.core.objects.base;

import com.google.gson.Gson;
import org.json.JSONObject;

import java.util.function.Supplier;

public abstract class JSONSerializable<T extends JSONSerializable> {

    private final T field;

    private final Supplier<T> supplier;

    protected JSONSerializable(Supplier<T> supplier) {
        this.supplier = supplier;
        this.field = this.supplier.get();
    }

    public final JSONObject serialize() {
        Gson gson = new Gson();
        return new JSONObject(gson.toJson(this));
    }

    @SuppressWarnings("unchecked")
    public final T deserialize(JSONObject json) {
        Gson gson = new Gson();
        return (T) gson.fromJson(json.toString(), field.getClass());
    }

}
