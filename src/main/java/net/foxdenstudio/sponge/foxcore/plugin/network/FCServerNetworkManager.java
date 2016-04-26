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

package net.foxdenstudio.sponge.foxcore.plugin.network;

import net.foxdenstudio.sponge.foxcore.common.network.IServerPacket;
import net.foxdenstudio.sponge.foxcore.common.network.client.ClientPackets;
import net.foxdenstudio.sponge.foxcore.common.network.server.ServerPackets;
import net.foxdenstudio.sponge.foxcore.plugin.FoxCoreMain;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.network.PlayerConnection;

import java.util.HashMap;
import java.util.Map;

public class FCServerNetworkManager {

    private static FCServerNetworkManager instance;

    private ChannelBinding.RawDataChannel channel;

    private final Map<Player, Boolean> clientModPresent;
    private final Map<Player, Map<Integer, ClientPackets>> packetMapping;

    private FCServerNetworkManager() {
        packetMapping = new HashMap<>();
        clientModPresent = new HashMap<>();
        channel = Sponge.getGame().getChannelRegistrar().createRawChannel(FoxCoreMain.instance(), "foxcore");
        channel.addListener(Platform.Type.SERVER, (data, connection, side) -> {
            if (connection instanceof PlayerConnection) {
                Player player = ((PlayerConnection) connection).getPlayer();
                int id = data.readInteger();
                if (id == 0) {
                    ClientPackets.HANDSHAKE.supplier.get().read(data, player);
                } else {
                    packetMapping.get(player).get(data.readInteger()).supplier.get().read(data, player);
                }
            }
        });
    }

    public static void init() {
        if (instance == null) instance = new FCServerNetworkManager();
    }

    public static FCServerNetworkManager instance() {
        return instance;
    }

    public Map<Player, Map<Integer, ClientPackets>> getPacketMapping() {
        return packetMapping;
    }

    public void sendPacket(Player player, IServerPacket serverPacket) {
        if (clientModPresent.get(player)) {
            channel.sendTo(player, load -> {
                load.writeInteger(ServerPackets.map.get(serverPacket.id()).ordinal());
                serverPacket.write(load);
            });
        }
    }


}
