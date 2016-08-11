package net.foxdenstudio.sponge.foxcore.plugin.wand;

import org.spongepowered.api.data.DataView;

/**
 * Created by Fox on 7/29/2016.
 */
public interface IWandBuilder {

    IWand create();

    IWand build(DataView dataView);
}
