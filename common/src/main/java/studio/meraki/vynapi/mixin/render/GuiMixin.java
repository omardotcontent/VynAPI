package studio.meraki.vynapi.mixin.render;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.meraki.vynapi.handler.other.DebugTextHandler;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow
    public abstract Font getFont();

    @Inject(
            method = "extractRenderState",
            at = @At("TAIL")
    )
    private void debugText(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (DebugTextHandler.getRenderedTexts().isEmpty())
            return;

        int y = 10;
        for (final String string : DebugTextHandler.getRenderedTexts()) {
            graphics.text(getFont(), string, 10, y, 0xFFFFFFFF, false);
            y += 10;
        }
    }
}