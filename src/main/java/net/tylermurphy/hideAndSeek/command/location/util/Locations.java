package net.tylermurphy.hideAndSeek.command.location.util;

/**
 * @author bobby29831
 */
public enum Locations {

    GAME("spawns.game", "GAME_SPAWN"),
    LOBBY("spawns.lobby", "LOBBY_SPAWN"),
    EXIT("spawns.exit", "EXIT_SPAWN");

    private final String path, message;
    Locations(String path, String message) {
        this.path = path;
        this.message = message;
    }

    public String message() {
        return message;
    }

    public String path() {
        return path;
    }

    public String path(String additive) {
        return path + "." + additive;
    }

}