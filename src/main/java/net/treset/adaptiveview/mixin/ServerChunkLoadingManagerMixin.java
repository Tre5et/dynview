package net.treset.adaptiveview.mixin;

import net.minecraft.server.world.ServerChunkLoadingManager;
import net.treset.adaptiveview.distance.ViewDistanceHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ServerChunkLoadingManager.class)
public class ServerChunkLoadingManagerMixin {
    @ModifyConstant(method = "canTickChunk(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/util/math/ChunkPos;)Z", constant = @Constant(doubleValue = 16384.0))
    private double getMaxDistance(double value) {
        // Max 8 chunks, set in net/minecraft/server/world/ChunkTicketManager::distanceFromNearestPlayerTracker
        return Math.pow(ViewDistanceHandler.getChunkTickingDistance() * 16, 2);
    }
}
