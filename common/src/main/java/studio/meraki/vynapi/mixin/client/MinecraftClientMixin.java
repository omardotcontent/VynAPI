package studio.meraki.vynapi.mixin.client;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.meraki.vynapi.handler.client.BackgroundLoopHandler;
import studio.meraki.vynapi.handler.other.DebugTextHandler;
@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTickEnd(final CallbackInfo ci) {
        final Minecraft client = (Minecraft) (Object) this;

        DebugTextHandler.onTickEnd();

        if (client.level == null || client.isPaused())
            return;

        BackgroundLoopHandler.tickAll();
    }
}