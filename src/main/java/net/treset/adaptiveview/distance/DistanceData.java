package net.treset.adaptiveview.distance;

import net.treset.adaptiveview.config.Rule;
import net.treset.adaptiveview.tools.MathTools;

import java.util.List;

record DistanceData(int maxDistance, int minDistance, int updateRate, int step) {
    public int getTargetDistance(int currentDistance) {
        return MathTools.clamp(currentDistance + step, minDistance, maxDistance);
    }

    public static DistanceData extract(List<Rule> rules, int defaultMax, int defaultMin) {
        int maxDistance = Integer.MAX_VALUE;
        int minDistance = 0;
        int step = 0;
        int updateRate = Integer.MAX_VALUE;
        for(Rule rule : rules) {
            if(rule.getMaxDistance() != null && rule.getMaxDistance() < maxDistance) {
                maxDistance = rule.getMaxDistance();
            }
            if(rule.getMinDistance() != null && rule.getMinDistance() > minDistance) {
                minDistance = rule.getMinDistance();
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

        if(maxDistance == Integer.MAX_VALUE) maxDistance = defaultMax;
        if(minDistance == 0) minDistance = defaultMin;
        if(maxDistance < minDistance) maxDistance = minDistance;

        return new DistanceData(maxDistance, minDistance, updateRate, step);
    }
}


