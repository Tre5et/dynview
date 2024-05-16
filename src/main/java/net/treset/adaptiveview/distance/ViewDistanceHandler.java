package net.treset.adaptiveview.distance;

import net.treset.adaptiveview.AdaptiveViewMod;
import net.treset.adaptiveview.config.Config;
import net.treset.adaptiveview.config.Rule;
import net.treset.adaptiveview.config.ServerState;
import net.treset.adaptiveview.tools.MathTools;

import java.util.ArrayList;

public class ViewDistanceHandler {
    private final Config config;

    public ViewDistanceHandler(Config config) {
        this.config = config;
    }

    public int updateViewDistance(ServerState state) {
        ArrayList<Rule> activeRules = new ArrayList<>();
        for(Rule rule : config.getRules()) {
            if(rule.applies(state)) {
                activeRules.add(rule);
                //TODO: remove
                System.out.println(rule);
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

        // TODO: remove
        System.out.println(targetViewDistance + ", " + step);
        setViewDistance(targetViewDistance);

        return updateRate;
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
