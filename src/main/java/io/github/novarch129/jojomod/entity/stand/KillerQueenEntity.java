package io.github.novarch129.jojomod.entity.stand;

import io.github.novarch129.jojomod.capability.Stand;
import io.github.novarch129.jojomod.capability.StandChunkEffects;
import io.github.novarch129.jojomod.capability.StandEffects;
import io.github.novarch129.jojomod.entity.stand.attack.KillerQueenPunchEntity;
import io.github.novarch129.jojomod.init.SoundInit;
import io.github.novarch129.jojomod.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

@SuppressWarnings("ConstantConditions")
public class KillerQueenEntity extends AbstractStandEntity {
    public LivingEntity bombEntity;
    protected int shaCount;
    private SheerHeartAttackEntity sheerHeartAttack;

    public KillerQueenEntity(EntityType<? extends AbstractStandEntity> type, World world) {
        super(type, world);
    }

    @Override
    public SoundEvent getSpawnSound() {
        return SoundInit.SPAWN_KILLER_QUEEN.get();
    }

    public void detonate() {
        if (getMaster() == null) return;
        Stand.getLazyOptional(master).ifPresent(props -> {
            if (world.getBlockState(props.getBlockPos()).isAir(world, props.getBlockPos())) {
                props.setBlockPos(BlockPos.ZERO);
                StandChunkEffects.getLazyOptional(world.getChunkAt(master.getPosition())).ifPresent(standChunkEffects -> standChunkEffects.removeBombPos(master));
                props.setAbilityUseCount(0);
            }
            if (props.getCooldown() <= 0) {
                if (!world.isRemote)
                    getServer().getWorld(dimension).getEntities()
                            .filter(entity -> entity instanceof ItemEntity)
                            .forEach(entity ->
                                    StandEffects.getLazyOptional(entity).ifPresent(effects -> {
                                        if (effects.isBomb()) {
                                            PlayerEntity player = world.getPlayerByUuid(effects.getStandUser());
                                            if (player != null && player.equals(master)) {
                                                entity.world.createExplosion(entity, entity.getPosX(), entity.getPosY(), entity.getPosZ(), 2.3f, Explosion.Mode.DESTROY);
                                                props.setAbilityUseCount(0);
                                                entity.remove();
                                            }
                                        }
                                    })
                            );
                if (props.getBlockPos() != BlockPos.ZERO) {
                    if (!world.getChunkProvider().isChunkLoaded(world.getChunkAt(props.getBlockPos()).getPos())) return;
                    world.createExplosion(this, props.getBlockPos().getX(), props.getBlockPos().getY(), props.getBlockPos().getZ(), 3, Explosion.Mode.DESTROY);
                    StandChunkEffects.getLazyOptional(world.getChunkAt(master.getPosition())).ifPresent(standChunkEffects -> standChunkEffects.removeBombPos(master));
                    props.setBlockPos(BlockPos.ZERO);
                    props.setAbilityUseCount(0);
                }
                if (bombEntity != null) {
                    if (bombEntity.isAlive()) {
                        props.setCooldown(140);
                        if (bombEntity instanceof MobEntity) {
                            Explosion explosion = new Explosion(bombEntity.world, master, bombEntity.getPosX(), bombEntity.getPosY(), bombEntity.getPosZ(), 4, true, Explosion.Mode.NONE);
                            ((MobEntity) bombEntity).spawnExplosionParticle();
                            explosion.doExplosionB(true);
                            world.playSound(null, master.getPosition(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1, 1);
                            bombEntity.remove();
                        } else if (bombEntity instanceof PlayerEntity) {
                            Stand.getLazyOptional((PlayerEntity) bombEntity).ifPresent(bombProps -> {
                                if (bombProps.getStandID() != Util.StandID.GER) {
                                    Explosion explosion = new Explosion(bombEntity.world, master, bombEntity.getPosX(), bombEntity.getPosY(), bombEntity.getPosZ(), 4, true, Explosion.Mode.NONE);
                                    ((PlayerEntity) bombEntity).spawnSweepParticles();
                                    explosion.doExplosionB(true);
                                    bombEntity.attackEntityFrom(DamageSource.FIREWORKS, 4.5f * bombEntity.getArmorCoverPercentage());
                                } else {
                                    Explosion explosion = new Explosion(master.world, master, master.getPosX(), master.getPosY(), master.getPosZ(), 4, true, Explosion.Mode.NONE);
                                    if (master.world.isRemote) {
                                        for (int i = 0; i < 20; ++i) {
                                            double d0 = master.world.rand.nextGaussian() * 0.02;
                                            double d1 = master.world.rand.nextGaussian() * 0.02;
                                            double d2 = master.world.rand.nextGaussian() * 0.02;
                                            master.world.addParticle(ParticleTypes.POOF, master.getPosXWidth(1) - d0 * 10, master.getPosYRandom() - d1 * 10, master.getPosZRandom(1) - d2 * 10, d0, d1, d2);
                                        }
                                    } else
                                        master.world.setEntityState(master, (byte) 20);
                                    explosion.doExplosionB(true);
                                    master.setHealth(0);
                                }
                            });
                        }
                        if (!master.isCreative() && !master.isSpectator())
                            master.getFoodStats().addStats(-2, 0);
                    }
                }
            }
        });
    }

    public void toggleSheerHeartAttack() {
        if (getMaster() == null || world.isRemote) return;
        if (sheerHeartAttack == null)
            sheerHeartAttack = new SheerHeartAttackEntity(world, this);
        Stand.getLazyOptional(getMaster()).ifPresent(props -> {
            if (shaCount <= 0) {
                sheerHeartAttack.setPosition(getPosX(), getPosY(), getPosZ());
                world.addEntity(sheerHeartAttack);
                shaCount++;
                props.setCooldown(300);
            } else {
                if (!world.isRemote)
                    world.getServer().getWorld(dimension).getEntities()
                            .filter(entity -> entity instanceof SheerHeartAttackEntity)
                            .filter(entity -> ((SheerHeartAttackEntity) entity).getMaster().getEntityId() == getMaster().getEntityId())
                            .forEach(Entity::remove);
            }
        });
    }

    public void turnItemOrBlockIntoBomb() {
        if (getMaster() == null || world.isRemote) return;
        Stand.getLazyOptional(master).ifPresent(props -> {
            if (!master.isCrouching() && master.getHeldItemMainhand() != ItemStack.EMPTY && props.getAbilityUseCount() == 0) {
                if (master.getHeldItemMainhand().getCount() > 1) {
                    if (master.inventory.getStackInSlot(master.inventory.getBestHotbarSlot()).isEmpty()) {
                        ItemStack stack = master.getHeldItemMainhand().copy();
                        stack.shrink(master.getHeldItemMainhand().getCount() - 1);
                        stack.getOrCreateTag().putBoolean("bomb", true);
                        master.getHeldItemMainhand().shrink(1);
                        stack.setDisplayName(new StringTextComponent("Bomb"));
                        master.inventory.add(master.inventory.getBestHotbarSlot(), stack);
                        master.sendStatusMessage(new StringTextComponent("Killer Queen has turned 1 " + new TranslationTextComponent(master.getHeldItemMainhand().getTranslationKey()).getFormattedText() + " into a bomb."), true);
                        props.setAbilityUseCount(1);
                    } else
                        master.sendStatusMessage(new StringTextComponent("Your hotbar is full!"), true);
                } else if (master.getHeldItemMainhand().getCount() == 1) {
                    master.getHeldItemMainhand().getOrCreateTag().putBoolean("bomb", true);
                    master.getHeldItemMainhand().setDisplayName(new StringTextComponent("Bomb"));
                    master.sendStatusMessage(new StringTextComponent("Killer Queen has turned your " + new TranslationTextComponent(master.getHeldItemMainhand().getTranslationKey()).getFormattedText() + " into a bomb."), true);
                    props.setAbilityUseCount(1);
                }
            } else if (master.isCrouching() && props.getAbilityUseCount() == 0) {
                BlockPos position = master.getPosition().add(0, -1, 0);
                props.setBlockPos(position);
                StandChunkEffects.getLazyOptional(world.getChunkAt(master.getPosition())).ifPresent(standChunkEffects -> standChunkEffects.addBombPos(master, position));
                master.sendStatusMessage(new StringTextComponent("Killer Queen has turned the block at X" + position.getX() + " Y" + position.getY() + " Z" + position.getZ() + " into a bomb."), true);
                props.setAbilityUseCount(1);
            }
        });
    }

    @Override
    public void attack(boolean special) {
        if (getMaster() == null) return;
        attackTick++;
        if (attackTick == 1)
            if (special)
                attackRush = true;
            else {
                world.playSound(null, getPosition(), SoundInit.PUNCH_MISS.get(), SoundCategory.NEUTRAL, 1, 0.6f / (rand.nextFloat() * 0.3f + 1) * 2);
                KillerQueenPunchEntity killerQueenPunchEntity = new KillerQueenPunchEntity(world, this, master);
                killerQueenPunchEntity.shoot(getMaster(), rotationPitch, rotationYaw, 2, 0.3f);
                world.addEntity(killerQueenPunchEntity);
            }
    }

    @Override
    public void tick() {
        super.tick();
        if (getMaster() != null) {
            Stand.getLazyOptional(master).ifPresent(props -> props.setAbility(false));

            followMaster();
            setRotationYawHead(master.rotationYawHead);
            setRotation(master.rotationYaw, master.rotationPitch);

            if (master.swingProgressInt == 0 && !attackRush)
                attackTick = 0;
            if (attackRush) {
                master.setSprinting(false);
                attackTicker++;
                if (attackTicker >= 10)
                    if (!world.isRemote) {
                        master.setSprinting(false);
                        KillerQueenPunchEntity killerQueen1 = new KillerQueenPunchEntity(world, this, master);
                        killerQueen1.randomizePositions();
                        killerQueen1.shoot(master, master.rotationPitch, master.rotationYaw, 2, 0.4f);
                        world.addEntity(killerQueen1);
                        KillerQueenPunchEntity killerQueen2 = new KillerQueenPunchEntity(world, this, master);
                        killerQueen2.randomizePositions();
                        killerQueen2.shoot(master, master.rotationPitch, master.rotationYaw, 2, 0.4f);
                        world.addEntity(killerQueen2);
                    }
                if (attackTicker >= 80) {
                    attackRush = false;
                    attackTicker = 0;
                }
            }
        }
    }
}