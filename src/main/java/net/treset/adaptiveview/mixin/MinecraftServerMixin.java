package net.treset.adaptiveview.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.MinecraftServer;
import net.treset.adaptiveview.distance.ServerHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method="tick(Ljava/util/function/BooleanSupplier;)V", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci, @Local(ordinal = 1) long delta) {
        ServerHandler.getTickLengths().add(delta);
    }
}
