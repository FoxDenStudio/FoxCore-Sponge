package net.foxdenstudio.foxsuite.foxcore.sponge.mixin.sponge.command;

import net.foxdenstudio.foxsuite.foxcore.platform.command.PlatformCommand;
import net.foxdenstudio.foxsuite.foxcore.platform.command.PlatformCommandManager;
import net.foxdenstudio.foxsuite.foxcore.platform.command.PlatformCommandMapping;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Mixin(CommandManager.class)
public interface MixinCommandManager extends PlatformCommandManager {

    Optional<CommandMapping> register(Object plugin, CommandCallable callable, List<String> aliases);

    @SuppressWarnings("unchecked")
    @Override
    default Optional<? extends PlatformCommandMapping> registerCommand(Object plugin, PlatformCommand command, String primaryAlias, String... secondaryAliases) {
        List<String> aliases = new ArrayList<>();
        aliases.add(primaryAlias);
        aliases.addAll(Arrays.asList(secondaryAliases));
        return (Optional<PlatformCommandMapping>) (Optional<?>) this.register(plugin, (CommandCallable) command, aliases);
    }
}
