package studio.meraki.vynapi.mixin.client;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.meraki.vynapi.handler.other.DebugTextHandler;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTickEnd(final CallbackInfo ci) {
        DebugTextHandler.onTickEnd();
    }
}