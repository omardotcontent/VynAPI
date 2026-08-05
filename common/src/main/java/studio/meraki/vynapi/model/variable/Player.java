package studio.meraki.vynapi.model.variable;

import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@VynType(name = "Player")
@SuppressWarnings("unused")
public final class Player {

    private LocalPlayer player;
    private final Minecraft client;
    private LivingEntity livingEntity;

    public Player(final LocalPlayer player, final Minecraft client) {
        this.player = player;
        this.client = client;
    }

    public void setLivingEntity(final LivingEntity entity) {
        this.livingEntity = entity;
    }

    public LivingEntity getLivingEntity() {
        return livingEntity;
    }

    public void setPlayer(final LocalPlayer player) {
        this.player = player;
    }

    @VynFunc
    public Block getSteppingBlock() {
        if (player == null || client.level == null) {
            return null;
        }
        // TODO(mapping): Yarn getSteppingPos() -> best guess is getOnPos(); double-check against your MC version.
        return new Block(player.getOnPos(), client.level);
    }

    @VynFunc
    public List<Block> getNearbyBlocks(final int blockRadius) {
        if (player == null || client.level == null) {
            return null;
        }
        final List<Block> blocks = new ArrayList<>();
        for (int x = -blockRadius; x <= blockRadius; x++) {
            for (int y = -blockRadius; y <= blockRadius; y++) {
                for (int z = -blockRadius; z <= blockRadius; z++) {
                    final Block block = new Block(player.blockPosition().offset(x, y, z), client.level);
                    if (!Objects.equals(block.getName(), "minecraft:air")) {
                        blocks.add(block);
                    }
                }
            }
        }
        return blocks;
    }

    @VynFunc
    public Block getTargetBlock() {
        if (!(client.hitResult instanceof BlockHitResult blockHit)) {
            return null;
        }
        if (player == null || client.level == null) {
            return null;
        }
        return new Block(blockHit.getBlockPos(), client.level);
    }

    @VynFunc
    public String getGameMode() {
        // TODO(mapping): LocalPlayer doesn't expose game mode directly in vanilla Mojang mappings.
        // Vanilla only tracks it via Minecraft#gameMode (MultiPlayerGameMode#getPlayerMode() -> GameType).
        // Verify this against your version; GameType has no asString(), use .getName() or .name() instead.
        return (player != null && client.gameMode != null && client.gameMode.getPlayerMode() != null)
                ? client.gameMode.getPlayerMode().getName()
                : null;
    }

    @VynFunc
    public Position getPosition() {
        return (player != null) ? new Position((int) player.getX(), (int) player.getY(), (int) player.getZ()) : null;
    }

    @VynFunc
    public World getWorld() {
        return (client.level != null) ? new World(client.level) : null;
    }

    @VynFunc
    public double getHealth() {
        return (player != null) ? player.getHealth() : -1;
    }

    @VynFunc
    public double getFoodLevel() {
        return (player != null) ? player.getFoodData().getFoodLevel() : -1;
    }

    @VynFunc
    public double getSaturationLevel() {
        return (player != null) ? player.getFoodData().getSaturationLevel() : -1;
    }

    @VynFunc
    public float portalEffectIntensity() {
        // TODO(mapping): exact Mojang field name for Yarn's nauseaIntensity is unconfirmed - verify.
        return player != null ? player.portalEffectIntensity : 0.0f;
    }

    @VynFunc
    public int getExperienceLevel() {
        return player != null ? player.experienceLevel : -1;
    }

    @VynFunc
    public int getItemUseTime() {
        return (livingEntity != null) ? livingEntity.getTicksUsingItem() : -1;
    }

    @VynFunc
    public int getItemUseTimeLeft() {
        return (livingEntity != null) ? livingEntity.getUseItemRemainingTicks() : -1;
    }

    @VynFunc
    public int getArmor() {
        return (livingEntity != null) ? livingEntity.getArmorValue() : -1;
    }

    @VynFunc
    public double getEyePosX() {
        return (livingEntity != null) ? livingEntity.getEyePosition().x : 0;
    }

    @VynFunc
    public double getEyePosY() {
        return (livingEntity != null) ? livingEntity.getEyePosition().y : 0;
    }

    @VynFunc
    public double getEyePosZ() {
        return (livingEntity != null) ? livingEntity.getEyePosition().z : 0;
    }

    @VynFunc
    public double getHeadYaw() {
        return (livingEntity != null) ? livingEntity.getYHeadRot() : 0;
    }

    @VynFunc
    public double getDamageTiltYaw() {
        // TODO(mapping): Mojang equivalent of Yarn's getDamageTiltYaw() is unconfirmed - verify against your version.
        return (livingEntity != null) ? livingEntity.getHurtDir() : 0;
    }

    @VynFunc
    public double maxUpStep() {
       return (livingEntity != null) ? livingEntity.maxUpStep() : 0;
    }

    @VynFunc
    public double getExperienceProgress() {
        return player != null ? player.experienceProgress : -1f;
    }

    @VynFunc
    public double getVelocityX() {
        if (player == null) {
            return 0;
        }
        final double vx = player.getDeltaMovement().x;
        final double vz = player.getDeltaMovement().z;
        final double yawRad = Math.toRadians(player.getYRot());
        return (vx * Math.cos(yawRad) + vz * Math.sin(yawRad)) * 0.3333;
    }

    @VynFunc
    public double getVelocityY() {
        if (player == null) return 0;
        final double y = player.getDeltaMovement().y;
        return (Math.abs(y + 0.0784) < 0.001) ? 0 : y * 0.3333;
    }

    @VynFunc
    public double getVelocityZ() {
        if (player == null) {
            return 0;
        }
        final double vx = player.getDeltaMovement().x;
        final double vz = player.getDeltaMovement().z;
        final double yawRad = Math.toRadians(player.getYRot());
        return (-vx * Math.sin(yawRad) + vz * Math.cos(yawRad)) * 0.3333;
    }

    @VynFunc
    public boolean isSwimming() {
        return player != null && player.isSwimming();
    }

    @VynFunc
    public boolean isSprinting() {
        return player != null && player.isSprinting();
    }

    @VynFunc
    public boolean isInLava() {
        return player != null && player.isInLava();
    }

    @VynFunc
    public boolean isInFluid() {
        // TODO(mapping): Yarn's generic isInFluid() has no confirmed vanilla Mojang equivalent - verify.
        return player != null && player.isInLiquid();
    }

    @VynFunc
    public boolean isOnFire() {
        return player != null && player.isOnFire();
    }

    @VynFunc
    public boolean isTouchingWater() {
        return player != null && player.isInWater();
    }

    @VynFunc
    public boolean isClimbing() {
        return player != null && player.onClimbable();
    }

    @VynFunc
    public boolean isDescending() {
        // TODO(mapping): unconfirmed - verify against your version.
        return player != null && player.isDescending();
    }

    @VynFunc
    public boolean isVisuallyCrawling() {
        // TODO(mapping): unconfirmed - possibly isVisuallyCrawling() - verify against your version.
        return player != null && player.isVisuallyCrawling();
    }

    @VynFunc
    public boolean isBlocking() {
        return player != null && player.isBlocking();
    }

    @VynFunc
    public boolean isRiding() {
        return player != null && player.isPassenger();
    }

    @VynFunc
    public boolean isPushable() {
        return player != null && player.isPushable();
    }

    @VynFunc
    public boolean isPushedByFluids() {
        return player != null && player.isPushedByFluid();
    }

    @VynFunc
    public boolean isInvulnerable() {
        return player != null && player.isInvulnerable();
    }

    @VynFunc
    public boolean isUsingItem() {
        return player != null && player.isUsingItem();
    }

    @VynFunc
    public boolean isUsingRiptide() {
        return player != null && player.isAutoSpinAttack();
    }

    @VynFunc
    public boolean isFrozen() {
        return player != null && player.isFullyFrozen();
    }

    @VynFunc
    public boolean isCurrentlyGlowing() {
        return player != null && player.isCurrentlyGlowing();
    }

    @VynFunc
    public boolean isAutoJumpEnabled() {
        // TODO(mapping): unconfirmed - verify against your version.
        return player != null && player.isAutoJumpEnabled();
    }

    @VynFunc
    public boolean isSuppressingSlidingDownLadder() {
        // TODO(mapping): unconfirmed - possibly redundant with onClimbable() - verify against your version.
        return player != null && player.isSuppressingSlidingDownLadder();
    }

    @VynFunc
    public boolean getDoLimitedCrafting() {
        // TODO(mapping): unconfirmed - verify against your version.
        return player != null && player.getDoLimitedCrafting();
    }

    @VynFunc
    public boolean isInsideWall() {
        return player != null && player.isInWall();
    }

    @VynFunc
    public boolean isJumping() {
        // TODO(mapping): unconfirmed - verify against your version.
        return player != null && player.jump();
    }

    @VynFunc
    public boolean isOnRail() {
        // TODO(mapping): unconfirmed - verify against your version.
        return player != null && player.isOnRail();
    }

    @VynFunc
    public boolean isFireImmune() {
        // TODO(mapping): Mojang commonly exposes this without the "is" prefix as fireImmune() - verify.
        return player != null && player.isFireImmune();
    }


    @VynFunc
    public boolean isSubmergedInWater() {
        return player != null && player.isUnderWater();
    }

    @VynFunc
    public boolean isSneaking() {
        return player != null && player.isShiftKeyDown();
    }

    @VynFunc
    public boolean isOnGround() {
        return player != null && player.onGround();
    }

    @VynFunc
    public boolean showsDeathScreen() {
        return player != null && player.shouldShowDeathScreen();
    }

    @VynFunc
    public boolean isHorizontalCollision() {
        return player != null && player.horizontalCollision;
    }

    @VynFunc
    public boolean isInSneakingPose() {
        return player != null && player.isCrouching();
    }

    @VynFunc
    public boolean shouldSlowDown() {
        // TODO(mapping): unconfirmed - verify against your version.
        return player != null && player.shouldSlowDown();
    }

    @VynFunc
    public boolean isRidingJumpable() {
        // TODO(mapping): unconfirmed - possibly jumpableVehicle() - verify against your version.
        return player != null && player.getJumpingMount() != null;
    }

    @VynFunc
    public float getMountJumpStrength() {
        // TODO(mapping): unconfirmed - verify against your version.
        return player != null ? player.getMountJumpStrength() : 0.0f;
    }

    @VynFunc
    public boolean isCamera() {
        return player != null && client.getCameraEntity() == player;
    }

    @VynFunc
    public int getPermissionLevel() {
        return player != null ? player.getPermissionLevel() : 0;
    }

    @VynFunc
    public boolean isMainPlayer() {
        // TODO(mapping): unconfirmed - may not have a direct vanilla equivalent, verify against your version.
        return player != null && player.isMainPlayer();
    }

    @VynFunc
    public float getMoodPercentage() {
        // TODO(mapping): unconfirmed and fairly obscure - verify against your version.
        return player != null ? player.getMoodPercentage() : 0.0f;
    }


    @VynFunc
    public boolean canBreatheInWater() {
        return livingEntity != null && livingEntity.canBreatheInWater();
    }

    @VynFunc
    public boolean hasLandedInFluid() {
        // TODO(mapping): unconfirmed - verify against your version.
        return livingEntity != null && livingEntity.hasLandedInFluid();
    }

    @VynFunc
    public boolean isBaby() {
        return livingEntity != null && livingEntity.isBaby();
    }

    @VynFunc
    public float getAgeScale() {
        // TODO(mapping): unconfirmed - possibly getAgeScale() - verify against your version.
        return livingEntity != null ? livingEntity.getAgeScale() : 1.0f;
    }

    @VynFunc
    public float getScale() {
        return livingEntity != null ? livingEntity.getScale() : 1.0f;
    }

    @VynFunc
    public boolean isDead() {
        return livingEntity != null && livingEntity.isDeadOrDying();
    }

    @VynFunc
    public float getLuck() {
        return livingEntity != null ? livingEntity.getLuck() : 0.0f;
    }

    @VynFunc
    public float getAbsorptionAmount() {
        return livingEntity != null ? livingEntity.getAbsorptionAmount() : 0.0f;
    }

    @VynFunc
    public float getMaxAbsorption() {
        // TODO(mapping): unconfirmed - verify against your version.
        return livingEntity != null ? livingEntity.getMaxAbsorption() : 0.0f;
    }

    @VynFunc
    public int getStuckArrowCount() {
        return livingEntity != null ? livingEntity.getArrowCount() : 0;
    }

    @VynFunc
    public int getStingerCount() {
        return livingEntity != null ? livingEntity.getStingerCount() : 0;
    }

    @VynFunc
    public void playSound(final String soundId, final double volume, final double pitch) {
        if (player != null) {
            player.playSound(BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(soundId)), (float) volume, (float) pitch);
        }
    }

    @VynFunc
    public void playSound(final Sound sound) {
        if (player == null) {
            return;
        }
        player.playSound(BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(sound.getName())), (float) sound.getVolume(), (float) sound.getPitch());
    }

    @VynFunc
    public void playSoundWorld(final Position position, final String soundId, final double volume, final double pitch) {
        if (player == null || client.level == null) {
            return;
        }
        client.level.playLocalSound(
                position.getX(),
                position.getY(),
                position.getZ(),
                BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(soundId)).get().value(),
                SoundSource.BLOCKS,
                (float) volume,
                (float) pitch,
                true);
    }

    @VynFunc
    public void playSoundWorld(final Position position, final Sound sound) {
        if (player == null || client.level == null) {
            return;
        }
        client.level.playLocalSound(
                position.getX(),
                position.getY(),
                position.getZ(),
                BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(sound.getName())).get().value(),
                SoundSource.BLOCKS,
                (float) sound.getVolume(),
                (float) sound.getPitch(),
                true);
    }

    @VynFunc
    public void sendMessage(final String message) {
        if (player != null)
            player.displayClientMessage(Component.literal(message), false);
    }

    public LocalPlayer getPlayer() {
        return player;
    }
}