/*
 * This file is part of FoxCore, licensed under the MIT License (MIT).
 *
 * Copyright (c) gravityfox - https://gravityfox.net/
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.foxdenstudio.sponge.foxcore.mod.network;

import com.flowpowered.math.vector.Vector3i;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.foxdenstudio.sponge.foxcore.common.network.server.ServerPackets;
import net.foxdenstudio.sponge.foxcore.mod.FoxCoreForgeMain;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

import java.util.ArrayList;
import java.util.List;

@Sharable
public class PositionHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FMLProxyPacket) {
            ByteBuf data = ((FMLProxyPacket) msg).payload();
            if (data.isReadable(4) && data.getInt(0) == ServerPackets.POSITION.ID) {
                data.skipBytes(4);
                List<Vector3i> list = new ArrayList<>();
                while (data.isReadable(12)) {
                    Vector3i pos = new Vector3i(data.readInt(), data.readInt(), data.readInt());
                    list.add(pos);
                    //System.out.println("[" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]");
                }
                FoxCoreForgeMain.proxy.updatePositionsList(list);
                //System.out.flush();
            } else {
                super.channelRead(ctx, msg);
            }
        } else {
            super.channelRead(ctx, msg);
        }

    }

}
