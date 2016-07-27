package net.foxdenstudio.sponge.foxcore.common.network.client.listener;

import com.flowpowered.math.vector.Vector3i;
import io.netty.buffer.ByteBuf;
import net.foxdenstudio.sponge.foxcore.common.network.IServerPacketListener;
import net.foxdenstudio.sponge.foxcore.mod.FoxCoreCUIMain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fox on 7/22/2016.
 */
public class ServerPositionPacketListener implements IServerPacketListener {

    @Override
    public void read(ByteBuf payload) {
        List<Vector3i> positionList = new ArrayList<>();
        while (payload.isReadable(12)) {
            Vector3i pos = new Vector3i(payload.readInt(), payload.readInt(), payload.readInt());
            positionList.add(pos);
        }
        FoxCoreCUIMain.proxy.updatePositionsList(positionList);
    }

}
