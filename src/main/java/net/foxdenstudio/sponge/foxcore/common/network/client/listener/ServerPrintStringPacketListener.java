package net.foxdenstudio.sponge.foxcore.common.network.client.listener;

import io.netty.buffer.ByteBuf;
import net.foxdenstudio.sponge.foxcore.common.network.IServerPacketListener;
import net.foxdenstudio.sponge.foxcore.mod.FoxCoreClientMain;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * Created by Fox on 7/22/2016.
 */
public class ServerPrintStringPacketListener implements IServerPacketListener {

    @Override
    public void read(ByteBuf payload) {
        while (payload.isReadable()) {
            FoxCoreClientMain.logger.info(ByteBufUtils.readUTF8String(payload));
        }
    }

}
