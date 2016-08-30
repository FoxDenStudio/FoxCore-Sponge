package net.foxdenstudio.sponge.foxcore.common.network;

import io.netty.buffer.ByteBuf;

/**
 * Created by Fox on 4/18/2016.
 */
public interface IClientPacket extends IPacket {

    void write(ByteBuf buf);
}
