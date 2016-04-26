package net.foxdenstudio.sponge.foxcore.common.network.server;

import com.flowpowered.math.vector.Vector3i;
import io.netty.buffer.ByteBuf;
import net.foxdenstudio.sponge.foxcore.common.network.IServerPacket;
import net.foxdenstudio.sponge.foxcore.mod.FoxCoreForgeMain;
import org.spongepowered.api.network.ChannelBuf;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fox on 4/18/2016.
 */
public class ServerPositionPacket implements IServerPacket {

    public static final String ID = "serverpositionpacket";
    List<Vector3i> positionList;

    public ServerPositionPacket(List<Vector3i> positionList) {
        this.positionList = positionList;
    }

    public ServerPositionPacket() {
    }

    @Override
    public void read(ByteBuf payload) {
        positionList = new ArrayList<>();
        while (payload.isReadable(12)) {
            Vector3i pos = new Vector3i(payload.readInt(), payload.readInt(), payload.readInt());
            positionList.add(pos);
        }
        FoxCoreForgeMain.proxy.updatePositionsList(positionList);
    }

    @Override
    public void write(ChannelBuf buf) {
        positionList.forEach(vec -> buf.writeInteger(vec.getX()).writeInteger(vec.getY()).writeInteger(vec.getZ()));
    }

    @Override
    public String id() {
        return ID;
    }
}
