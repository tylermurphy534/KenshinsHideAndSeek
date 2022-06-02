package net.tylermurphy.hideAndSeek.command.location.util;

/**
 * @author bobby29831
 */
public enum Locations {

    GAME("spawns.game"),
    LOBBY("spawns.lobby"),
    EXIT("spawns.exit");

    private final String path;
    Locations(String path) {
        this.path = path;
    }

    public String message() {
        return this + "_SPAWN";
    }

    public String path(String additive) {
        return path + "." + additive;
    }

}