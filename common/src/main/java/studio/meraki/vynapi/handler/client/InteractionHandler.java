package studio.meraki.vynapi.handler.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import studio.meraki.vynapi.handler.script.ScriptHandler;

public final class InteractionHandler {

    private static final String COOLDOWN_ID = "SwingSoundCooldown";

    private InteractionHandler() {
    }

    public static void handleBlockInteraction(final Minecraft client) {
        if (BackgroundLoopHandler.isLoopRunning(COOLDOWN_ID)) return;

        final LocalPlayer player = client.player;
        if (player == null) return;

        final MobEffectInstance haste = player.getEffect(MobEffects.HASTE);
        final MobEffectInstance conduit = player.getEffect(MobEffects.CONDUIT_POWER);
        final int amplifier = (haste != null ? haste.getAmplifier() + 1 : 0) + (conduit != null ? conduit.getAmplifier() + 1 : 0);

        BackgroundLoopHandler.waitTicks(COOLDOWN_ID, Math.max(0, 4 - amplifier), () -> {});

        ScriptHandler.fireEvent("onSwingHand");
    }
}