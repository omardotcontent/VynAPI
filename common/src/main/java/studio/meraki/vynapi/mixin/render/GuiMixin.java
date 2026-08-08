package studio.meraki.vynapi.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.meraki.vynapi.handler.other.DebugTextHandler;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Final
    @Shadow
    private Minecraft minecraft;

    @Inject(
            method = "extractRenderState",
            at = @At("TAIL")
    )
    private void debugText(DeltaTracker deltaTracker, boolean shouldRenderLevel, boolean resourcesLoaded, CallbackInfo ci,
                           @Local(name = "graphics") GuiGraphicsExtractor graphics) {
        if (DebugTextHandler.getRenderedTexts().isEmpty())
            return;

        int y = 10;
        for (final String string : DebugTextHandler.getRenderedTexts()) {
            graphics.text(this.minecraft.font, string, 10, y, 0xFFFFFFFF, false);
            y += 10;
        }
    }
}