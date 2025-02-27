package net.treset.adaptiveview.config;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.treset.adaptiveview.AdaptiveViewMod;
import net.treset.adaptiveview.tools.BroadcastLevel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Config {
    private static final File configFile = new File("./config/adaptiveview.json");
    private static final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create();

    private int updateRate;
    private int maxViewDistance;
    private int minViewDistance;
    private int maxSimDistance;
    private int minSimDistance;
    private int maxChunkTickingDistance;
    private int minChunkTickingDistance;
    private boolean allowOnClient;
    @SerializedName("broadcast_to_ops")
    @Expose(serialize = false)
    private boolean deprecatedBroadcastToOps;
    private BroadcastLevel broadcastChangesDefault;
    @SerializedName(value = "broadcast_changes", alternate = "broadcast_to")
    private ArrayList<String> broadcastChanges;
    private BroadcastLevel broadcastLockDefault;
    private ArrayList<String> broadcastLock;
    private ArrayList<Rule> rules;

    private transient boolean viewLocked = false;
    private transient boolean simLocked = false;
    private transient boolean chunkTickingLocked = false;

    public Config(int updateRate, int maxViewDistance, int minViewDistance, int maxSimDistance, int minSimDistance, int maxChunkTickingDistance, int minChunkTicking, boolean allowOnClient, BroadcastLevel broadcastChangesDefault, ArrayList<String> broadcastChanges, BroadcastLevel broadcastLockDefault, ArrayList<String> broadcastLock, ArrayList<Rule> rules) {
        this.updateRate = updateRate;
        this.maxViewDistance = maxViewDistance;
        this.minViewDistance = minViewDistance;
        this.maxSimDistance = maxSimDistance;
        this.minSimDistance = minSimDistance;
        this.maxChunkTickingDistance = maxChunkTickingDistance;
        this.minChunkTickingDistance = minChunkTicking;
        this.allowOnClient = allowOnClient;
        this.rules = rules;
        this.broadcastChangesDefault = broadcastChangesDefault;
        this.broadcastChanges = broadcastChanges;
        this.broadcastLockDefault = broadcastLockDefault;
        this.broadcastLock = broadcastLock;
    }

    public static Config generic() {
        return new Config(
            600,
            20,
            4,
            20,
            4,
            8,
            2,
            false,
            BroadcastLevel.NONE,
            new ArrayList<>(),
            BroadcastLevel.OPS,
            new ArrayList<>(),
            new ArrayList<>(List.of(
                    new Rule(
                            RuleType.MSPT,
                            null,
                            null,
                            60,
                            RuleTarget.VIEW,
                            null,
                            -2,
                            null,
                            null,
                            null,
                            null
                    ),
                    new Rule(
                            RuleType.MSPT,
                            null,
                            null,
                            50,
                            RuleTarget.VIEW,
                            null,
                            -1,
                            null,
                            null,
                            null,
                            null
                    ),
                    new Rule(
                            RuleType.MSPT,
                            null,
                            40,
                            null,
                            RuleTarget.VIEW,
                            null,
                            1,
                            null,
                            null,
                            null,
                            null

                    ),
                    new Rule(
                            RuleType.MSPT,
                            null,
                            30,
                            null,
                            RuleTarget.VIEW,
                            null,
                            2,
                            null,
                            null,
                            null,
                            null
                    )
            ))
        );
    }

    public static Config loadOrDefault() {
        try  {
            return load();
        } catch (IOException e) {
            return generic();
        }
    }

    public static Config load() throws IOException {
        if(!configFile.exists()) {
            return migrateOldConfig();
        }

        String json;
        try {
            json = Files.readString(configFile.toPath());
        } catch (IOException e) {
            AdaptiveViewMod.LOGGER.warn("Failed to read config, using default", e);
            throw e;
        }

        try {
            Config config = gson.fromJson(json, Config.class);
            if(config.rules == null) {
                AdaptiveViewMod.LOGGER.warn("Config not valid, trying to migrate old one");
                return migrateOldConfig();
            }
            for (Rule rule : config.rules) {
                if(!rule.isEffective()) {
                    AdaptiveViewMod.LOGGER.warn("Rule is not effective: {}", rule);
                }
            }
            amendMigration(config);
            AdaptiveViewMod.LOGGER.info("Loaded config");
            return config;
        } catch (JsonSyntaxException e) {
            AdaptiveViewMod.LOGGER.warn("Failed to parse config, using default", e);
            throw new IOException("Failed to load config", e);
        }
    }

    private static void amendMigration(Config config) {
        if(config.getBroadcastChangesDefault() == null) {
            config.setBroadcastChangesDefault(config.deprecatedIsBroadcastToOps() ? BroadcastLevel.OPS : BroadcastLevel.NONE);
        }
        if(config.getBroadcastChanges() == null) {
            config.setBroadcastChanges(new ArrayList<>());
        }
        if(config.getBroadcastLockDefault() == null) {
            config.setBroadcastLockDefault(BroadcastLevel.OPS);
        }
        if(config.getBroadcastLock() == null) {
            config.setBroadcastLock(new ArrayList<>());
        }
    }

    private static Config migrateOldConfig() {
        OldConfig oldConfig = OldConfig.load();
        if(oldConfig == null) {
            AdaptiveViewMod.LOGGER.info("Creating new config...");
            Config config = Config.generic();
            config.save();
            AdaptiveViewMod.LOGGER.info("Crated new config.");
            return config;
        }

        AdaptiveViewMod.LOGGER.info("Migrating old config...");

        Config config = new Config(
            oldConfig.getUpdateInterval(),
            oldConfig.getMaxViewDistance(),
            oldConfig.getMinViewDistance(),
            oldConfig.getMaxViewDistance(),
            oldConfig.getMinViewDistance(),
            8,
            8,
            oldConfig.isOverrideClient(),
            BroadcastLevel.NONE,
            new ArrayList<>(),
            BroadcastLevel.OPS,
            new ArrayList<>(),
            new ArrayList<>(List.of(
                    new Rule(
                            RuleType.MSPT,
                            null,
                            null,
                            oldConfig.getMaxMsptAggressive(),
                            RuleTarget.VIEW,
                            null,
                            -2,
                            null,
                            null,
                            null,
                            null
                    ),
                    new Rule(
                            RuleType.MSPT,
                            null,
                            null,
                            oldConfig.getMaxMspt(),
                            RuleTarget.VIEW,
                            null,
                            -1,
                            null,
                            null,
                            null,
                            null
                    ),
                    new Rule(
                            RuleType.MSPT,
                            null,
                            oldConfig.getMinMspt(),
                            null,
                            RuleTarget.VIEW,
                            null,
                            1,
                            null,
                            null,
                            null,
                            null

                    ),
                    new Rule(
                            RuleType.MSPT,
                            null,
                            oldConfig.getMinMsptAggressive(),
                            null,
                            RuleTarget.VIEW,
                            null,
                            2,
                            null,
                            null,
                            null,
                            null
                    )
            ))
        );

        config.save();

        AdaptiveViewMod.LOGGER.info("Migrated new config.");
        return config;
    }

    public void copy(Config config) {
        this.updateRate = config.updateRate;
        this.maxViewDistance = config.maxViewDistance;
        this.minViewDistance = config.minViewDistance;
        this.maxSimDistance = config.maxSimDistance;
        this.minSimDistance = config.minSimDistance;
        this.maxChunkTickingDistance = config.maxChunkTickingDistance;
        this.minChunkTickingDistance = config.minChunkTickingDistance;
        this.allowOnClient = config.allowOnClient;
        this.broadcastChangesDefault = config.broadcastChangesDefault;
        this.broadcastChanges = config.broadcastChanges;
        this.broadcastLockDefault = config.broadcastLockDefault;
        this.broadcastLock = config.broadcastLock;
        this.rules = config.rules;
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

        String json = gson.toJson(this);
        try {
            Files.writeString(configFile.toPath(), json);
            AdaptiveViewMod.LOGGER.info("Saved config");
        } catch (IOException e) {
            AdaptiveViewMod.LOGGER.error("Failed to write config file", e);
        }
    }

    public int getUpdateRate() {
        return updateRate;
    }

    public void setUpdateRate(int updateRate) {
        this.updateRate = updateRate;
    }

    public int getMaxViewDistance() {
        return maxViewDistance;
    }

    public void setMaxViewDistance(int maxViewDistance) {
        this.maxViewDistance = maxViewDistance;
    }

    public int getMinViewDistance() {
        return minViewDistance;
    }

    public void setMinViewDistance(int minViewDistance) {
        this.minViewDistance = minViewDistance;
    }

    public int getMaxSimDistance() {
        if(maxSimDistance == 0) {
            setMaxSimDistance(maxViewDistance);
            save();
        }
        return maxSimDistance;
    }

    public void setMaxSimDistance(int maxSimDistance) {
        this.maxSimDistance = maxSimDistance;
    }

    public int getMinSimDistance() {
        if(minSimDistance == 0) {
            setMinSimDistance(minViewDistance);
            save();
        }
        return minSimDistance;
    }

    public void setMinSimDistance(int minSimDistance) {
        this.minSimDistance = minSimDistance;
    }

    public int getMaxChunkTickingDistance() {
        if(maxChunkTickingDistance <= 0) {
            setMaxChunkTickingDistance(8);
            save();
        }
        return maxChunkTickingDistance;
    }

    public void setMaxChunkTickingDistance(int maxChunkTickingDistance) {
        this.maxChunkTickingDistance = maxChunkTickingDistance;
    }

    public int getMinChunkTickingDistance() {
        if(minChunkTickingDistance <= 0) {
            setMinChunkTickingDistance(8);
            save();
        }
        return minChunkTickingDistance;
    }

    public void setMinChunkTickingDistance(int minChunkTickingDistance) {
        this.minChunkTickingDistance = minChunkTickingDistance;
    }

    public boolean isAllowOnClient() {
        return allowOnClient;
    }

    public void setAllowOnClient(boolean allowOnClient) {
        this.allowOnClient = allowOnClient;
    }

    public BroadcastLevel getBroadcastChangesDefault() {
        return broadcastChangesDefault;
    }

    public void setBroadcastChangesDefault(BroadcastLevel broadcastChangesDefault) {
        this.broadcastChangesDefault = broadcastChangesDefault;
    }

    public ArrayList<String> getBroadcastChanges() {
        return broadcastChanges;
    }

    public void setBroadcastChanges(ArrayList<String> broadcastChanges) {
        this.broadcastChanges = broadcastChanges;
    }

    public BroadcastLevel getBroadcastLockDefault() {
        return broadcastLockDefault;
    }

    public void setBroadcastLockDefault(BroadcastLevel broadcastLockDefault) {
        this.broadcastLockDefault = broadcastLockDefault;
    }

    public ArrayList<String> getBroadcastLock() {
        return broadcastLock;
    }

    public void setBroadcastLock(ArrayList<String> broadcastLock) {
        this.broadcastLock = broadcastLock;
    }

    public ArrayList<Rule> getRules() {
        return rules;
    }

    public void setRules(ArrayList<Rule> rules) {
        this.rules = rules;
    }

    public boolean isViewLocked() {
        return viewLocked;
    }

    public void setViewLocked(boolean viewLocked) {
        this.viewLocked = viewLocked;
    }

    public boolean isSimLocked() {
        return simLocked;
    }

    public void setSimLocked(boolean simLocked) {
        this.simLocked = simLocked;
    }

    public boolean isChunkTickingLocked() {
        return chunkTickingLocked;
    }

    public void setChunkTickingLocked(boolean chunkTickingLocked) {
        this.chunkTickingLocked = chunkTickingLocked;
    }

    @Deprecated
    public boolean deprecatedIsBroadcastToOps() {
        return deprecatedBroadcastToOps;
    }
}
