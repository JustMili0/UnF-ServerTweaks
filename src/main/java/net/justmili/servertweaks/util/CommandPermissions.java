package net.justmili.servertweaks.util;

public enum CommandPermissions {
    ENTITY_SELECTORS(0),
    MODERATOR(1),
    GAMEMASTER(2),
    ADMIN(3),
    OWNER(4);

    private final int level;
    CommandPermissions(int level) {
        this.level = level;
    }
    public int level() {
        return level;
    }
}
