package me.noobgam.pastie.main.jetty;

public final class Environment {
    private Environment(){
    }

    public static final String HOST;

    static {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            HOST = System.getenv("COMPUTERNAME");
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac os x")) {
            HOST = System.getenv("HOSTNAME");
        } else {
            HOST = "unknown";
        }
    }
}
