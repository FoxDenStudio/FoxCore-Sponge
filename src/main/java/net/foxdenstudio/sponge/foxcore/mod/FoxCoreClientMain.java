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
import net.foxdenstudio.sponge.foxcore.mod.rendernew.windows.RenderUtils;
import net.foxdenstudio.sponge.foxcore.mod.windows.Registry;
import net.foxdenstudio.sponge.foxcore.mod.windows.RenderManager;
import net.foxdenstudio.sponge.foxcore.mod.windows.examples.BasicWindow;
import net.foxdenstudio.sponge.foxcore.mod.windows.parts.BasePart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.input.Keyboard.*;

@Mod(modid = FoxCoreClientMain.MODID, name = "FoxCoreClient", clientSideOnly = true)
public class FoxCoreClientMain {

    public static final String MODID = "foxcoreclient";
    @Mod.Instance(MODID)
    public static FoxCoreClientMain instance;
    public static Logger logger;
    private final KeyBinding windowControlKeyBinding = new KeyBinding("Window Control Key", KEY_GRAVE, "FoxCore");
    private final HashMap<Integer, List<Consumer<Integer>>> keyBindings = new HashMap<>();
    private final HashMap<Integer, List<Consumer<Integer>>> mouseBindings = new HashMap<>();
    private boolean windowControlActive = false;
    private RenderHandler renderHandler;
    private FCClientNetworkManager.ClientChannel foxcoreChannel;
    private RenderManager renderManager;
    private int eventButton;
    private long lastMouseEvent;
    private BasePart lastBasePart;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        logger.info("Instantiating client network manager");
        FCClientNetworkManager manager = FCClientNetworkManager.instance();
        logger.info("Creating foxcore network channel");
        this.foxcoreChannel = manager.getOrCreateClientChannel("foxcore");
        logger.info("Registering server packet listeners");
        this.foxcoreChannel.registerListener(ServerPositionPacket.ID, new ServerPositionPacketListener());
        this.foxcoreChannel.registerListener(ServerPrintStringPacket.ID, new ServerPrintStringPacketListener());
        registerKeyBinds();
        registerMouseBinds();
    }

    private void registerKeyBinds() {
        System.out.println("(Re)Registering Key Bindings!");
        this.keyBindings.clear();
        addKeyBinding(KEY_E, System.out::println);
        addKeyBinding(KEY_S, System.err::println);
        addKeyBinding(KEY_K, integer -> {
            final BasicWindow basicWindow = new BasicWindow();
            basicWindow.setPositionX(25).setPositionY(25).setWidth(150).setHeight(150);
            basicWindow.setTitle("FoxEdit Info").revalidate();
            System.out.println("Adding window: " + basicWindow);
            Registry.getInstance().addParts(basicWindow);
        });
    }

    private void registerMouseBinds() {
        System.out.println("(Re)Registering Mouse Bindings!");
        this.mouseBindings.clear();
        addMouseBinding(0, System.out::println);
    }

    private void addKeyBinding(int key, Consumer<Integer> consumer) {
        if (!this.keyBindings.containsKey(key)) {
            this.keyBindings.put(key, new ArrayList<>());
        }
        this.keyBindings.get(key).add(consumer);
    }

    private void addMouseBinding(int mouseButton, Consumer<Integer> consumer) {
        if (!this.mouseBindings.containsKey(mouseButton)) {
            this.mouseBindings.put(mouseButton, new ArrayList<>());
        }
        this.mouseBindings.get(mouseButton).add(consumer);
    }

    @EventHandler
    public void loadS(FMLInitializationEvent event) {
        logger.info("Registering event handlers");
        MinecraftForge.EVENT_BUS.register(this.renderHandler = new RenderHandler(Minecraft.getMinecraft()));
        MinecraftForge.EVENT_BUS.register(this.renderManager = new RenderManager());
//        MinecraftForge.EVENT_BUS.register(new GuiRenderListener());
        //MinecraftForge.EVENT_BUS.register(RenderManager.instance());
        MinecraftForge.EVENT_BUS.register(this);
        logger.info("Registering MinecraftForge networking channels");
        FCClientNetworkManager.instance().registerNetworkingChannels();
        ClientRegistry.registerKeyBinding(this.windowControlKeyBinding);
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
        return this.renderHandler;
    }

    public FCClientNetworkManager.ClientChannel getFoxcoreChannel() {
        return this.foxcoreChannel;
    }

    @SubscribeEvent
    public void onTickEvent(TickEvent event) {
        if (this.windowControlActive) {
            while (Mouse.next()) {
                final Point point = RenderUtils.calculateMouseLocation();
                final BasePart basePart = Registry.getInstance().getPartUnder(point.x, point.y);
//                System.out.println(basePart);
//                System.out.println(basePart);
//                int dx = Mouse.getDX(), dy = -Mouse.getDY();
//
                if (basePart != null) {
                    this.lastBasePart = basePart;
                }
//

                int k = Mouse.getEventButton();

                if (Mouse.getEventButtonState()) {
                    this.eventButton = k;
                    this.lastMouseEvent = Minecraft.getSystemTime();
//                    this.mouseClicked(i, j, this.eventButton);
//                    logger.debug("Mouse Clicked: " + point.x + " | " + point.y + " | " + this.eventButton);
                    if (this.lastBasePart != null) {
                        Registry.getInstance().getPartList().use(this.lastBasePart);
                        this.lastBasePart.mouseClicked(point.x - this.lastBasePart.getPositionX(), point.y - this.lastBasePart.getPositionY(), k);
                    }
                } else if (k != -1) {
                    this.eventButton = -1;
//                    logger.debug("Mouse Release: " + point.x + " | " + point.y + " | " + k);
                    if (this.lastBasePart != null) {
                        this.lastBasePart.mouseReleased(point.x - this.lastBasePart.getPositionX(), point.y - this.lastBasePart.getPositionY(), k);
                    }
                    this.lastBasePart = null;
                } else if (this.eventButton != -1 && this.lastMouseEvent > 0L) {
                    long l = Minecraft.getSystemTime() - this.lastMouseEvent;
//                    this.mouseClickMove(i, j, this.eventButton, l);
//                    logger.debug("Mouse Dragged: " + point.x + " | " + point.y + " | " + this.eventButton + " | " + l);
                    if (l > 100 && this.lastBasePart != null) {
                        this.lastBasePart.mouseDrag(point.x - this.lastBasePart.getPositionX(), point.y - this.lastBasePart.getPositionY(), this.eventButton);
                    }
                }
            }
            while (Keyboard.next()) {
                if (Keyboard.isKeyDown(Keyboard.getEventKey())) {
                    if (Keyboard.getEventKey() == KEY_ESCAPE || Keyboard.getEventKey() == this.windowControlKeyBinding.getKeyCode()) {
                        this.windowControlActive = false;
                        Minecraft.getMinecraft().setIngameFocus();
                        KeyBinding.unPressAllKeys();
                    } else if (Keyboard.getEventKey() == KEY_BACKSLASH) {
                        if (Keyboard.isKeyDown(KEY_RSHIFT)) {
                            registerKeyBinds();
                            registerMouseBinds();
                        }
                    } else {
                        if (this.keyBindings.containsKey(Keyboard.getEventKey())) {
                            this.keyBindings.get(Keyboard.getEventKey()).iterator().forEachRemaining(con -> con.accept(Keyboard.getEventKey()));
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        if (this.windowControlKeyBinding.isPressed()) {
            if (!this.windowControlActive) {
                Minecraft.getMinecraft().setIngameNotInFocus();
                this.windowControlActive = true;
                KeyBinding.unPressAllKeys();
            }
        }
    }

    public boolean getIsWindowControlActive() {
        return this.windowControlActive;
    }
}
