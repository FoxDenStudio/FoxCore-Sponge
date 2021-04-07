package net.foxdenstudio.foxsuite.foxcore.sponge.mixin.sponge.command;

import net.foxdenstudio.foxsuite.foxcore.platform.command.PlatformCommand;
import net.foxdenstudio.foxsuite.foxcore.platform.command.source.CommandSource;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Mixin(CommandCallable.class)
public interface MixinCommandCallable extends PlatformCommand {

    List<String> getSuggestions(org.spongepowered.api.command.CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException;

    @Override
    default List<String> getSuggestions(@Nonnull CommandSource source, @Nonnull String arguments) throws CommandException {
        return this.getSuggestions((org.spongepowered.api.command.CommandSource) source, arguments, null);
    }
}
