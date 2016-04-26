package net.foxdenstudio.sponge.foxcore.common.network.client;

import net.foxdenstudio.sponge.foxcore.common.network.IClientPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by Fox on 4/25/2016.
 */
public enum ClientPackets {

    HANDSHAKE(ClientHandshakePacket.ID, ClientHandshakePacket::new);

    public final String id;
    public final Supplier<IClientPacket> supplier;

    public static final Map<String, ClientPackets> map = new HashMap<>();

    ClientPackets(String id, Supplier<IClientPacket> supplier) {
        this.id = id;
        this.supplier = supplier;
    }

    static {
        for (ClientPackets sp : ClientPackets.values()) {
            map.put(sp.id, sp);
        }
    }

}
