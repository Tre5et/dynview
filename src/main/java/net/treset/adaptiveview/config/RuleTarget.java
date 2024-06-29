package net.treset.adaptiveview.config;

public enum RuleTarget {
    VIEW("view_distance"),
    SIMULATION("simulation_distance");

    private final String name;

    RuleTarget(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
