package studio.meraki.vynapi.model.variable;

import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
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
    private LivingEntity livingEntity;

    public Player() {
        this(null);
    }

    public Player(final LocalPlayer player) {
        this.player = player;
    }

    public Player(final LocalPlayer player, final Minecraft client) {
        this.player = player;
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

    /**
     * Backing living entity for LivingEntity-backed functions. LocalPlayer is itself a
     * LivingEntity, so fall back to it whenever no explicit entity was set - this keeps
     * every LivingEntity getter functional instead of silently returning defaults.
     */
    private LivingEntity living() {
        return livingEntity != null ? livingEntity : player;
    }

    private static Minecraft client() {
        return Minecraft.getInstance();
    }

    // ------------------------------------------------------------------ blocks / world

    @VynFunc
    public Block getSteppingBlock() {
        final Minecraft client = client();
        if (player == null || client == null || client.level == null) {
            return null;
        }
        return new Block(player.getOnPos(), client.level);
    }

    @VynFunc
    public List<Block> getNearbyBlocks(final int blockRadius) {
        final Minecraft client = client();
        if (player == null || client == null || client.level == null) {
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
        final Minecraft client = client();
        if (client == null || !(client.hitResult instanceof BlockHitResult blockHit)) {
            return null;
        }
        if (player == null || client.level == null) {
            return null;
        }
        return new Block(blockHit.getBlockPos(), client.level);
    }

    @VynFunc
    public World getWorld() {
        final Minecraft client = client();
        return (client != null && client.level != null) ? new World(client.level) : null;
    }

    // ------------------------------------------------------------------ identity / game state

    @VynFunc
    public String getName() {
        return (player != null) ? player.getName().getString() : null;
    }

    @VynFunc
    public String getUUID() {
        return (player != null) ? player.getStringUUID() : null;
    }

    @VynFunc
    public String getGameMode() {
        final Minecraft client = client();
        return (player != null && client != null && client.gameMode != null && client.gameMode.getPlayerMode() != null)
                ? client.gameMode.getPlayerMode().getName()
                : null;
    }

    @VynFunc
    public boolean isCamera() {
        final Minecraft client = client();
        return player != null && client != null && client.getCameraEntity() == player;
    }

    @VynFunc
    public boolean isLocalPlayer() {
        return player != null && player.isLocalPlayer();
    }

    @VynFunc
    public boolean isMainPlayer() {
        return isLocalPlayer();
    }

    @VynFunc
    public int getPermissionLevel() {
        return player != null ? player.getPermissionLevel() : 0;
    }

    @VynFunc
    public boolean isSpectator() {
        return player != null && player.isSpectator();
    }

    // ------------------------------------------------------------------ position / rotation

    @VynFunc
    public Position getPosition() {
        return (player != null) ? new Position((int) player.getX(), (int) player.getY(), (int) player.getZ()) : null;
    }

    @VynFunc
    public double getX() {
        return (player != null) ? player.getX() : 0;
    }

    @VynFunc
    public double getY() {
        return (player != null) ? player.getY() : 0;
    }

    @VynFunc
    public double getZ() {
        return (player != null) ? player.getZ() : 0;
    }

    @VynFunc
    public double getEyePosX() {
        return (living() != null) ? living().getEyePosition().x : 0;
    }

    @VynFunc
    public double getEyePosY() {
        return (living() != null) ? living().getEyePosition().y : 0;
    }

    @VynFunc
    public double getEyePosZ() {
        return (living() != null) ? living().getEyePosition().z : 0;
    }

    @VynFunc
    public float getYaw() {
        return (player != null) ? player.getYRot() : 0.0f;
    }

    @VynFunc
    public float getPitch() {
        return (player != null) ? player.getXRot() : 0.0f;
    }

    @VynFunc
    public double getHeadYaw() {
        return (living() != null) ? living().getYHeadRot() : 0;
    }

    @VynFunc
    public float getBodyYaw() {
        return (living() != null) ? living().yBodyRot : 0.0f;
    }

    @VynFunc
    public float getWidth() {
        return (player != null) ? player.getBbWidth() : 0.0f;
    }

    @VynFunc
    public float getHeight() {
        return (player != null) ? player.getBbHeight() : 0.0f;
    }

    @VynFunc
    public double getFallDistance() {
        return (player != null) ? player.fallDistance : 0.0f;
    }

    @VynFunc
    public double maxUpStep() {
        return (living() != null) ? living().maxUpStep() : 0;
    }

    // ------------------------------------------------------------------ velocity / movement

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
    public float getSpeed() {
        return (living() != null) ? living().getSpeed() : 0.0f;
    }

    // ------------------------------------------------------------------ vitals

    @VynFunc
    public double getHealth() {
        return (player != null) ? player.getHealth() : -1;
    }

    @VynFunc
    public float getMaxHealth() {
        return (living() != null) ? living().getMaxHealth() : -1.0f;
    }

    @VynFunc
    public float getAbsorptionAmount() {
        return (living() != null) ? living().getAbsorptionAmount() : 0.0f;
    }

    @VynFunc
    public float getMaxAbsorption() {
        return (living() != null) ? living().getMaxAbsorption() : 0.0f;
    }

    @VynFunc
    public boolean isAlive() {
        return living() != null && living().isAlive();
    }

    @VynFunc
    public boolean isDead() {
        return living() != null && living().isDeadOrDying();
    }

    @VynFunc
    public int getHurtTime() {
        return (living() != null) ? living().hurtTime : 0;
    }

    @VynFunc
    public int getDeathTime() {
        return (living() != null) ? living().deathTime : 0;
    }

    @VynFunc
    public double getDamageTiltYaw() {
        return (living() != null) ? living().getHurtDir() : 0;
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
    public int getAirSupply() {
        return (player != null) ? player.getAirSupply() : -1;
    }

    @VynFunc
    public int getMaxAirSupply() {
        return (player != null) ? player.getMaxAirSupply() : -1;
    }

    @VynFunc
    public int getArmor() {
        return (living() != null) ? living().getArmorValue() : -1;
    }

    @VynFunc
    public float getArmorCoverPercentage() {
        return (living() != null) ? living().getArmorCoverPercentage() : 0.0f;
    }

    @VynFunc
    public float getLuck() {
        return (living() != null) ? living().getLuck() : 0.0f;
    }

    @VynFunc
    public int getStuckArrowCount() {
        return (living() != null) ? living().getArrowCount() : 0;
    }

    @VynFunc
    public int getStingerCount() {
        return (living() != null) ? living().getStingerCount() : 0;
    }

    // ------------------------------------------------------------------ experience

    @VynFunc
    public int getExperienceLevel() {
        return player != null ? player.experienceLevel : -1;
    }

    @VynFunc
    public double getExperienceProgress() {
        return player != null ? player.experienceProgress : -1f;
    }

    @VynFunc
    public int getTotalExperience() {
        return player != null ? player.totalExperience : -1;
    }

    // ------------------------------------------------------------------ movement state booleans

    @VynFunc
    public boolean isOnGround() {
        return player != null && player.onGround();
    }

    @VynFunc
    public boolean isSprinting() {
        return player != null && player.isSprinting();
    }

    @VynFunc
    public boolean isSwimming() {
        return player != null && player.isSwimming();
    }

    @VynFunc
    public boolean isVisuallySwimming() {
        return living() != null && living().isVisuallySwimming();
    }

    @VynFunc
    public boolean isSneaking() {
        return player != null && player.isShiftKeyDown();
    }

    @VynFunc
    public boolean isInSneakingPose() {
        return player != null && player.isCrouching();
    }

    @VynFunc
    public boolean isMovingSlowly() {
        return player != null && player.isMovingSlowly();
    }

    @VynFunc
    public boolean isJumping() {
        // LocalPlayer#input holds the authoritative client-side key state; Input#jump() is the jump key flag.
        return player != null && player.input != null && player.input.keyPresses != null && player.input.keyPresses.jump();
    }

    @VynFunc
    public boolean isClimbing() {
        return player != null && player.onClimbable();
    }

    @VynFunc
    public boolean isDescending() {
        return player != null && player.isDescending();
    }

    @VynFunc
    public boolean isVisuallyCrawling() {
        return player != null && player.isVisuallyCrawling();
    }

    @VynFunc
    public boolean isFallFlying() {
        return living() != null && living().isFallFlying();
    }

    @VynFunc
    public int getFallFlyingTicks() {
        return (living() != null) ? living().getFallFlyingTicks() : 0;
    }

    @VynFunc
    public boolean isUsingRiptide() {
        return player != null && player.isAutoSpinAttack();
    }

    @VynFunc
    public boolean isSleeping() {
        return living() != null && living().isSleeping();
    }

    @VynFunc
    public Position getSleepingPosition() {
        if (living() == null || living().getSleepingPos().isEmpty()) {
            return null;
        }
        final BlockPos pos = living().getSleepingPos().get();
        return new Position(pos.getX(), pos.getY(), pos.getZ());
    }

    @VynFunc
    public float getJumpBoostPower() {
        return (living() != null) ? living().getJumpBoostPower() : 0.0f;
    }

    // ------------------------------------------------------------------ fluids / fire / freezing

    @VynFunc
    public boolean isInLava() {
        return player != null && player.isInLava();
    }

    @VynFunc
    public boolean isInFluid() {
        return player != null && player.isInLiquid();
    }

    @VynFunc
    public boolean isTouchingWater() {
        return player != null && player.isInWater();
    }

    @VynFunc
    public boolean isSubmergedInWater() {
        return player != null && player.isUnderWater();
    }

    @VynFunc
    public boolean isUnderWater() {
        return isSubmergedInWater();
    }

    @VynFunc
    public float getWaterVision() {
        return player != null ? player.getWaterVision() : 0.0f;
    }

    @VynFunc
    public boolean canBreatheInWater() {
        return living() != null && living().canBreatheUnderwater();
    }

    @VynFunc
    public boolean canBreatheUnderwater() {
        return canBreatheInWater();
    }

    @VynFunc
    public boolean hasLandedInFluid() {
        return living() != null && living().hasLandedInLiquid();
    }

    @VynFunc
    public boolean hasLandedInLiquid() {
        return hasLandedInFluid();
    }

    @VynFunc
    public boolean isSensitiveToWater() {
        return living() != null && living().isSensitiveToWater();
    }

    @VynFunc
    public boolean isAffectedByFluids() {
        return living() != null && living().isAffectedByFluids();
    }

    @VynFunc
    public boolean isPushedByFluids() {
        return player != null && player.isPushedByFluid();
    }

    @VynFunc
    public boolean isOnFire() {
        return player != null && player.isOnFire();
    }

    @VynFunc
    public boolean isFireImmune() {
        return player != null && player.fireImmune();
    }

    @VynFunc
    public boolean canFreeze() {
        return living() != null && living().canFreeze();
    }

    @VynFunc
    public boolean isFreezing() {
        return player != null && player.isFreezing();
    }

    @VynFunc
    public boolean isFrozen() {
        return player != null && player.isFullyFrozen();
    }

    @VynFunc
    public int getTicksFrozen() {
        return (player != null) ? player.getTicksFrozen() : 0;
    }

    @VynFunc
    public float getPercentFrozen() {
        return (player != null) ? player.getPercentFrozen() : 0.0f;
    }

    // ------------------------------------------------------------------ collision / generic flags

    @VynFunc
    public boolean isInsideWall() {
        return player != null && player.isInWall();
    }

    @VynFunc
    public boolean isHorizontalCollision() {
        return player != null && player.horizontalCollision;
    }

    @VynFunc
    public boolean isPushable() {
        return player != null && player.isPushable();
    }

    @VynFunc
    public boolean isInvulnerable() {
        return player != null && player.isInvulnerable();
    }

    @VynFunc
    public boolean isRiding() {
        return player != null && player.isPassenger();
    }

    @VynFunc
    public boolean isOnRail() {
        return player != null && player.isOnRails();
    }

    @VynFunc
    public boolean isInvisible() {
        return player != null && player.isInvisible();
    }

    @VynFunc
    public boolean isSilent() {
        return player != null && player.isSilent();
    }

    @VynFunc
    public boolean isCurrentlyGlowing() {
        return player != null && player.isCurrentlyGlowing();
    }

    @VynFunc
    public boolean shouldShowName() {
        return living() != null && living().shouldShowName();
    }

    @VynFunc
    public boolean isPickable() {
        return player != null && player.isPickable();
    }

    @VynFunc
    public boolean canBeSeenAsEnemy() {
        return living() != null && living().canBeSeenAsEnemy();
    }

    @VynFunc
    public boolean canBeSeenByAnyone() {
        return living() != null && living().canBeSeenByAnyone();
    }

    // ------------------------------------------------------------------ item use / combat / swing

    @VynFunc
    public boolean isUsingItem() {
        return player != null && player.isUsingItem();
    }

    @VynFunc
    public int getItemUseTime() {
        return (living() != null) ? living().getTicksUsingItem() : -1;
    }

    @VynFunc
    public int getItemUseTimeLeft() {
        return (living() != null) ? living().getUseItemRemainingTicks() : -1;
    }

    @VynFunc
    public String getUsedItemHand() {
        return (living() != null && living().getUsedItemHand() != null) ? living().getUsedItemHand().name() : null;
    }

    @VynFunc
    public boolean isBlocking() {
        return player != null && player.isBlocking();
    }

    @VynFunc
    public float getSecondsToDisableBlocking() {
        return (living() != null) ? living().getSecondsToDisableBlocking() : 0.0f;
    }

    @VynFunc
    public boolean isSwinging() {
        return living() != null && living().swinging;
    }

    @VynFunc
    public int getSwingTime() {
        return (living() != null) ? living().swingTime : 0;
    }

    @VynFunc
    public float getAttackAnim(final double tickDelta) {
        return (living() != null) ? living().getAttackAnim((float) tickDelta) : 0.0f;
    }

    // ------------------------------------------------------------------ potion / effect related

    @VynFunc
    public boolean isAffectedByPotions() {
        return living() != null && living().isAffectedByPotions();
    }

    @VynFunc
    public boolean attackable() {
        return living() != null && living().attackable();
    }

    @VynFunc
    public boolean isInvertedHealAndHarm() {
        return living() != null && living().isInvertedHealAndHarm();
    }

    @VynFunc
    public boolean hasInfiniteMaterials() {
        return living() != null && living().hasInfiniteMaterials();
    }

    // ------------------------------------------------------------------ riding / vehicle

    @VynFunc
    public boolean isRidingJumpable() {
        return player != null && player.jumpableVehicle() != null;
    }

    @VynFunc
    public float getMountJumpStrength() {
        return player != null ? player.getJumpRidingScale() : 0.0f;
    }

    @VynFunc
    public float getJumpRidingScale() {
        return getMountJumpStrength();
    }

    @VynFunc
    public boolean isHandsBusy() {
        return player != null && player.isHandsBusy();
    }

    @VynFunc
    public boolean shouldRotateWithMinecart() {
        return player != null && player.shouldRotateWithMinecart();
    }

    // ------------------------------------------------------------------ client-only flags (LocalPlayer)

    @VynFunc
    public boolean isAutoJumpEnabled() {
        return player != null && player.isAutoJumpEnabled();
    }

    @VynFunc
    public boolean isSuppressingSlidingDownLadder() {
        return player != null && player.isSuppressingSlidingDownLadder();
    }

    @VynFunc
    public boolean canSpawnSprintParticle() {
        return player != null && player.canSpawnSprintParticle();
    }

    @VynFunc
    public boolean getDoLimitedCrafting() {
        return player != null && player.getDoLimitedCrafting();
    }

    @VynFunc
    public boolean showsDeathScreen() {
        return player != null && player.shouldShowDeathScreen();
    }

    @VynFunc
    public boolean canDropItems() {
        return player != null && player.canDropItems();
    }

    @VynFunc
    public boolean isTextFilteringEnabled() {
        return player != null && player.isTextFilteringEnabled();
    }

    @VynFunc
    public float getMoodPercentage() {
        return player != null ? player.getCurrentMood() : 0.0f;
    }

    @VynFunc
    public float getCurrentMood() {
        return getMoodPercentage();
    }

    @VynFunc
    public float portalEffectIntensity() {
        return player != null ? player.portalEffectIntensity : 0.0f;
    }

    @VynFunc
    public String getPortalTransition() {
        return (player != null && player.getActivePortalLocalTransition() != null) ? player.getActivePortalLocalTransition().name() : null;
    }

    // ------------------------------------------------------------------ misc scalars

    @VynFunc
    public int getMaxFallDistance() {
        return (living() != null) ? living().getMaxFallDistance() : 0;
    }

    @VynFunc
    public boolean isBaby() {
        return living() != null && living().isBaby();
    }

    @VynFunc
    public float getAgeScale() {
        return (living() != null) ? living().getAgeScale() : 1.0f;
    }

    @VynFunc
    public float getScale() {
        return (living() != null) ? living().getScale() : 1.0f;
    }

    @VynFunc
    public float getVoicePitch() {
        return (living() != null) ? living().getVoicePitch() : 1.0f;
    }

    @VynFunc
    public String getMainArm() {
        return (living() != null && living().getMainArm() != null) ? living().getMainArm().name() : null;
    }

    @VynFunc
    public float getSwimAmount(final double tickDelta) {
        return (living() != null) ? living().getSwimAmount((float) tickDelta) : 0.0f;
    }

    @VynFunc
    public double getVisibilityPercent() {
        return (living() != null) ? living().getVisibilityPercent(null) : 0;
    }

    @VynFunc
    public float getYBob() {
        return player != null ? player.yBob : 0.0f;
    }

    @VynFunc
    public float getXBob() {
        return player != null ? player.xBob : 0.0f;
    }

    // ------------------------------------------------------------------ actions

    @VynFunc
    public void playSound(final String soundId, final double volume, final double pitch) {
        if (player != null) {
            player.playSound(BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(soundId)).get().value(), (float) volume, (float) pitch);
        }
    }

    @VynFunc
    public void playSound(final Sound sound) {
        if (player == null) {
            return;
        }
        player.playSound(BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(sound.getName())).get().value(), (float) sound.getVolume(), (float) sound.getPitch());
    }

    @VynFunc
    public void playSoundWorld(final Position position, final String soundId, final double volume, final double pitch) {
        final Minecraft client = client();
        if (player == null || client == null || client.level == null) {
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
        final Minecraft client = client();
        if (player == null || client == null || client.level == null) {
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
