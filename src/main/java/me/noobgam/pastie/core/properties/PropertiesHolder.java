package me.noobgam.pastie.core.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesHolder {
    private static final Properties PROPERTIES = new Properties();

    static {
        // loads main props
        try (InputStream is = PropertiesHolder.class.getClassLoader().getResourceAsStream("main.properties")) {
            PROPERTIES.load(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getProperty(String key) {
        synchronized (PROPERTIES) {
            return PROPERTIES.getProperty(key);
        }
    }

    public static int getIntProperty(String key) {
        synchronized (PROPERTIES) {
            return Integer.valueOf(PROPERTIES.getProperty(key));
        }
    }

    public static void loadProperties(InputStream inputStream) throws IOException {
        synchronized (PROPERTIES) {
            PROPERTIES.load(inputStream);
        }
    }
}
