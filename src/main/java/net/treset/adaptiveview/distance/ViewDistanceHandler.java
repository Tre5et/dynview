package net.treset.adaptiveview.distance;

import net.treset.adaptiveview.AdaptiveViewMod;
import net.treset.adaptiveview.config.Config;
import net.treset.adaptiveview.config.Rule;
import net.treset.adaptiveview.config.ServerState;
import net.treset.adaptiveview.tools.MathTools;
import net.treset.adaptiveview.tools.TextTools;

import java.util.ArrayList;
import java.util.List;

public class ViewDistanceHandler {
    private final Config config;

    public ViewDistanceHandler(Config config) {
        this.config = config;
    }

    public int updateViewDistance(ServerState state) {
        ArrayList<Rule> activeRules = new ArrayList<>();
        ArrayList<Integer> activeIndexes = new ArrayList<>();
        for(int i = 0; i < config.getRules().size(); i++) {
            Rule rule = config.getRules().get(i);
            if(rule.applies(state)) {
                activeRules.add(rule);
                activeIndexes.add(i + 1);
            }
        }

        int maxViewDistance = Integer.MAX_VALUE;
        int minViewDistance = 0;
        int updateRate = Integer.MAX_VALUE;
        int step = 0;
        for(Rule rule : activeRules) {
            if(rule.getMaxViewDistance() != null && rule.getMaxViewDistance() < maxViewDistance) {
                maxViewDistance = rule.getMaxViewDistance();
            }
            if(rule.getMinViewDistance() != null && rule.getMinViewDistance() > minViewDistance) {
                minViewDistance = rule.getMinViewDistance();
            }
            if(rule.getUpdateRate() != null && rule.getUpdateRate() < updateRate) {
                updateRate = rule.getUpdateRate();
            }
            if(rule.getStep() != null) {
                rule.incrementCounter();
                if(rule.getStep() < 0 && rule.getStep() < step) {
                    step = rule.getStep();
                } else if(rule.getStep() > 0 && rule.getStep() > step) {
                    step = rule.getStep();
                }
            }
        }

        if(maxViewDistance == Integer.MAX_VALUE) {
            maxViewDistance = config.getMaxViewDistance();
        }
        if(minViewDistance == 0) {
            minViewDistance = config.getMinViewDistance();
        }
        if(maxViewDistance < minViewDistance) {
            maxViewDistance = minViewDistance;
        }
        if(updateRate == Integer.MAX_VALUE) {
            updateRate = config.getUpdateRate();
        }

        int targetViewDistance = MathTools.clamp(state.getCurrentViewDistance() + step, minViewDistance, maxViewDistance);

        if(targetViewDistance != state.getCurrentViewDistance()) {
            TextTools.sendMessage((p) -> {
                if(config.isBroadcastToOps() && AdaptiveViewMod.getServer().getPlayerManager().isOperator(p.getGameProfile())) {
                    return true;
                } else return TextTools.containsIgnoreCase(config.getBroadcastTo(), p.getName().getString());
            }, "$N$i[AdaptiveView] Changed View Distance from %d to %d because of %s.", state.getCurrentViewDistance(), targetViewDistance, getRuleCauseString(activeIndexes));
            setViewDistance(targetViewDistance);
        }

        return updateRate;
    }

    private String getRuleCauseString(List<Integer> activeIndexes) {
        if(activeIndexes.isEmpty()) {
            return "no Rules";
        }
        if(activeIndexes.size() == 1) {
            return "Rule " + activeIndexes.get(0);
        }
        StringBuilder sb = new StringBuilder("Rules ");
        sb.append(activeIndexes.get(0));
        for(int i = 1; i < activeIndexes.size() - 1; i++) {
            sb.append(", ").append(activeIndexes.get(i));
        }
        sb.append(" and ").append(activeIndexes.get(activeIndexes.size() - 1));
        return sb.toString();
    }

    public void setViewDistance(int chunks) {
        if(!config.isLocked()) {
            AdaptiveViewMod.getServer().getPlayerManager().setViewDistance(chunks);
        }
    }

    public static int getViewDistance() {
        return AdaptiveViewMod.getServer().getPlayerManager().getViewDistance();
    }
}
