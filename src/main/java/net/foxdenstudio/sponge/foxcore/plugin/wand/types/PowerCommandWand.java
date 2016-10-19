package net.foxdenstudio.sponge.foxcore.plugin.wand.types;

import net.foxdenstudio.sponge.foxcore.plugin.command.util.ProcessResult;
import net.foxdenstudio.sponge.foxcore.plugin.wand.WandMutableBase;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.List;

/**
 * Created by Fox on 10/13/2016.
 */
public class PowerCommandWand extends WandMutableBase {

    @Override
    public boolean leftClickAir(Player player) {
        return false;
    }

    @Override
    public boolean rightClickAir(Player player) {
        return false;
    }

    @Override
    public boolean leftClickBlock(Player player, BlockSnapshot block) {
        return false;
    }

    @Override
    public boolean rightClickBlock(Player player, BlockSnapshot block) {
        return false;
    }

    @Override
    public boolean leftClickEntity(Player player, Entity entity) {
        return false;
    }

    @Override
    public boolean rightClickEntity(Player player, Entity entity) {
        return false;
    }

    @Override
    public ProcessResult modify(CommandSource source, String arguments) throws CommandException {
        return ProcessResult.failure();
    }

    @Override
    public Text getItemName() {
        return null;
    }

    @Override
    public List<Text> getLore() {
        return null;
    }

    @Override
    public String type() {
        return null;
    }

    @Override
    public int getContentVersion() {
        return 0;
    }

    @Override
    public DataContainer toContainer() {
        return null;
    }

}
