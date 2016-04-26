package net.foxdenstudio.sponge.foxcore.common.network;

import io.netty.buffer.ByteBuf;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBuf;

/**
 * Created by Fox on 4/18/2016.
 */
public interface IClientPacket extends IPacket {

    void read(ChannelBuf payload, Player player);

    void write(ByteBuf buf);
}
