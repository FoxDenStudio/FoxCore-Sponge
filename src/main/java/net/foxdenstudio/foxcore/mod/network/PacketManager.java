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

package net.foxdenstudio.foxcore.mod.network;

import io.netty.channel.ChannelHandler;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;

public class PacketManager {

    public static PacketManager instance;

    public final FMLEmbeddedChannel channelInstance;

    private PacketManager() {

        if (NetworkRegistry.INSTANCE.hasChannel("foxcore", Side.CLIENT)) {
            this.channelInstance = NetworkRegistry.INSTANCE.getChannel("foxcore", Side.CLIENT);
            for (Map.Entry<String, ChannelHandler> entry : this.channelInstance.pipeline().toMap().entrySet()) {
                if (entry.getKey().contains("Sponge")) {
                    channelInstance.pipeline().replace(entry.getValue(), "foxcorehandler", new StringPrinter());
                    break;
                }
            }
        } else {
            this.channelInstance = NetworkRegistry.INSTANCE.newChannel("foxcore", new StringPrinter()).get(Side.CLIENT);
        }
        System.out.println("------------------------------------------------------------");
        System.out.println(channelInstance.pipeline().toMap());
        System.out.println("------------------------------------------------------------");
        System.out.println(NetworkRegistry.INSTANCE.newChannel("asdftest", new StringPrinter()).get(Side.CLIENT).pipeline().toMap());
        System.out.println("------------------------------------------------------------");
    }

    public static PacketManager instance() {
        if (instance == null) instance = new PacketManager();
        return instance;
    }


}
