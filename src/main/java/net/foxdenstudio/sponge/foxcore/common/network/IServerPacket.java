package net.foxdenstudio.sponge.foxcore.common.network;

import org.spongepowered.api.network.ChannelBuf;

/**
 * Created by Fox on 4/18/2016.
 */
public interface IServerPacket extends IPacket {

    void write(ChannelBuf buf);
}
