package com.novarch.jojomod.events;

import com.novarch.jojomod.JojoBizarreSurvival;
import com.novarch.jojomod.capabilities.stand.JojoProvider;
import com.novarch.jojomod.entities.fakePlayer.FakePlayerEntity;
import com.novarch.jojomod.entities.stands.aerosmith.EntityAerosmith;
import com.novarch.jojomod.init.EffectInit;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = JojoBizarreSurvival.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandleStandAbilities
{
    public static Entity renderViewEntity = null;
    public static List<Entity> removalQueue = new ArrayList<Entity>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        PlayerEntity player = event.player;

        if(!player.isPotionActive(Effects.GLOWING) && !player.isPotionActive(EffectInit.CRIMSON.get()))
            player.setGlowing(false);

        JojoProvider.getLazyOptional(player).ifPresent(props -> {
            if(props.getCooldown() == 0.5)
                props.setTimeLeft(1000);

            if(!props.getStandOn())
            {
                player.setInvulnerable(false);
                if(!player.isCreative() && !player.isSpectator())
                    player.setGameType(GameType.SURVIVAL);

                if(player.isPotionActive(EffectInit.CRIMSON_USER.get()))
                    player.removePotionEffect(EffectInit.CRIMSON_USER.get());

                if(props.getCooldown() > 0)
                    props.subtractCooldown(0.5);

                if(props.getTimeLeft() < 1000)
                    props.addTimeLeft(0.5);

                if(!player.world.isRemote)
                    player.world.getServer().getWorld(player.dimension).getEntities().forEach(entity -> {
                        if(entity instanceof FakePlayerEntity)
                            if(((FakePlayerEntity) entity).getParent() == player)
                                removalQueue.add(entity);
                });
            }

            else if(props.getStandOn() && !props.getAbility())
            {
                if(props.getCooldown() > 0)
                    props.subtractCooldown(0.5);

                if(props.getTimeLeft() < 100)
                    props.addTimeLeft(0.5);
            }

            if(!player.world.isRemote)
                player.world.getServer().getWorld(player.dimension).getEntities().forEach(entity -> {
                    if(entity instanceof EntityAerosmith)
                        if(((EntityAerosmith) entity).getMaster() == player)
                            renderViewEntity = entity;
                });
        });
    }

    @SubscribeEvent
    public static void crimsonEffectAddedHandler(PotionEvent.PotionAddedEvent event)
    {
        if(event.getPotionEffect().getPotion() == EffectInit.CRIMSON.get())
            event.getEntityLiving().setGlowing(true);
    }

    @SubscribeEvent
    public static void crimsonEffectRemovedHandler(PotionEvent.PotionRemoveEvent event)
    {
        if(event.getPotion() == EffectInit.CRIMSON.get())
            event.getEntityLiving().setGlowing(false);
        if(event.getPotion() == Effects.GLOWING)
            event.getEntityLiving().setGlowing(false);
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event)
    {
        if(Minecraft.getInstance().player==null)
            return;

        PlayerEntity player = Minecraft.getInstance().player;

        JojoProvider.getLazyOptional(player).ifPresent(props -> {
            if(!props.getStandOn()) {
                if(!player.isSpectator())
                    Minecraft.getInstance().setRenderViewEntity(player);
            } else {
                if(renderViewEntity != null)
                    if(renderViewEntity.isAlive())
                        if(props.getAbility()) {
                            Minecraft.getInstance().setRenderViewEntity(renderViewEntity);
                            if(Minecraft.getInstance().gameSettings.thirdPersonView != 1)
                                Minecraft.getInstance().gameSettings.thirdPersonView=1;
                        }
                    else
                        Minecraft.getInstance().setRenderViewEntity(player);
            }
        });
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event)
    {
        List<Entity> removed = new ArrayList<Entity>();
        if(event.phase == TickEvent.Phase.END)
        {
            if(removalQueue.size() > 0)
            {
                for (Entity entity : removalQueue)
                {
                    entity.remove();
                }
            }

            if(removed.size() > 0)
            {
                for (Entity removedEntity : removed) {
                    if (removalQueue.contains(removedEntity))
                        removalQueue.remove(removedEntity);
                }
                removed.removeAll(removed);
            }
        }
    }

    @SubscribeEvent
    public static void lastRender(RenderWorldLastEvent event)
    {
        //PlayerModel model = new PlayerModel(1.0f, true);
        //model.render(event.getMatrixStack(), null, 1, 1, 1, 1, 1, 1);
    }
}