package net.treset.adaptiveview.config;

public enum RuleType {
    MSPT("MSPT"),
    MEMORY("Memory"),
    PLAYERS("Players");

    private final String name;

    RuleType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
