package com.jugaado.chat.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigProperties {
    private static final String CONFIG_FILE = "Config.properties";

    private static FileInputStream in;
    private static Properties properties;

    private static void init() throws IOException {
        in = new FileInputStream(CONFIG_FILE);
        properties = new Properties();
        properties.load(in);
        in.close();
    }

    public static String getProperty(String key) {
        if (properties == null) {
            try {
                init();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return properties.getProperty(key);
    }
}
