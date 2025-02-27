package net.treset.adaptiveview.distance;

import net.minecraft.server.network.ServerPlayerEntity;
import net.treset.adaptiveview.AdaptiveViewMod;
import net.treset.adaptiveview.config.Config;
import net.treset.adaptiveview.config.Rule;
import net.treset.adaptiveview.config.ServerState;
import net.treset.adaptiveview.tools.NotificationState;
import net.treset.adaptiveview.tools.TextTools;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ViewDistanceHandler {

    private final Config config;

    public ViewDistanceHandler(Config config) {
        this.config = config;
    }

    public int updateViewDistance(ServerState state) {
        ArrayList<Rule> viewDistanceRules = new ArrayList<>();
        ArrayList<Integer> viewDistanceIndexes = new ArrayList<>();
        ArrayList<Rule> simDistanceRules = new ArrayList<>();
        ArrayList<Integer> simDistanceIndexes = new ArrayList<>();
        ArrayList<Rule> chunkTickingRules = new ArrayList<>();
        ArrayList<Integer> chunkTickingIndexes = new ArrayList<>();

        for(int i = 0; i < config.getRules().size(); i++) {
            Rule rule = config.getRules().get(i);
            if(rule.applies(state)) {
                switch (rule.getTarget()) {
                    case VIEW -> {
                        viewDistanceRules.add(rule);
                        viewDistanceIndexes.add(i + 1);
                    }
                    case SIMULATION -> {
                        simDistanceRules.add(rule);
                        simDistanceIndexes.add(i + 1);
                    }
                    case CHUNK_TICKING -> {
                        chunkTickingRules.add(rule);
                        chunkTickingIndexes.add(i + 1);
                    }
                }
            }
        }

        DistanceData viewDistanceData = DistanceData.extract(viewDistanceRules, config.getMaxViewDistance(), config.getMinViewDistance());
        DistanceData simDistanceData = DistanceData.extract(simDistanceRules, config.getMaxSimDistance(), config.getMinSimDistance());
        DistanceData chunkTickingData = DistanceData.extract(chunkTickingRules, config.getMaxChunkTickingDistance(), config.getMinChunkTickingDistance());

        int targetViewDistance = viewDistanceData.getTargetDistance(state.currentViewDistance());
        if(targetViewDistance != state.currentViewDistance() && !config.isViewLocked()) {
            TextTools.broadcastIf((p) -> shouldBroadcastChange(p, config), "Changed View Distance from %d to %d because of %s.", state.currentViewDistance(), targetViewDistance, getRuleCauseString(viewDistanceIndexes));
            setViewDistance(targetViewDistance);
        }

        int targetSimDistance = simDistanceData.getTargetDistance(state.currentSimDistance());
        if(targetSimDistance != state.currentSimDistance() && !config.isSimLocked()) {
            TextTools.broadcastIf((p) -> shouldBroadcastChange(p, config), "Changed Simulation Distance from %d to %d because of %s.", state.currentSimDistance(), targetSimDistance, getRuleCauseString(simDistanceIndexes));
            setSimDistance(targetSimDistance);
        }

        int targetChunkTicking = chunkTickingData.getTargetDistance(state.currentChunkTickingDistance());
        if(targetChunkTicking != state.currentChunkTickingDistance() && !config.isChunkTickingLocked()) {
            TextTools.broadcastIf((p) -> shouldBroadcastChange(p, config), "Changed Chunk-Ticking Distance from %d to %d because of %s.", state.currentChunkTickingDistance(), targetChunkTicking, getRuleCauseString(chunkTickingIndexes));
            setChunkTickingDistance(targetChunkTicking);
        }

        int updateRate = IntStream.of(viewDistanceData.updateRate(), simDistanceData.updateRate(), chunkTickingData.updateRate()).min().getAsInt();
        if(updateRate == Integer.MAX_VALUE) {
            updateRate = config.getUpdateRate();
        }
        return updateRate;
    }

    private String getRuleCauseString(List<Integer> activeIndexes) {
        if(activeIndexes.isEmpty()) {
            return "no Rules";
        }
        if(activeIndexes.size() == 1) {
            String ruleName = config.getRules().get(activeIndexes.get(0) - 1).getName();
            if(ruleName == null) {
                ruleName = activeIndexes.get(0).toString();
            }
            return "Rule " + ruleName;
        }
        StringBuilder sb = new StringBuilder("Rules ");
        String ruleName1 = config.getRules().get(activeIndexes.get(0) - 1).getName();
        if(ruleName1 == null) {
            ruleName1 = activeIndexes.get(0).toString();
        }
        sb.append(ruleName1);
        for(int i = 1; i < activeIndexes.size() - 1; i++) {
            String ruleName = config.getRules().get(activeIndexes.get(i) - 1).getName();
            if(ruleName == null) {
                ruleName = activeIndexes.get(i).toString();
            }
            sb.append(", ").append(ruleName);
        }
        String ruleNameN = config.getRules().get(activeIndexes.get(activeIndexes.size() - 1) - 1).getName();
        if(ruleNameN == null) {
            ruleNameN = activeIndexes.get(activeIndexes.size() - 1).toString();
        }
        sb.append(" and ").append(ruleNameN);
        return sb.toString();
    }

    public static void setViewDistance(int chunks) {
        AdaptiveViewMod.getServer().getPlayerManager().setViewDistance(chunks);
    }

    public static void setSimDistance(int chunks) {
        AdaptiveViewMod.getServer().getPlayerManager().setSimulationDistance(chunks);
    }

    public static int getViewDistance() {
        return AdaptiveViewMod.getServer().getPlayerManager().getViewDistance();
    }

    public static int getSimDistance() {
        return AdaptiveViewMod.getServer().getPlayerManager().getSimulationDistance();
    }

    private static int chunkTickingDistance = 8;

    public static int getChunkTickingDistance() {
        return chunkTickingDistance;
    }

    public static void setChunkTickingDistance(int chunks) {
        chunkTickingDistance = chunks;
    }

    public static boolean shouldBroadcastChange(ServerPlayerEntity player, Config config) {NotificationState state = NotificationState.getFromPlayer(player, config.getBroadcastChanges());
        if(state == NotificationState.ADDED) {
            return true;
        }
        if(state == NotificationState.REMOVED) {
            return false;
        }
        return switch(config.getBroadcastChangesDefault()) {
            case ALL -> true;
            case NONE -> false;
            case OPS -> AdaptiveViewMod.getServer().getPlayerManager().isOperator(player.getGameProfile());
        };
    }
}
