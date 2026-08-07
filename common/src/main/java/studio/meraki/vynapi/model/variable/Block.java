package studio.meraki.vynapi.model.variable;

import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@VynType(name = "Block")
@SuppressWarnings("unused")
public final class Block {

    private final BlockState blockState;
    private final BlockPos sourceBlock;
    private final Level world;
    private Position position;

    public Block(final BlockPos sourceBlock, final Level world) {
        this.sourceBlock = sourceBlock;
        this.world = world;
        this.blockState = world.getBlockState(sourceBlock);
    }

    @VynFunc
    public String getName() {
        return blockState.getBlock().asItem().toString();
    }

    @VynFunc
    public boolean hasBlockTag(final String tagID) {
        return BuiltInRegistries.BLOCK
                .wrapAsHolder(blockState.getBlock())
                .tags()
                .anyMatch(tag -> {
                    Identifier tagId = tag.location();
                    return (tagId.equals(Identifier.tryParse(tagID))) || tagId.getPath().equalsIgnoreCase(tagID);
                });
    }

    @VynFunc
    public Position getPosition() {
        if (position != null) {
            return position;
        }
        position = new Position(sourceBlock.getX(), sourceBlock.getY(), sourceBlock.getZ());
        return position;
    }

    @VynFunc
    public int getLightBlock() {
        return blockState.getLightDampening();
    }

    @VynFunc
    public int getLightEmission() {
        return blockState.getLightEmission();
    }

    @VynFunc
    public int getSkyDarken() {
        return world.getSkyDarken();
    }

    @VynFunc
    public String instrument() {
        return blockState.instrument().getSoundEvent().getRegisteredName();
    }

    @VynFunc
    public boolean isSolidRender() {
        return blockState.isSolidRender();
    }

    @VynFunc
    public boolean isAir() {
        return blockState.isAir();
    }

    @VynFunc
    public boolean ignitedByLava() {
        return blockState.ignitedByLava();
    }

    @VynFunc
    public boolean isRandomlyTicking() {
        return blockState.isRandomlyTicking();
    }

    @VynFunc
    public boolean canBeReplaced() {
        return blockState.canBeReplaced();
    }

    @VynFunc
    public boolean hasBlockEntity() {
        return blockState.hasBlockEntity();
    }

    @VynFunc
    public boolean canOcclude() {
        return blockState.canOcclude();
    }

    @VynFunc
    public boolean hasLargeCollisionShape() {
        return blockState.hasLargeCollisionShape();
    }

    @VynFunc
    public boolean requiresCorrectToolForDrops() {
        return blockState.requiresCorrectToolForDrops();
    }

    @VynFunc
    public String toString() {
        return blockState.toString();
    }

}