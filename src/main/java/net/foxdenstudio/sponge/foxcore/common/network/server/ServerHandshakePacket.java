package net.foxdenstudio.sponge.foxcore.common.network.server;

import io.netty.buffer.ByteBuf;
import net.foxdenstudio.sponge.foxcore.common.network.IServerPacket;
import net.foxdenstudio.sponge.foxcore.common.network.client.ClientHandshakePacket;
import net.foxdenstudio.sponge.foxcore.mod.network.FCClientNetworkManager;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import org.spongepowered.api.network.ChannelBuf;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fox on 4/18/2016.
 */
public class ServerHandshakePacket implements IServerPacket {

    public static final String ID = "serverhandshake";

    public String plugin;
    public String version;
    public Map<Integer, String> packetMap;

    public ServerHandshakePacket(String plugin, String version) {
        this.plugin = plugin;
        this.version = version;
        this.packetMap = new HashMap<>();
        for (ServerPackets sp : ServerPackets.values()) {
            packetMap.put(sp.ordinal(), sp.id);
        }
    }

    public ServerHandshakePacket() {
        plugin = "";
        version = "";
    }

    @Override
    public void read(ByteBuf payload) {
        plugin = ByteBufUtils.readUTF8String(payload);
        version = ByteBufUtils.readUTF8String(payload);
        packetMap = new HashMap<>();
        while (payload.isReadable(5)) {
            packetMap.put(payload.readInt(), ByteBufUtils.readUTF8String(payload));
        }

        Map<Integer, ServerPackets> serverMap = FCClientNetworkManager.instance().getPacketMapping();
        for (Map.Entry<Integer, String> entry : packetMap.entrySet()) {
            serverMap.put(entry.getKey(), ServerPackets.map.get(entry.getValue()));
        }

        FCClientNetworkManager.instance().sendPacket(new ClientHandshakePacket("foxcore", Loader.instance().getIndexedModList().get("foxcore").getVersion()));
    }

    @Override
    public void write(ChannelBuf buf) {
        buf.writeString(plugin);
        buf.writeString(version);
        for (Map.Entry<Integer, String> entry : packetMap.entrySet()) {
            buf.writeInteger(entry.getKey());
            buf.writeString(entry.getValue());
        }
    }

    @Override
    public String id() {
        return ID;
    }
}
