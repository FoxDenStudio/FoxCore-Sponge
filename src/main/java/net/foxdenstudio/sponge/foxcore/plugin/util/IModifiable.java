package net.foxdenstudio.sponge.foxcore.plugin.util;

import net.foxdenstudio.sponge.foxcore.plugin.command.util.ProcessResult;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Fox on 11/2/2016.
 */
public interface IModifiable {

    /**
     * This method is called when the modify command is used to try to this object.
     * It is essentially a command handler that does a specific job.
     * <p>
     *
     * @param source    The {@link CommandSource} of the modify command
     * @param arguments The {@link String} arguments specifically for this object
     * @return the result of the operation. The success flag should be true if and only if the object was changed in some way.
     * @throws CommandException If there is an issue parsing the command.
     */

    ProcessResult modify(CommandSource source, String arguments) throws CommandException;

    List<String> modifySuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException;

}
