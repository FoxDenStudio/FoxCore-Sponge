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

package net.foxdenstudio.sponge.foxcore.mod;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.foxdenstudio.sponge.foxcore.common.network.IClientPacket;
import net.foxdenstudio.sponge.foxcore.common.network.IServerPacketListener;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.Map;

public class FCClientNetworkManager {

    private static FCClientNetworkManager instance;

    private FMLEmbeddedChannel fmlEmbeddedChannel;
    private final Map<String, ClientChannel> clientChannels = new HashMap<>();
    private final Map<String, Integer> clientPacketIDMapping = new HashMap<>();
    public final Map<Integer, String> serverChannelMapping = new HashMap<>();
    public final Map<Integer, String> serverPacketMapping = new HashMap<>();
    public boolean hasServer = false;
    private int nextAvailableChannelIndex = 1;
    private int nextAvailablePacketIndex = 0;
    private boolean locked = false;

    private FCClientNetworkManager() {
    }

    public static FCClientNetworkManager instance() {
        if (instance == null) instance = new FCClientNetworkManager();
        return instance;
    }

    public void registerNetworkingChannels() {
        if (NetworkRegistry.INSTANCE.hasChannel("fox", Side.CLIENT)) {
            String inboundHandlerName = null;
            this.fmlEmbeddedChannel = NetworkRegistry.INSTANCE.getChannel("fox", Side.CLIENT);
            for (Map.Entry<String, ChannelHandler> entry : this.fmlEmbeddedChannel.pipeline().toMap().entrySet()) {
                if (entry.getKey().contains("Sponge")) {
                    fmlEmbeddedChannel.pipeline().remove(entry.getValue());
                } else if (entry.getKey().contains("Inbound")) {
                    inboundHandlerName = entry.getKey();
                }
            }
            if (inboundHandlerName != null) {
                fmlEmbeddedChannel.pipeline().addBefore(inboundHandlerName, "FoxPacketHandler", new PacketHandler());
            } else {
                fmlEmbeddedChannel.pipeline().addLast(new PacketHandler());
            }
        } else {
            this.fmlEmbeddedChannel = NetworkRegistry.INSTANCE.newChannel("fox", new PacketHandler()).get(Side.CLIENT);
        }
    }

    public void negotiateHandshake() {
        if (fmlEmbeddedChannel != null && locked) {
            ByteBuf byteBuf = Unpooled.buffer();
            byteBuf.writeInt(0);
            byteBuf.writeInt(this.clientChannels.size());
            for (FCClientNetworkManager.ClientChannel channel : this.clientChannels.values()) {
                byteBuf.writeInt(channel.id);
                ByteBufUtils.writeUTF8String(byteBuf, channel.name);
            }
            byteBuf.writeInt(this.clientPacketIDMapping.size());
            for (Map.Entry<String, Integer> entry : this.clientPacketIDMapping.entrySet()) {
                byteBuf.writeInt(entry.getValue());
                ByteBufUtils.writeUTF8String(byteBuf, entry.getKey());
            }
            fmlEmbeddedChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
            fmlEmbeddedChannel.writeAndFlush(new FMLProxyPacket(new PacketBuffer(byteBuf), "fox")).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            FoxCoreClientMain.logger.info("Sent handshake packet");
        } else {
            FoxCoreClientMain.logger.error("Tried to negotiate handshake before manager configurations has been locked!");
        }
    }

    public void registerPacket(String packetID) {
        if (!locked && !this.clientPacketIDMapping.containsKey(packetID)) {
            this.clientPacketIDMapping.put(packetID, this.nextAvailablePacketIndex++);
        }
    }

    public ClientChannel getOrCreateClientChannel(String name) {
        ClientChannel channel = this.clientChannels.get(name);
        if (channel == null && !locked) {
            channel = new ClientChannel(name, this.nextAvailableChannelIndex++);
            this.clientChannels.put(name, channel);
        }
        return channel;
    }

    void lock() {
        this.locked = true;
    }

    public class ClientChannel {

        public final FCClientNetworkManager networkManager = FCClientNetworkManager.this;
        public final String name;
        public final int id;
        private final Map<String, IServerPacketListener> serverPacketListeners = new HashMap<>();


        private ClientChannel(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public void sendPacket(IClientPacket clientPacket) {
            ByteBuf byteBuf = Unpooled.buffer();
            byteBuf.writeInt(id);
            byteBuf.writeInt(clientPacketIDMapping.get(clientPacket.id()));
            clientPacket.write(byteBuf);
            fmlEmbeddedChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
            fmlEmbeddedChannel.writeAndFlush(new FMLProxyPacket(new PacketBuffer(byteBuf), "fox")).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }

        public void registerListener(String packetName, IServerPacketListener listener) {
            if (!serverPacketListeners.containsKey(packetName)) {
                serverPacketListeners.put(packetName, listener);
            }
        }

        public void sendDebug() {
            ByteBuf byteBuf = Unpooled.buffer();
            byteBuf.writeInt(-1);

            fmlEmbeddedChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
            fmlEmbeddedChannel.writeAndFlush(new FMLProxyPacket(new PacketBuffer(byteBuf), "fox")).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }

        public boolean hasServerChannel() {
            return FCClientNetworkManager.this.serverChannelMapping.values().contains(this.name);
        }
    }

    /**
     * Created by Fox on 4/18/2016.
     */
    public class PacketHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof FMLProxyPacket) {
                ByteBuf data = ((FMLProxyPacket) msg).payload();
                int channelID = data.readInt();
                if (channelID == -1) {
                    FoxCoreClientMain.logger.info("DEBUG MESSAGE RECIEVED!");
                    System.out.println(Thread.currentThread());
                    FoxCoreClientMain.instance.getFoxcoreChannel().sendDebug();
                } else if (channelID == 0) {
                    FoxCoreClientMain.logger.info("FoxCore client network manager received a handshake. This means the server has FoxCore installed. Negotiating.");
                    int channelCount = data.readInt();
                    for (int i = 0; i < channelCount; i++) {
                        int serverChannelID = data.readInt();
                        String serverChannelName = ByteBufUtils.readUTF8String(data);
                        serverChannelMapping.put(serverChannelID, serverChannelName);
                    }
                    int packetCount = data.readInt();
                    for (int i = 0; i < packetCount; i++) {
                        int serverPacketID = data.readInt();
                        String serverPacketName = ByteBufUtils.readUTF8String(data);
                        serverPacketMapping.put(serverPacketID, serverPacketName);
                    }
                    hasServer = true;
                    // SpongeForge is dumb, and appears to lose packets that are sent to the server within the first
                    // few milliseconds of joining the server.
                    // So we just wait a second before sending the handshake packet.
                    // Since we are on a Netty worker thread, we're not stopping the rest of the network code from catching up.
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    negotiateHandshake();
                } else if (hasServer) {
                    ClientChannel channel = clientChannels.get(serverChannelMapping.get(channelID));
                    if (channel != null) {
                        int packetID = data.readInt();
                        IServerPacketListener listener = channel.serverPacketListeners.get(serverPacketMapping.get(packetID));
                        if (listener != null) {
                            listener.read(data);
                        }
                    }
                }
            } else {
                super.channelRead(ctx, msg);
            }
        }
    }
}
