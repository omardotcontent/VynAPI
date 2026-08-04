package studio.meraki.vynapi.mixin.render;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.meraki.vynapi.handler.other.DebugTextHandler;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Inject(
            method = "render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V",
            at = @At("TAIL")
    )
    private void debugText(final DrawContext context, final RenderTickCounter tickCounter, final CallbackInfo ci) {
        if (DebugTextHandler.getRenderedTexts().isEmpty())
            return;

        int y = 10;
        for (final String string : DebugTextHandler.getRenderedTexts()) {
            context.drawText(getTextRenderer(), string, 10, y, 0xFFFFFFFF, false);
            y += 10;
        }
    }
}