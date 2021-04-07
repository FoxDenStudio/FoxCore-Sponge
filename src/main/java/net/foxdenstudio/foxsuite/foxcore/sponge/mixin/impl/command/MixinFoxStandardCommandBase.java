package net.foxdenstudio.foxsuite.foxcore.sponge.mixin.impl.command;

import net.foxdenstudio.foxsuite.foxcore.api.command.FoxCommandBase;
import net.foxdenstudio.foxsuite.foxcore.api.command.standard.FoxStandardCommand;
import net.foxdenstudio.foxsuite.foxcore.api.command.standard.FoxStandardCommandBase;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(FoxStandardCommandBase.class)
public abstract class MixinFoxStandardCommandBase extends FoxCommandBase implements FoxStandardCommand, CommandCallable {

    @Override
    public boolean testPermission(CommandSource source) {
        return source.hasPermission(this.getClass().getName());
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.empty();
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return Optional.empty();
    }

    @Override
    public Text getUsage(CommandSource source) {
        return Text.of();
    }
}
