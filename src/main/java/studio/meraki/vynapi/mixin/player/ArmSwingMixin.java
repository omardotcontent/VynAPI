package studio.meraki.vynapi.mixin.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.meraki.vynapi.handler.client.InteractionHandler;

@Mixin(ClientPlayerEntity.class)
public abstract class ArmSwingMixin {

    @Unique
    private final MinecraftClient client = MinecraftClient.getInstance();

    @Inject(method = "swingHand", at = @At("HEAD"))
    private void interactivestuff$swingHand(final Hand hand, final CallbackInfo ci) {
        InteractionHandler.handleBlockInteraction(client, hand);
    }
}