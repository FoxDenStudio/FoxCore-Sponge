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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import net.foxdenstudio.sponge.foxcore.common.network.IClientPacket;
import net.foxdenstudio.sponge.foxcore.common.network.client.ClientPackets;
import net.foxdenstudio.sponge.foxcore.common.network.server.ServerPackets;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.Map;

public class FCClientNetworkManager {

    private static FCClientNetworkManager instance;

    private final FMLEmbeddedChannel channelInstance;
    private final Map<Integer, ServerPackets> packetMapping;

    private FCClientNetworkManager() {
        packetMapping = new HashMap<>();
        if (NetworkRegistry.INSTANCE.hasChannel("foxcore", Side.CLIENT)) {
            String inboundHandlerName = null;
            this.channelInstance = NetworkRegistry.INSTANCE.getChannel("foxcore", Side.CLIENT);
            for (Map.Entry<String, ChannelHandler> entry : this.channelInstance.pipeline().toMap().entrySet()) {
                if (entry.getKey().contains("Sponge")) {
                    channelInstance.pipeline().remove(entry.getValue());
                } else if (entry.getKey().contains("Inbound")) {
                    inboundHandlerName = entry.getKey();
                }
            }
            if (inboundHandlerName != null) {
                channelInstance.pipeline().addBefore(inboundHandlerName, "FCPacketHandler", new PacketHandler());
            } else {
                channelInstance.pipeline().addLast(new PacketHandler());
            }
        } else {
            this.channelInstance = NetworkRegistry.INSTANCE.newChannel("foxcore", new PacketHandler()).get(Side.CLIENT);
        }
        /*System.out.println("------------------------------------------------------------");
        System.out.println(channelInstance.pipeline().toMap());
        System.out.println("------------------------------------------------------------");*/
    }

    public static FCClientNetworkManager instance() {
        if (instance == null) instance = new FCClientNetworkManager();
        return instance;
    }

    public Map<Integer, ServerPackets> getPacketMapping() {
        return packetMapping;
    }

    public void sendPacket(IClientPacket clientPacket) {
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeInt(ClientPackets.map.get(clientPacket.id()).ordinal());
        clientPacket.write(byteBuf);
        channelInstance.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        channelInstance.writeAndFlush(new FMLProxyPacket(new PacketBuffer(byteBuf), "foxcore")).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }
}
