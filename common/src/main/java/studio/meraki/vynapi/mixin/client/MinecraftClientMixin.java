package studio.meraki.vynapi.mixin.client;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.meraki.vynapi.handler.client.BackgroundLoopHandler;
import studio.meraki.vynapi.handler.other.DebugTextHandler;
import studio.meraki.vynapi.handler.script.ScriptHandler;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {


    @Inject(method = "tick", at = @At("TAIL"))
    private void onTickEnd(final CallbackInfo ci) {
        final Minecraft vynAPI$client = Minecraft.getInstance();

        DebugTextHandler.onTickEnd();

        if (vynAPI$client.level == null || vynAPI$client.isPaused())
            return;

        BackgroundLoopHandler.tickAll();
    }


    @Inject(method = "tick", at = @At("HEAD"))
    private void onTickStart(final CallbackInfo ci) {
        final Minecraft vynAPI$client = Minecraft.getInstance();
        ScriptHandler.tick(vynAPI$client);
    }
}