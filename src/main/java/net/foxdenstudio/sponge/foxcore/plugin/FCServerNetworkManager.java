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

package net.foxdenstudio.sponge.foxcore.plugin;

import net.foxdenstudio.sponge.foxcore.common.network.IClientPacketListener;
import net.foxdenstudio.sponge.foxcore.common.network.IServerPacket;
import net.foxdenstudio.sponge.foxcore.common.network.server.packet.ServerPrintStringPacket;
import net.foxdenstudio.sponge.foxcore.mod.FCClientNetworkManager;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.network.PlayerConnection;

import java.util.HashMap;
import java.util.Map;

public class FCServerNetworkManager {

    private static FCServerNetworkManager instance;

    private ChannelBinding.RawDataChannel rawDataChannel;

    private final Map<String, ServerChannel> serverChannels = new HashMap<>();
    private final Map<String, Integer> serverPacketIDMapping = new HashMap<>();
    private final Map<Player, PlayerConfig> playerConfigs = new HashMap<>();
    private int nextAvailableChannelIndex = 1;
    private int nextAvailablePacketIndex = 0;
    private boolean locked = false;


    private FCServerNetworkManager() {
    }

    public static FCServerNetworkManager instance() {
        if (instance == null) instance = new FCServerNetworkManager();
        return instance;
    }

    public void registerNetworkingChannels() {
        rawDataChannel = Sponge.getGame().getChannelRegistrar().createRawChannel(FoxCoreMain.instance(), "fox");
        rawDataChannel.addListener(Platform.Type.SERVER, (data, connection, side) -> {
            FoxCoreMain.instance().logger().info("FOX PACKET RECIEVED!!!");
            if (connection instanceof PlayerConnection) {
                Player player = ((PlayerConnection) connection).getPlayer();
                PlayerConfig playerConfig = this.playerConfigs.get(player);
                int channelID = data.readInteger();
                if (channelID == 0) {
                    int channelCount = data.readInteger();
                    for (int i = 0; i < channelCount; i++) {
                        int serverChannelID = data.readInteger();
                        String serverChannelName = data.readString();
                        playerConfig.clientChannelMapping.put(serverChannelID, serverChannelName);
                    }
                    int packetCount = data.readInteger();
                    for (int i = 0; i < packetCount; i++) {
                        int serverPacketID = data.readInteger();
                        String serverPacketName = data.readString();
                        playerConfig.clientPacketMapping.put(serverPacketID, serverPacketName);
                    }
                    playerConfig.hasClient = true;
                    FoxCoreMain.instance().logger().info("Saying hi to \"" + player.getName() + "\"!");
                    FoxCoreMain.instance().getFoxcoreNetworkChannel().sendPacket(player, new ServerPrintStringPacket("Yerf. ^^ (I got told to change it. Again. x3)"));
                } else if (playerConfig.hasClient) {
                    ServerChannel channel = this.serverChannels.get(playerConfig.clientChannelMapping.get(channelID));
                    if (channel != null) {
                        int packetID = data.readInteger();
                        IClientPacketListener listener = channel.clientPacketListeners.get(playerConfig.clientPacketMapping.get(packetID));
                        if (listener != null) {
                            listener.read(data, player);
                        }
                    }
                }
            }
        });
    }

    public PlayerConfig getPlayerConfig(Player player) {
        return this.playerConfigs.get(player);
    }

    public void negotiateHandshake(Player player) {
        if (rawDataChannel != null && locked) {
            PlayerConfig playerConfig = new PlayerConfig();
            this.playerConfigs.put(player, playerConfig);

            rawDataChannel.sendTo(player, load -> {
                load.writeInteger(0);
                load.writeInteger(this.serverChannels.size());
                for (ServerChannel channel : this.serverChannels.values()) {
                    load.writeInteger(channel.id);
                    load.writeString(channel.name);
                }
                load.writeInteger(this.serverPacketIDMapping.size());
                for (Map.Entry<String, Integer> entry : this.serverPacketIDMapping.entrySet()) {
                    load.writeInteger(entry.getValue());
                    load.writeString(entry.getKey());
                }
            });
        } else {
            FoxCoreMain.instance().logger().error("Tried to negotiate handshake before manager configurations has been locked!");
        }
    }

    public void registerPacket(String packetID) {
        if (!locked && !this.serverPacketIDMapping.containsKey(packetID)) {
            this.serverPacketIDMapping.put(packetID, this.nextAvailablePacketIndex++);
        }
    }

    public ServerChannel getOrCreateServerChannel(String name) {
        ServerChannel channel = this.serverChannels.get(name);
        if (channel == null && !locked) {
            channel = new ServerChannel(name, this.nextAvailableChannelIndex++);
            this.serverChannels.put(name, channel);
        }
        return channel;
    }

    public boolean hasClient(Player player) {
        PlayerConfig playerConfig = this.playerConfigs.get(player);
        return playerConfig != null && playerConfig.hasClient;
    }

    @Listener
    public void onServerStarting(GameStartingServerEvent event) {
        locked = true;
    }

    public class ServerChannel {

        public final FCServerNetworkManager networkManager = FCServerNetworkManager.this;
        public final String name;
        public final int id;
        private final Map<String, IClientPacketListener> clientPacketListeners = new HashMap<>();


        private ServerChannel(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public void sendPacket(Player player, IServerPacket serverPacket) {
            if (rawDataChannel != null) {
                PlayerConfig playerConfig = playerConfigs.get(player);
                if (playerConfig != null && playerConfig.hasClient) {
                    rawDataChannel.sendTo(player, load -> {
                        load.writeInteger(id);
                        load.writeInteger(serverPacketIDMapping.get(serverPacket.id()));
                        serverPacket.write(load);
                    });
                }
            }
        }

        public void registerListener(String packetName, IClientPacketListener listener) {
            if (!clientPacketListeners.containsKey(packetName)) {
                clientPacketListeners.put(packetName, listener);
            }
        }

    }

    public class PlayerConfig {
        public boolean hasClient = false;
        final Map<Integer, String> clientChannelMapping = new HashMap<>();
        final Map<Integer, String> clientPacketMapping = new HashMap<>();
    }

}
