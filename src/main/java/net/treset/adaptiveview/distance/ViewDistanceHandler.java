package net.treset.adaptiveview.distance;

import net.minecraft.server.network.ServerPlayerEntity;
import net.treset.adaptiveview.AdaptiveViewMod;
import net.treset.adaptiveview.config.Config;
import net.treset.adaptiveview.config.Rule;
import net.treset.adaptiveview.config.RuleTarget;
import net.treset.adaptiveview.config.ServerState;
import net.treset.adaptiveview.tools.NotificationState;
import net.treset.adaptiveview.tools.TextTools;

import java.util.ArrayList;
import java.util.List;

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

        for(int i = 0; i < config.getRules().size(); i++) {
            Rule rule = config.getRules().get(i);
            if(rule.applies(state)) {
                if(rule.getTarget() == RuleTarget.SIMULATION) {
                    simDistanceRules.add(rule);
                    simDistanceIndexes.add(i + 1);
                } else {
                    viewDistanceRules.add(rule);
                    viewDistanceIndexes.add(i + 1);
                }
            }
        }

        DistanceData viewDistanceData = DistanceData.extract(viewDistanceRules, config.getMaxViewDistance(), config.getMinViewDistance());
        DistanceData simDistanceData = DistanceData.extract(simDistanceRules, config.getMaxSimDistance(), config.getMinSimDistance());

        int targetViewDistance = viewDistanceData.getTargetDistance(state.getCurrentViewDistance());
        if(targetViewDistance != state.getCurrentViewDistance() && !config.isViewLocked()) {
            TextTools.broadcastIf((p) -> shouldBroadcastChange(p, config), "Changed View Distance from %d to %d because of %s.", state.getCurrentViewDistance(), targetViewDistance, getRuleCauseString(viewDistanceIndexes));
            setViewDistance(targetViewDistance);
        }

        int targetSimDistance = simDistanceData.getTargetDistance(state.getCurrentSimDistance());
        if(targetSimDistance != state.getCurrentSimDistance() && !config.isSimLocked()) {
            TextTools.broadcastIf((p) -> shouldBroadcastChange(p, config), "Changed Simulation Distance from %d to %d because of %s.", state.getCurrentSimDistance(), targetSimDistance, getRuleCauseString(simDistanceIndexes));
            setSimDistance(targetSimDistance);
        }

        int updateRate = Math.min(viewDistanceData.updateRate(), simDistanceData.updateRate());
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

    public void setViewDistance(int chunks) {
        AdaptiveViewMod.getServer().getPlayerManager().setViewDistance(chunks);
    }

    public void setSimDistance(int chunks) {
        AdaptiveViewMod.getServer().getPlayerManager().setSimulationDistance(chunks);
    }

    public static int getViewDistance() {
        return AdaptiveViewMod.getServer().getPlayerManager().getViewDistance();
    }

    public static int getSimDistance() {
        return AdaptiveViewMod.getServer().getPlayerManager().getSimulationDistance();
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
