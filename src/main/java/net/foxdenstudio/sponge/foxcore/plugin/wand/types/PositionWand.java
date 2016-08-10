package net.foxdenstudio.sponge.foxcore.plugin.wand.types;

import com.flowpowered.math.vector.Vector3i;
import net.foxdenstudio.sponge.foxcore.plugin.state.FCStateManager;
import net.foxdenstudio.sponge.foxcore.plugin.util.FCPUtil;
import net.foxdenstudio.sponge.foxcore.plugin.wand.IWand;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

/**
 * Created by Fox on 4/30/2016.
 */
public class PositionWand implements IWand {

    public static PositionWand WAND = new PositionWand();

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
        Vector3i pos = block.getPosition();
        List<Vector3i> positions = FCPUtil.getPositions(player);
        if (positions.contains(pos)) {
            positions.remove(positions.lastIndexOf(pos));
            player.sendMessage(Text.of(TextColors.GREEN, "Successfully removed position (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")!"));
            FCPUtil.updatePositions(player);
            FCStateManager.instance().getStateMap().get(player).updateScoreboard();
        }
        return true;
    }

    @Override
    public boolean rightClickBlock(Player player, BlockSnapshot block) {
        Vector3i pos = block.getPosition();
        List<Vector3i> positions = FCPUtil.getPositions(player);
        positions.add(pos);
        player.sendMessage(Text.of(TextColors.GREEN, "Successfully added position (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")!"));
        FCPUtil.updatePositions(player);
        FCStateManager.instance().getStateMap().get(player).updateScoreboard();
        return true;
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
    public String type() {
        return "position";
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return new MemoryDataContainer();
    }
}
