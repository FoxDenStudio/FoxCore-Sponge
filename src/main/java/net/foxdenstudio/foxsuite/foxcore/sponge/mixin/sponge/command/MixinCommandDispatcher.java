package net.foxdenstudio.foxsuite.foxcore.sponge.mixin.sponge.command;

import net.foxdenstudio.foxsuite.foxcore.platform.command.PlatformCommandDispatcher;
import org.spongepowered.api.command.dispatcher.Dispatcher;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Dispatcher.class)
public interface MixinCommandDispatcher extends PlatformCommandDispatcher {
}
