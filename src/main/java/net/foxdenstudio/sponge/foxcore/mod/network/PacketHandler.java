package net.foxdenstudio.sponge.foxcore.mod.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.foxdenstudio.sponge.foxcore.common.network.server.ServerPackets;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

/**
 * Created by Fox on 4/18/2016.
 */
public class PacketHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FMLProxyPacket) {
            ByteBuf data = ((FMLProxyPacket) msg).payload();
            int id = data.readInt();
            if (id == 0) {
                ServerPackets.HANDSHAKE.supplier.get().read(data);
            } else {
                FCClientNetworkManager.instance().getPacketMapping().get(id).supplier.get().read(data);
            }
        } else {
            super.channelRead(ctx, msg);
        }
    }
}
