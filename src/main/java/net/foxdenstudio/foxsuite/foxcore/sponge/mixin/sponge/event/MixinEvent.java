package net.foxdenstudio.foxsuite.foxcore.sponge.mixin.sponge.event;

import net.foxdenstudio.foxsuite.foxcore.platform.event.Event;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(org.spongepowered.api.event.Event.class)
public interface MixinEvent extends Event {

}
