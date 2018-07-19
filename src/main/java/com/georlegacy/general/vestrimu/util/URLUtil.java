package com.georlegacy.general.vestrimu.util;

import java.net.MalformedURLException;
import java.net.URL;

public class URLUtil {

    public static boolean validateURL(String url) {
        if (url == null)
            return true;
        try {
            new URL(url);
        } catch (MalformedURLException ex) {
            return false;
        }
        return true;
    }

}
