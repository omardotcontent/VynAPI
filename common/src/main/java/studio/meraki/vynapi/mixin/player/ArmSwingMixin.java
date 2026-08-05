package studio.meraki.vynapi.mixin.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.meraki.vynapi.handler.client.InteractionHandler;

@Mixin(LocalPlayer.class)
public abstract class ArmSwingMixin {



    @Inject(method = "swing", at = @At("HEAD"))
    private void vynapi$swingHand(InteractionHand p_108660_, CallbackInfo ci) {
        InteractionHandler.handleBlockInteraction(Minecraft.getInstance());
    }
}