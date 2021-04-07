package net.foxdenstudio.foxsuite.foxcore.sponge.mixin.sponge.command.source;

import net.foxdenstudio.foxsuite.foxcore.platform.command.source.CommandSource;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(org.spongepowered.api.command.CommandSource.class)
public interface MixinCommandSource extends CommandSource {

    @Override
    boolean hasPermission(String permission);

}
