package net.foxdenstudio.sponge.foxcore.common.network.server.packet;

import com.flowpowered.math.vector.Vector3i;
import net.foxdenstudio.sponge.foxcore.common.network.IServerPacket;
import org.spongepowered.api.network.ChannelBuf;

import java.util.List;

/**
 * Created by Fox on 4/18/2016.
 */
public class ServerPositionPacket implements IServerPacket {

    public static final String ID = "serverpositionpacket";
    private List<Vector3i> positionList;

    public ServerPositionPacket(List<Vector3i> positionList) {
        this.positionList = positionList;
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
