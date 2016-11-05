package net.foxdenstudio.sponge.foxcore.plugin.command.misc;

import net.foxdenstudio.sponge.foxcore.plugin.command.FCCommandBase;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by Fox on 11/5/2016.
 */
public class CommandPWD extends FCCommandBase {

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        source.sendMessage(Text.of(TextColors.GOLD, "Current dir: ", TextColors.RESET, System.getProperty("user.dir")));
        return CommandResult.empty();
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return source.hasPermission("foxcore.command.misc.pwd");
    }
}
