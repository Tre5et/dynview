package net.treset.adaptiveview.distance;

import net.treset.adaptiveview.AdaptiveViewMod;
import net.treset.adaptiveview.config.OldConfig;

public class ViewDistanceHandler {
    private final OldConfig config;

    public ViewDistanceHandler(OldConfig config) {
        this.config = config;
    }

    public void updateViewDistance(long averageTicks) {
        if(config.getLocked() != 0) return;
        if(averageTicks / 1000000 > config.getMaxMspt()) {
            if(averageTicks / 1000000 > config.getMaxMsptAggressive()) {
                addViewDistance(-2);
            } else addViewDistance(-1);
        } else if(averageTicks / 1000000 < config.getMinMspt()) {
            if(averageTicks / 1000000 < config.getMinMsptAggressive()) {
                addViewDistance(2);
            } else addViewDistance(1);
        }
    }

    public void addViewDistance(int chunks) {
        int vd = Math.max(config.getMinViewDistance(), Math.min(config.getMaxViewDistance(), getViewDistance() + chunks));
        if(vd == getViewDistance()) return;
        setViewDistance(vd);
    }

    public void setViewDistance(int chunks) {
        AdaptiveViewMod.getServer().getPlayerManager().setViewDistance(chunks);
    }

    public int getViewDistance() {
        return AdaptiveViewMod.getServer().getPlayerManager().getViewDistance();
    }

}
