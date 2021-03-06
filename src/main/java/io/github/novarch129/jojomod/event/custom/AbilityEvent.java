package io.github.novarch129.jojomod.event.custom;

import io.github.novarch129.jojomod.capability.IStand;
import io.github.novarch129.jojomod.network.message.client.CToggleAbilityPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * {@link AbilityEvent} if fired when the value of {@link IStand#getAbility()} is changed.
 * This event is fired from {@link CToggleAbilityPacket} and has two child events.
 */
public class AbilityEvent extends PlayerEvent {
    public AbilityEvent(PlayerEntity player) {
        super(player);
    }

    @Override
    public PlayerEntity getPlayer() {
        return super.getPlayer();
    }

    /**
     * Fired when an {@link IStand#getAbility()} is activated.
     */
    public static class AbilityActivated extends AbilityEvent {

        public AbilityActivated(PlayerEntity player) {
            super(player);
        }
    }

    /**
     * Fired when an {@link IStand#getAbility()} is deactivated.
     */
    public static class AbilityDeactivated extends AbilityEvent {

        public AbilityDeactivated(PlayerEntity player) {
            super(player);
        }
    }
}
