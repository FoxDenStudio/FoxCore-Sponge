package net.foxdenstudio.sponge.foxcore.plugin.wand;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Created by Fox on 4/30/2016.
 */
public interface IWand extends DataSerializable{

    boolean leftClickAir(Player player);

    boolean rightClickAir(Player player);

    boolean leftClickBlock(Player player, BlockSnapshot block);

    boolean rightClickBlock(Player player, BlockSnapshot block);

    boolean leftClickEntity(Player player, Entity entity);

    boolean rightClickEntity(Player player, Entity entity);

    String type();
}
