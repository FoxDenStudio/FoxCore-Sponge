package net.foxdenstudio.sponge.foxcore.common.network;

import io.netty.buffer.ByteBuf;
import org.spongepowered.api.network.ChannelBuf;

/**
 * Created by Fox on 4/18/2016.
 */
public interface IServerPacket {

    void read(ByteBuf payload);

    void write(ChannelBuf buf);
}
