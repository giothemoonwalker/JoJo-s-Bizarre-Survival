package io.github.novarch129.jojomod.event;

import io.github.novarch129.jojomod.JojoBizarreSurvival;
import io.github.novarch129.jojomod.capability.stand.Stand;
import io.github.novarch129.jojomod.init.KeyInit;
import io.github.novarch129.jojomod.network.message.client.*;
import io.github.novarch129.jojomod.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import static io.github.novarch129.jojomod.network.message.client.CAerosmithControlPacket.Action.MOVE;
import static io.github.novarch129.jojomod.network.message.client.CAerosmithControlPacket.Direction.*;

@Mod.EventBusSubscriber(modid = JojoBizarreSurvival.MOD_ID, bus = Bus.FORGE, value = Dist.CLIENT)
public class EventHandleKeybinds {
    @SubscribeEvent
    public static void onInput(TickEvent.ClientTickEvent event) { //It's recommended to use ClientTickEvent instead of any of the input events.
        if (event.phase != TickEvent.Phase.END || !Minecraft.getInstance().isGameFocused()) return;
        Minecraft mc = Minecraft.getInstance();

        if (mc.player != null) {
            if (KeyInit.SPAWN_STAND.isPressed())
                JojoBizarreSurvival.INSTANCE.sendToServer(new CStandSummonPacket());

            if (KeyInit.TOGGLE_ABILITY.isPressed())
                JojoBizarreSurvival.INSTANCE.sendToServer(new CToggleAbilityPacket());

            if (mc.gameSettings.keyBindAttack.isKeyDown())
                JojoBizarreSurvival.INSTANCE.sendToServer(new CStandAttackPacket());

            Stand.getLazyOptional(mc.player).ifPresent(props -> {
                if (props.getStandOn()) {
                    if (KeyInit.ABILITY1.isPressed())
                        JojoBizarreSurvival.INSTANCE.sendToServer(new CSyncStandAbilitiesPacket(1));
                    if (KeyInit.ABILITY2.isPressed())
                        JojoBizarreSurvival.INSTANCE.sendToServer(new CSyncStandAbilitiesPacket(2));
                }

                if (props.getStandID() == Util.StandID.AEROSMITH) {
                    if (props.getStandOn() && props.getAbility()) {
                        if (mc.gameSettings.keyBindForward.isKeyDown())
                            JojoBizarreSurvival.INSTANCE.sendToServer(new CAerosmithControlPacket(MOVE, FORWARDS, mc.gameSettings.keyBindSprint.isKeyDown()));
                        if (mc.gameSettings.keyBindBack.isKeyDown())
                            JojoBizarreSurvival.INSTANCE.sendToServer(new CAerosmithControlPacket(MOVE, BACKWARDS));
                        if (mc.gameSettings.keyBindRight.isKeyDown())
                            JojoBizarreSurvival.INSTANCE.sendToServer(new CAerosmithControlPacket(MOVE, RIGHT));
                        if (mc.gameSettings.keyBindLeft.isKeyDown())
                            JojoBizarreSurvival.INSTANCE.sendToServer(new CAerosmithControlPacket(MOVE, LEFT));
                        if (mc.gameSettings.keyBindJump.isKeyDown())
                            JojoBizarreSurvival.INSTANCE.sendToServer(new CAerosmithControlPacket(MOVE, UP));
                        if (mc.gameSettings.keyBindSneak.isKeyDown())
                            JojoBizarreSurvival.INSTANCE.sendToServer(new CAerosmithControlPacket(MOVE, DOWN));
                    }
                }
            });
        }
    }
}