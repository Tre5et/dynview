package net.treset.adaptiveview.config;

public enum RuleTarget {
    VIEW("View Distance"),
    SIMULATION("Simulation Distance"),
    CHUNK_TICKING("Chunk-Ticking Distance");

    private final String name;

    RuleTarget(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
