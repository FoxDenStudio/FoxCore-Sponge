package net.foxdenstudio.foxcore.sponge.mixin.impl.command;

import net.foxdenstudio.foxcore.api.command.result.FoxCommandResult;
import net.foxdenstudio.foxcore.api.command.standard.FoxStandardCommand;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@Mixin(FoxStandardCommand.class)
public interface MixinFoxStandardCommand extends CommandCallable {

    @Shadow
    FoxCommandResult process(net.foxdenstudio.foxcore.platform.command.source.CommandSource source,
                             String arguments) throws Exception;

    @Override
    default CommandResult process(CommandSource source, String arguments) throws CommandException {
        FoxCommandResult result;
        try {
            result = this.process((net.foxdenstudio.foxcore.platform.command.source.CommandSource) source, arguments);
        } catch (CommandException e1) {
            throw e1;
        } catch (Exception e2) {
            throw new CommandException(Text.of("Unknown exception executing command", e2));
        }
        int successCount = result.getSuccesses();
        if (successCount == 0) return CommandResult.empty();
        return CommandResult.successCount(successCount);
    }

    @Override
    List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException;

    @Override
    boolean testPermission(CommandSource source);

    @Override
    Optional<Text> getShortDescription(CommandSource source);

    @Override
    Optional<Text> getHelp(CommandSource source);

    @Override
    Text getUsage(CommandSource source);
}
