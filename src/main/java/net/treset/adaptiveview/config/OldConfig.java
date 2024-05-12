package net.treset.adaptiveview.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.treset.adaptiveview.AdaptiveViewMod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class OldConfig {
    private static final File configFile = new File("./config/adaptiveview.json");
    private static final File oldConfigFile = new File("./config/dynview.json");
    private int locked = 0;
    private int updateInterval = 600;
    private int minMspt = 40;
    private int maxMspt = 50;
    private int minMsptAggressive = 60;
    private int maxMsptAggressive = 30;
    private int minViewDistance = 4;
    private int maxViewDistance = 20;
    private boolean overrideClient = false;

    public static OldConfig load() {
        File file = configFile;
        if(!file.exists()) {
            file = oldConfigFile;
            if (!file.exists()) {
                return null;
            }
        }
        String json;
        try {
            json = Files.readString(file.toPath());
        } catch (IOException e) {
            AdaptiveViewMod.LOGGER.warn("Failed to read old config file", e);
            return new OldConfig();
        }
        if(file == oldConfigFile) {
            try {
                Files.delete(oldConfigFile.toPath());
            } catch (IOException e) {
                AdaptiveViewMod.LOGGER.warn("Failed to delete old old config file", e);
            }
        }

        try {
            return new Gson().fromJson(json, OldConfig.class);
        } catch (JsonSyntaxException e) {
            AdaptiveViewMod.LOGGER.error("Failed to parse old config file", e);
        }
        return null;
    }

    public void save() {
        if(!configFile.exists()) {
            try {
                Files.createDirectories(configFile.getParentFile().toPath());
                Files.createFile(configFile.toPath());
            } catch (IOException e) {
                AdaptiveViewMod.LOGGER.error("Failed to create config file", e);
            }
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(this);
        try {
            Files.writeString(configFile.toPath(), json);
        } catch (IOException e) {
            AdaptiveViewMod.LOGGER.error("Failed to write config file", e);
        }
    }

    public int getLocked() {
        return locked;
    }

    public int getUpdateInterval() {
        return updateInterval;
    }

    public int getMinMspt() {
        return minMspt;
    }

    public int getMaxMspt() {
        return maxMspt;
    }

    public int getMinMsptAggressive() {
        return minMsptAggressive;
    }

    public int getMaxMsptAggressive() {
        return maxMsptAggressive;
    }

    public int getMinViewDistance() {
        return minViewDistance;
    }

    public int getMaxViewDistance() {
        return maxViewDistance;
    }

    public void setLocked(int locked) {
        this.locked = locked;
    }

    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    public void setMinMspt(int minMspt) {
        this.minMspt = minMspt;
    }

    public void setMaxMspt(int maxMspt) {
        this.maxMspt = maxMspt;
    }

    public void setMinMsptAggressive(int minMsptAggressive) {
        this.minMsptAggressive = minMsptAggressive;
    }

    public void setMaxMsptAggressive(int maxMsptAggressive) {
        this.maxMsptAggressive = maxMsptAggressive;
    }

    public void setMinViewDistance(int minViewDistance) {
        this.minViewDistance = minViewDistance;
    }

    public void setMaxViewDistance(int maxViewDistance) {this.maxViewDistance = maxViewDistance;
    }

    public boolean isOverrideClient() {
        return overrideClient;
    }

    public void setOverrideClient(boolean overrideClient) {
        this.overrideClient = overrideClient;
    }
}
