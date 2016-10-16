package net.foxdenstudio.sponge.foxcore.common.network.server.packet;

import com.flowpowered.math.vector.Vector3f;
import net.foxdenstudio.sponge.foxcore.common.network.IServerPacket;
import net.foxdenstudio.sponge.foxcore.plugin.util.Position;
import org.spongepowered.api.network.ChannelBuf;

import java.util.List;

/**
 * Created by Fox on 4/18/2016.
 */
public class ServerPositionPacket implements IServerPacket {

    public static final String ID = "serverpositionpacket";
    private List<Position> positionList;

    public ServerPositionPacket(List<Position> positionList) {
        this.positionList = positionList;
    }

    @Override
    public void write(ChannelBuf buf) {
        positionList.forEach(pos -> {
            Vector3f color = pos.getRgbColor();
            buf
                    .writeInteger(pos.getX())
                    .writeInteger(pos.getY())
                    .writeInteger(pos.getZ())
                    .writeFloat(color.getX())
                    .writeFloat(color.getY())
                    .writeFloat(color.getZ());
        });
    }

    @Override
    public String id() {
        return ID;
    }
}
