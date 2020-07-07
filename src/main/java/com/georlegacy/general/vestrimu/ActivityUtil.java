package com.georlegacy.general.vestrimu;

public class ActivityUtil {


    public static String getDisplayNameFromKey(int key) {

        switch (key) {
            case 0: return "Playing";
            case 1: return "Streaming";
            case 2: return "Listening To";
            case 3: return "Watching";
            case 4: return "Custom Status";
            default: return "Invalid Key";
        }

    }

}
