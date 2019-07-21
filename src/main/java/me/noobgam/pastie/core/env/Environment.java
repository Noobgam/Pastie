package me.noobgam.pastie.core.env;

import me.noobgam.pastie.core.properties.SecretsLoader;

public final class Environment {

    public enum Type {
        PROD,
        TESTING,
        DEV
    }

    public enum OS {
        WIN,
        NIX
    }

    private Environment(){
    }

    public static final String HOST;
    public static final Type ENV;
    public static final OS SYSTEM;

    static {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            HOST = System.getenv("COMPUTERNAME");
            SYSTEM = OS.WIN;
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac os x")) {
            HOST = System.getenv("HOSTNAME");
            SYSTEM = OS.NIX;
        } else {
            HOST = "unknown";
            SYSTEM = OS.WIN;
        }

        String env = System.getenv("ENV");
        if ("PROD".equals(env)) {
            ENV = Type.PROD;
        } else if ("TEST".equals(env)) {
            ENV = Type.TESTING;
        } else {
            ENV = Type.DEV;
        }
    }

    public static void init() {
        SecretsLoader.loadSecrets();
    }
}
