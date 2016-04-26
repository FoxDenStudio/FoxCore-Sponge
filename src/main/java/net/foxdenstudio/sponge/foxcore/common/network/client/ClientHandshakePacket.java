package net.foxdenstudio.sponge.foxcore.common.network.client;

import io.netty.buffer.ByteBuf;
import net.foxdenstudio.sponge.foxcore.common.network.IClientPacket;
import net.foxdenstudio.sponge.foxcore.common.network.server.ServerPackets;
import net.foxdenstudio.sponge.foxcore.mod.network.FCClientNetworkManager;
import net.foxdenstudio.sponge.foxcore.plugin.network.FCServerNetworkManager;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBuf;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fox on 4/25/2016.
 */
public class ClientHandshakePacket implements IClientPacket {

    public static final String ID = "clienthandshake";

    public String plugin;
    public String version;
    public Map<Integer, String> packetMap;

    public ClientHandshakePacket(String plugin, String version) {
        this.plugin = plugin;
        this.version = version;
        this.packetMap = new HashMap<>();
        for (ClientPackets sp : ClientPackets.values()) {
            packetMap.put(sp.ordinal(), sp.id);
        }
    }

    public ClientHandshakePacket() {
        plugin = "";
        version = "";
    }

    @Override
    public void read(ChannelBuf payload, Player player) {
        plugin = payload.readString();
        version = payload.readString();
        packetMap = new HashMap<>();
        while (payload.available() > 4) {
            packetMap.put(payload.readInteger(), payload.readString());
        }

        Map<Integer, ClientPackets> clientMap = FCServerNetworkManager.instance().getPacketMapping().get(player);
        for (Map.Entry<Integer, String> entry : packetMap.entrySet()) {
            clientMap.put(entry.getKey(), ClientPackets.map.get(entry.getValue()));
        }
    }

    @Override
    public void write(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, plugin);
        ByteBufUtils.writeUTF8String(buf, version);
        for (Map.Entry<Integer, String> entry : packetMap.entrySet()) {
            buf.writeInt(entry.getKey());
            ByteBufUtils.writeUTF8String(buf, entry.getValue());
        }
    }

    @Override
    public String id() {
        return ID;
    }
}
