package net.foxdenstudio.sponge.foxcore.common.network.server.packet;

import net.foxdenstudio.sponge.foxcore.common.network.IServerPacket;
import org.spongepowered.api.network.ChannelBuf;

/**
 * Created by Fox on 4/18/2016.
 */
public class ServerPrintStringPacket implements IServerPacket {

    public static final String ID = "serverprintstring";
    private final String[] toPrint;

    public ServerPrintStringPacket(String... toPrint) {
        this.toPrint = toPrint;
    }

    @Override
    public void write(ChannelBuf buf) {
        for (String str : toPrint)
            buf.writeString(str);
    }

    @Override
    public String id() {
        return ID;
    }
}
