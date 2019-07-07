package me.noobgam.pastie.core.properties;

import me.noobgam.pastie.core.env.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;

public final class SecretsLoader {

    private static final Logger logger = LogManager.getLogger(SecretsLoader.class);

    public static void loadSecrets() {
        final File folder;
        if (Environment.SYSTEM == Environment.OS.WIN) {
            folder = new File(PropertiesHolder.getProperty("win.secrets.folder"));
        } else {
            folder = new File(PropertiesHolder.getProperty("nix.secrets.folder"));
        }
        if (folder.isDirectory() && folder.canRead()) {
            Environment.Type env = Environment.ENV;
            File props = new File(folder, "secrets-" + env.name().toLowerCase() + ".properties");
            try {
                PropertiesHolder.loadProperties(new FileInputStream(props));
            } catch (Exception e) {
                logger.warn("Error happened during secrets loading {}", props);
            }
        } else {
            logger.error("{} is not a valid secret folder", folder);
        }
    }
}
