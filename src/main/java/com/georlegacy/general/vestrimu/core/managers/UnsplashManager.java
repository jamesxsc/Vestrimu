package com.georlegacy.general.vestrimu.core.managers;

import com.georlegacy.general.vestrimu.SecretConstants;
import com.github.rye761.unsplash.Photo;
import com.github.rye761.unsplash.Unsplash;
import com.github.scribejava.core.model.Verb;
import com.google.gson.Gson;

import java.util.HashMap;

public class UnsplashManager {

    public UnsplashManager() {
        Unsplash.getInstance().init(new HashMap<String, String>() {{
            put("applicationId", SecretConstants.UNSPLASH_ACCESS);
            put("secret", SecretConstants.UNSPLASH_SECRET);
            put("callbackUrl", "xsc.co.uk");
        }});
    }

    public String getRandomThumbFromQuery(String query) {
        return new Gson().fromJson(Unsplash.getInstance().request(Verb.GET, "photos/random", new HashMap<String, String>() {{
            put("query", query);
        }}), Photo.class).urls.thumb;
    }

}
