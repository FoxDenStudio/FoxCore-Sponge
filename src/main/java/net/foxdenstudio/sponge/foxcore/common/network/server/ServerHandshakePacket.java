package net.foxdenstudio.sponge.foxcore.common.network.server;

import io.netty.buffer.ByteBuf;
import net.foxdenstudio.sponge.foxcore.common.network.IServerPacket;
import org.spongepowered.api.network.ChannelBuf;

/**
 * Created by Fox on 4/18/2016.
 */
public class ServerHandshakePacket implements IServerPacket {

    public String plugin;
    public String version;

    public ServerHandshakePacket(String plugin, String version) {
        this.plugin = plugin;
        this.version = version;
    }

    public ServerHandshakePacket() {
        version = "";
        version = "";
    }

    @Override
    public void read(ByteBuf payload) {

    }

    @Override
    public void write(ChannelBuf buf) {

    }
}
