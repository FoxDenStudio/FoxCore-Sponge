package net.foxdenstudio.sponge.foxcore.plugin.wand;

import net.foxdenstudio.sponge.foxcore.plugin.command.util.ProcessResult;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Optional;

/**
 * Created by Fox on 4/30/2016.
 */
public interface IWand extends DataSerializable {

    boolean leftClickAir(Player player);

    boolean rightClickAir(Player player);

    boolean leftClickBlock(Player player, BlockSnapshot block);

    boolean rightClickBlock(Player player, BlockSnapshot block);

    boolean leftClickEntity(Player player, Entity entity);

    boolean rightClickEntity(Player player, Entity entity);

    ProcessResult modify(CommandSource source, String arguments) throws CommandException;

    Text getItemName();

    List<Text> getLore();

    String type();
}
