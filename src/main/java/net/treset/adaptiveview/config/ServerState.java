package net.treset.adaptiveview.config;

import java.util.List;

public class ServerState {
    private double mspt;
    private double memory;
    private List<String> players;

    public ServerState(double mspt, double memory, List<String> players) {
        this.mspt = mspt;
        this.memory = memory;
        this.players = players;
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
