package net.foxdenstudio.sponge.foxcore.plugin.wand;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Fox on 7/29/2016.
 */
public interface IWandFactory {

    default IWand create(String arguments) throws CommandException {
        return create(arguments, null, null);
    }

    IWand create(String arguments, @Nullable CommandSource source, @Nullable Location<World> targetPosition) throws CommandException;

    List<String> createSuggestions(String arguments, CommandSource source, @Nullable Location<World> targetPosition) throws CommandException;

    IWand build(DataView dataView);

    String type();

    String[] aliases();
}
