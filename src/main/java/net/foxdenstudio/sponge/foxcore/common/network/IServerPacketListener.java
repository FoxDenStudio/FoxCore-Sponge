package net.foxdenstudio.sponge.foxcore.common.network;

import io.netty.buffer.ByteBuf;

/**
 * Created by Fox on 7/21/2016.
 */
public interface IServerPacketListener {

    void read(ByteBuf payload);

}
