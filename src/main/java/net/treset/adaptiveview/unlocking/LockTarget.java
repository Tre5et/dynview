package net.treset.adaptiveview.unlocking;

public enum LockTarget {
    MAIN("View and Simulation Distance", "are"),
    VIEW("View Distance", "is"),
    SIM("Simulation Distance", "is"),
    CHUNK("Chunk-Ticking Distance", "is");

    private final String prettyString;
    private final String is;

    LockTarget(String prettyString, String is) {
        this.prettyString = prettyString;
        this.is = is;
    }

    public String getPrettyString() {
        return prettyString;
    }

    public String getIs() {
        return is;
    }
}
