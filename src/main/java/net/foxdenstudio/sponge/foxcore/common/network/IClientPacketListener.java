package net.foxdenstudio.sponge.foxcore.common.network;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBuf;

/**
 * Created by Fox on 7/21/2016.
 */
public interface IClientPacketListener {

    void read(ChannelBuf payload, Player player);

}
