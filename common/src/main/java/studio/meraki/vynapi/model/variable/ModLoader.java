package studio.meraki.vynapi.model.variable;

import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.minecraft.client.Minecraft;
import studio.meraki.vynapi.handler.other.ModLoader.ModLoaderHandler;

@VynType(name = "ModLoader")
@SuppressWarnings("unused")
public final class ModLoader {

    private final ModLoaderHandler loader;

    public static ModLoader INSTANCE;

    public ModLoader(final ModLoaderHandler loader) {
        this.loader = loader;
        INSTANCE = this;
    }

    @VynFunc
    public boolean isModLoaded(final String modid) {
        return loader.isModLoaded(modid);
    }

    @VynFunc
    public static boolean isResourcePackLoaded(final String packName) {
        return Minecraft.getInstance().getResourcePackRepository()
                .getSelectedPacks()
                .stream()
                .anyMatch(pack -> pack.getId().equalsIgnoreCase(packName));
    }

    @VynFunc
    public String getRawGameVersion() {
        return loader.getRawGameVersion();
    }
}