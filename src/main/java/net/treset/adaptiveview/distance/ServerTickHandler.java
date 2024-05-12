package net.treset.adaptiveview.distance;

import net.minecraft.server.MinecraftServer;
import net.treset.adaptiveview.AdaptiveViewMod;
import net.treset.adaptiveview.config.OldConfig;
import net.treset.adaptiveview.tools.MathTools;
import net.treset.adaptiveview.unlocking.LockManager;

import java.util.*;

public class ServerTickHandler {
    private final OldConfig config;
    private final LockManager lockManager;
    private final ViewDistanceHandler viewDistanceHandler;

    private int tickCounter = 0;
    private final List<Long> tickLengths = new ArrayList<>();


    public ServerTickHandler(OldConfig config, LockManager lockManager, ViewDistanceHandler viewDistanceHandler) {
        this.config = config;
        this.lockManager = lockManager;
        this.viewDistanceHandler = viewDistanceHandler;
    }

    public void onTick(MinecraftServer server) {
        if(AdaptiveViewMod.isClient() && !config.isOverrideClient()) {
            return;
        }
        lockManager.onTick();


        tickCounter++;


        if(tickCounter % 100 == 0 || tickCounter % config.getUpdateInterval() == 0) {
            int endValue = server.getTicks() % 100 + 1;
            int startValue = endValue - 1 - (tickCounter - 1) % 100;
            int carry = Math.max(0, -startValue) - 1;
            startValue = Math.max(0, startValue);
            long[] arr = Arrays.copyOfRange(server.getTickTimes(), startValue, endValue);
            for(long e : arr) {
                tickLengths.add(e);
            }
            if(carry != -1) {
                for(long e : Arrays.copyOfRange(server.getTickTimes(), 99 - carry, 100)) {
                    tickLengths.add(e);
                }
            }

            if(tickCounter == config.getUpdateInterval()) {
                tickCounter = 0;
                viewDistanceHandler.updateViewDistance(MathTools.longArrayAverage(tickLengths.toArray(new Long[0])));
                tickLengths.clear();
            }
        }
    }
}
