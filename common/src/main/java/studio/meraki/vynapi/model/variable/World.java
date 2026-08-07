package studio.meraki.vynapi.model.variable;

import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Unique;

import java.util.Objects;

@VynType(name = "World")
@SuppressWarnings("unused")
public final class World {

    private final Level sourceWorld;

    public World(final Level sourceWorld) {
        this.sourceWorld = sourceWorld;
    }

    @VynFunc
    public Block getBlock(final int x, final int y, final int z) {
        return new Block(new BlockPos(x, y, z), sourceWorld);
    }

    @VynFunc
    public String getDimension() {
        return sourceWorld.dimension().identifier().toString();
    }

    @VynFunc
    public boolean isBrightOutside() {
        return sourceWorld.isBrightOutside();
    }

    @VynFunc
    public long getDayTime() {
        return sourceWorld.getDefaultClockTime();
    }

    @VynFunc
    public long getGameTime() {
        return sourceWorld.getGameTime();
    }

    @VynFunc
    public long calculateDistanceBetweenPositions(final Position pos1, final Position pos2) {
        return pos1.getDistanceTo(pos2);
    }

    @VynFunc
    public String getBiomeAt(final int x, final int y, final int z) {
        return getBiomeId(new BlockPos(x, y, z));
    }

    @VynFunc
    public String getBiomeAt(final Position position) {
        return getBiomeId(new BlockPos(position.getX(), position.getY(), position.getZ()));
    }

    @Unique
    private String getBiomeId(final BlockPos pos) {
        return sourceWorld.getBiome(pos)
                .unwrapKey()
                .map(key -> key.identifier().toString())
                .orElse(null);
    }

    @VynFunc
    public int getGrassColor(final Position position) {
        return BiomeColors.getAverageGrassColor((BlockAndTintGetter) sourceWorld, new BlockPos(position.getX(), position.getY(), position.getZ()));
    }

    @VynFunc
    public int getFoliageColor(final Position position) {
        return BiomeColors.getAverageFoliageColor((BlockAndTintGetter) sourceWorld, new BlockPos(position.getX(), position.getY(), position.getZ()));
    }

    @VynFunc
    public int getWaterColor(final Position position) {
        return BiomeColors.getAverageWaterColor((BlockAndTintGetter) sourceWorld, new BlockPos(position.getX(), position.getY(), position.getZ()));
    }

    @VynFunc
    public String toString() {
        return "World{dimension=" + getDimension() + ", isBrightOutside=" + isBrightOutside() + ", DayTime=" + getDayTime() + ", GameTime=" + getGameTime() + "}";
    }

}