package studio.meraki.vynapi.handler.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Hand;
import studio.meraki.vynapi.handler.script.ScriptInterpreter;

public final class InteractionHandler {

    private static final String COOLDOWN_ID = "SwingSoundCooldown";

    private InteractionHandler() {
    }

    public static void handleBlockInteraction(final MinecraftClient client, final Hand hand) {
        if (BackgroundLoopHandler.isLoopRunning(COOLDOWN_ID))
            return;

        final ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }

        final StatusEffectInstance haste = player.getStatusEffect(StatusEffects.HASTE);
        final StatusEffectInstance conduit = player.getStatusEffect(StatusEffects.CONDUIT_POWER);
        final int amplifier = (haste != null ? haste.getAmplifier() + 1 : 0) + (conduit != null ? conduit.getAmplifier() + 1 : 0);

        BackgroundLoopHandler.waitTicks(COOLDOWN_ID, Math.max(0, 4 - amplifier), () -> {});

        ScriptInterpreter.onSwingHand();
    }
}