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

import net.foxdenstudio.sponge.foxcore.common.network.client.listener.ServerPositionPacketListener;
import net.foxdenstudio.sponge.foxcore.common.network.client.listener.ServerPrintStringPacketListener;
import net.foxdenstudio.sponge.foxcore.common.network.server.packet.ServerPositionPacket;
import net.foxdenstudio.sponge.foxcore.common.network.server.packet.ServerPrintStringPacket;
import net.foxdenstudio.sponge.foxcore.mod.render.RenderHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = FoxCoreClientMain.MODID, name = "FoxCoreClient", clientSideOnly = true)
public class FoxCoreClientMain {

    public static final String MODID = "foxcoreclient";

    @Mod.Instance(MODID)
    public static FoxCoreClientMain instance;

    public static Logger logger;

    private RenderHandler renderHandler;
    private FCClientNetworkManager.ClientChannel foxcoreChannel;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        logger.info("Instantiating client network manager");
        FCClientNetworkManager manager = FCClientNetworkManager.instance();
        logger.info("Creating foxcore network channel");
        foxcoreChannel = manager.getOrCreateClientChannel("foxcore");
        logger.info("Registering server packet listeners");
        foxcoreChannel.registerListener(ServerPositionPacket.ID, new ServerPositionPacketListener());
        foxcoreChannel.registerListener(ServerPrintStringPacket.ID, new ServerPrintStringPacketListener());
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
        logger.info("Registering event handlers");
        MinecraftForge.EVENT_BUS.register(renderHandler = new RenderHandler(Minecraft.getMinecraft()));
        MinecraftForge.EVENT_BUS.register(this);
        logger.info("Registering MinecraftForge networking channels");
        FCClientNetworkManager.instance().registerNetworkingChannels();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }

    @EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        FCClientNetworkManager.instance().lock();
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        FCClientNetworkManager.instance().hasServer = false;
        FoxCoreClientMain.logger.info("Disco Fox!");
        this.renderHandler.updateList(null, null);
    }

    public RenderHandler getRenderHandler() {
        return renderHandler;
    }

    public FCClientNetworkManager.ClientChannel getFoxcoreChannel() {
        return foxcoreChannel;
    }
}
