package net.foxdenstudio.foxsuite.foxcore.sponge.mixin.sponge.command.source;


import net.foxdenstudio.foxsuite.foxcore.platform.command.source.ConsoleSource;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(org.spongepowered.api.command.source.ConsoleSource.class)
public interface MixinConsoleSource extends ConsoleSource {
}
