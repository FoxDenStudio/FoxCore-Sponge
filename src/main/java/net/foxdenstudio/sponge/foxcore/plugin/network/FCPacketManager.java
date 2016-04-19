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

import com.flowpowered.math.vector.Vector3i;
import net.foxdenstudio.sponge.foxcore.plugin.FoxCoreMain;
import org.spongepowered.api.Platform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBinding;

import java.util.List;

import static net.foxdenstudio.sponge.foxcore.common.network.server.ServerPackets.POSITION;
import static net.foxdenstudio.sponge.foxcore.common.network.server.ServerPackets.PRINTSTRING;

public class FCPacketManager {

    private static FCPacketManager instance;

    private static ChannelBinding.RawDataChannel channel;

    private FCPacketManager() {

        channel = FoxCoreMain.instance().game().getChannelRegistrar().createRawChannel(FoxCoreMain.instance(), "foxcore");
    }

    public static void init() {
        if (instance == null) instance = new FCPacketManager();
    }

    public static FCPacketManager instance() {
        return instance;
    }

    public void yerf(Player player) {
        FoxCoreMain.instance().logger().info("Saying hi to " + player.getName());
        channel.sendTo(player, load -> {
            load.writeInteger(PRINTSTRING.ID);
            load.writeString("Yerf. ^^ (I got told to change it. Again. x3)");
        });
    }

    public void sendPos(Player player, List<Vector3i> pos) {
        channel.sendTo(player, load -> {
            load.writeInteger(POSITION.ID);
            pos.forEach(vec -> load.writeInteger(vec.getX()).writeInteger(vec.getY()).writeInteger(vec.getZ()));
        });
    }
}
