package net.treset.adaptiveview.config;

import java.util.List;

public class ServerState {
    private int currentViewDistance;
    private int currentSimDistance;
    private double mspt;
    private double memory;
    private List<String> players;

    public ServerState(int currentViewDistance, int currentSimDistance, double mspt, double memory, List<String> players) {
        this.currentViewDistance = currentViewDistance;
        this.currentSimDistance = currentSimDistance;
        this.mspt = mspt;
        this.memory = memory;
        this.players = players;
    }

    public int getCurrentViewDistance() {
        return currentViewDistance;
    }

    public void setCurrentViewDistance(int currentViewDistance) {
        this.currentViewDistance = currentViewDistance;
    }

    public int getCurrentSimDistance() {
        return currentSimDistance;
    }

    public void setCurrentSimDistance(int currentSimDistance) {
        this.currentSimDistance = currentSimDistance;
    }

    public double getMspt() {
        return mspt;
    }

    public void setMspt(double mspt) {
        this.mspt = mspt;
    }

    public double getMemory() {
        return memory;
    }

    public void setMemory(double memory) {
        this.memory = memory;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }
}
