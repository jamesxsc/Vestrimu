package com.georlegacy.general.vestrimu;

import java.io.IOException;
import java.util.Properties;

public class SecretConstants {

    private static final Properties properties;

    static {
        properties = new Properties();
        try {
            properties.load(Vestrimu.getInstance().getClass().getClassLoader().getResourceAsStream("credentials.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Invalid credentials.properties");
        }


        TOKEN = getPropertyOrFail("token");
        SQL_USER = getPropertyOrFail("sql_user");
        SQL_PASS = getPropertyOrFail("sql_pass");
        YAND_TRAN_SECRET = getPropertyOrFail("yand_tran_secret");
        UNSPLASH_ACCESS = getPropertyOrFail("unsplash_access");
        UNSPLASH_SECRET = getPropertyOrFail("unsplash_secret");
    }

    private static String getPropertyOrFail(String key) {
        String property = properties.getProperty(key);
        if (property == null || property.isEmpty())
            throw new RuntimeException("Invalid value for key '" + key + "' in credentials.properties");
        else
            return property;
    }

    // todo add sql url etc.

    public static final String TOKEN;

    public static final String SQL_USER;

    public static final String SQL_PASS;

    public static final String YAND_TRAN_SECRET;

    public static final String UNSPLASH_ACCESS;

    public static final String UNSPLASH_SECRET;

}
