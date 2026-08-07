package studio.meraki.vynapi.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.meraki.vynapi.handler.other.DebugTextHandler;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow
    @Final
    Minecraft minecraft;

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Gui;render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void renderDebugText(
            final DeltaTracker deltaTracker,
            final boolean renderLevel,
            final CallbackInfo ci,
            @Local final GuiGraphics guiGraphics
    ) {
        if (DebugTextHandler.getRenderedTexts().isEmpty() || this.minecraft.options.hideGui)
            return;

        final Font font = this.minecraft.font;
        int y = 10;
        for (final String string : DebugTextHandler.getRenderedTexts()) {
            guiGraphics.drawString(font, string, 10, y, 0xFFFFFFFF, false);
            y += 10;
        }
    }
}