package studio.meraki.vynapi.mixin.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.meraki.vynapi.handler.script.ScriptHandler;
import studio.meraki.vynapi.model.variable.Position;
import studio.meraki.vynapi.model.variable.Sound;

@Mixin(SoundEngine.class)
public abstract class SoundListenerMixin {

    @Unique
    private final Minecraft client = Minecraft.getInstance();

    @Inject(method = "play", at = @At("TAIL"))
    private void vynapi$play(SoundInstance sound, CallbackInfo ci) {
        if (client.player == null
                || client.level == null
                || sound.getSource() == SoundSource.AMBIENT)
            return;

        ScriptHandler.fireEvent("onPlaySound",
                new Sound(
                        sound.getLocation().toString(),
                        sound.getVolume(),
                        sound.getPitch(),
                        new Position((int) sound.getX(), (int) sound.getY(), (int) sound.getZ())
                )
        );
    }
}