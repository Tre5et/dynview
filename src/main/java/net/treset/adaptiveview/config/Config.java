package net.treset.adaptiveview.config;

import com.google.gson.*;
import net.treset.adaptiveview.AdaptiveViewMod;

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
    private boolean allowOnClient;
    private ArrayList<Rule> rules;

    public Config(int updateRate, int maxViewDistance, int minViewDistance, boolean allowOnClient, ArrayList<Rule> rules) {
        this.updateRate = updateRate;
        this.maxViewDistance = maxViewDistance;
        this.minViewDistance = minViewDistance;
        this.allowOnClient = allowOnClient;
        this.rules = rules;
    }

    public static Config generic() {
        return new Config(
            600,
            20,
            4,
            false,
            new ArrayList<>(List.of(
                    new Rule(
                            RuleType.MSPT,
                            null,
                            null,
                            60,
                            null,
                            -2,
                            null,
                            null
                    ),
                    new Rule(
                            RuleType.MSPT,
                            null,
                            null,
                            50,
                            null,
                            -1,
                            null,
                            null
                    ),
                    new Rule(
                            RuleType.MSPT,
                            null,
                            40,
                            null,
                            null,
                            1,
                            null,
                            null

                    ),
                    new Rule(
                            RuleType.MSPT,
                            null,
                            30,
                            null,
                            null,
                            2,
                            null,
                            null

                    )
            ))
        );
    }

    public static Config load() {
        if(!configFile.exists()) {
            return migrateOldConfig();
        }

        String json;
        try {
            json = Files.readString(configFile.toPath());
        } catch (IOException e) {
            AdaptiveViewMod.LOGGER.warn("Failed to read config, using default", e);
            return Config.generic();
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
            AdaptiveViewMod.LOGGER.info("Loaded config");
            return config;
        } catch (JsonSyntaxException e) {
            AdaptiveViewMod.LOGGER.warn("Failed to parse config, using default", e);
            return Config.generic();
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
            oldConfig.isOverrideClient(),
            new ArrayList<>(List.of(
                    new Rule(
                            RuleType.MSPT,
                            null,
                            null,
                            oldConfig.getMaxMsptAggressive(),
                            null,
                            -2,
                            null,
                            null
                    ),
                    new Rule(
                            RuleType.MSPT,
                            null,
                            null,
                            oldConfig.getMaxMspt(),
                            null,
                            -1,
                            null,
                            null
                    ),
                    new Rule(
                            RuleType.MSPT,
                            null,
                            oldConfig.getMinMspt(),
                            null,
                            null,
                            1,
                            null,
                            null

                    ),
                    new Rule(
                            RuleType.MSPT,
                            null,
                            oldConfig.getMinMsptAggressive(),
                            null,
                            null,
                            2,
                            null,
                            null

                    )
            ))
        );

        config.save();

        AdaptiveViewMod.LOGGER.info("Migrated new config.");
        return config;
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

    public boolean isAllowOnClient() {
        return allowOnClient;
    }

    public void setAllowOnClient(boolean allowOnClient) {
        this.allowOnClient = allowOnClient;
    }

    public ArrayList<Rule> getRules() {
        return rules;
    }

    public void setRules(ArrayList<Rule> rules) {
        this.rules = rules;
    }
}
