package net.foxdenstudio.foxcore.sponge.mixin.sponge.command.result;

import net.foxdenstudio.foxcore.platform.command.result.PlatformCommandResult;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CommandResult.class)
public abstract class MixinCommandResult implements PlatformCommandResult {
}
