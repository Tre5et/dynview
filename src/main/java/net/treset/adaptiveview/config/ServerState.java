package net.treset.adaptiveview.config;

import java.util.List;

public class ServerState {
    private int currentViewDistance;
    private double mspt;
    private double memory;
    private List<String> players;

    public ServerState(int currentViewDistance, double mspt, double memory, List<String> players) {
        this.currentViewDistance = currentViewDistance;
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
