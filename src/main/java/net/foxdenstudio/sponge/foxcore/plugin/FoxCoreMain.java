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

import com.google.inject.Inject;
import net.foxdenstudio.sponge.foxcore.common.network.server.packet.ServerPositionPacket;
import net.foxdenstudio.sponge.foxcore.common.network.server.packet.ServerPrintStringPacket;
import net.foxdenstudio.sponge.foxcore.plugin.command.*;
import net.foxdenstudio.sponge.foxcore.plugin.listener.WandBlockListener;
import net.foxdenstudio.sponge.foxcore.plugin.listener.WandEntityListener;
import net.foxdenstudio.sponge.foxcore.plugin.state.FCStateManager;
import net.foxdenstudio.sponge.foxcore.plugin.state.PositionStateField;
import net.foxdenstudio.sponge.foxcore.plugin.util.Aliases;
import net.foxdenstudio.sponge.foxcore.plugin.wand.FCWandRegistry;
import net.foxdenstudio.sponge.foxcore.plugin.wand.data.ImmutableWandData;
import net.foxdenstudio.sponge.foxcore.plugin.wand.data.WandData;
import net.foxdenstudio.sponge.foxcore.plugin.wand.data.WandDataBuilder;
import net.foxdenstudio.sponge.foxcore.plugin.wand.types.CounterWand;
import net.foxdenstudio.sponge.foxcore.plugin.wand.types.PositionWand;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tristate;

import java.nio.file.Path;
import java.util.UUID;

@Plugin(id = "foxcore",
        name = "FoxCore",
        description = "Core plugin for Fox plugins. This plugin also contains some core functionality that other plugins may wish to use.",
        authors = {"gravityfox"},
        url = "https://github.com/FoxDenStudio/FoxCore"
)
public final class FoxCoreMain {

    private static FoxCoreMain instance;

    @Inject
    private Logger logger;
    @Inject
    private Game game;
    @Inject
    private EventManager eventManager;
    @Inject
    @ConfigDir(sharedRoot = true)
    private Path configDirectory;
    @Inject
    private PluginContainer container;

    private FCCommandDispatcher fcDispatcher;

    private FCServerNetworkManager.ServerChannel foxcoreNetworkChannel;

    public static FoxCoreMain instance() {
        return instance;
    }

    @Listener
    public void construct(GameConstructionEvent event) {
        instance = this;
    }

    @Listener
    public void preInit(GamePreInitializationEvent event) {
        logger.info("Beginning FoxCore initialization");
        logger.info("Version: " + container.getVersion().orElse("Unknown"));
        logger.info("Initializing state manager");
        FCStateManager.init();
        logger.info("Initializing network packet manager");
        FCServerNetworkManager.instance();
        logger.info("Configuring commands");
        configureCommands();
    }

    @Listener
    public void init(GameInitializationEvent event) {
        logger.info("Starting network packet manager");
        FCServerNetworkManager.instance().registerNetworkingChannels();
        logger.info("Creating server network channel");
        foxcoreNetworkChannel = FCServerNetworkManager.instance().getOrCreateServerChannel("foxcore");
        logger.info("Registering packet listeners");
        registerPackets();
        logger.info("Registering positions state field");
        FCStateManager.instance().registerStateFactory(new PositionStateField.Factory(), PositionStateField.ID, PositionStateField.ID, Aliases.POSITIONS_ALIASES);
        logger.info("Registering wand factories");
        registerWands();
        logger.info("Registering commands");
        game.getCommandManager().register(this, fcDispatcher, "foxcore", "foxc", "fcommon", "fc");
    }

    @Listener
    public void registerListeners(GameInitializationEvent event) {
        logger.info("Registering event listeners");
        EventManager manager = game.getEventManager();
        try {
            manager.registerListeners(this, FCServerNetworkManager.instance());
        } catch (Exception e) {
            logger.error("Error registering Network Manager Listeners", e);
        }
        try {
            manager.registerListener(this, InteractBlockEvent.class, new WandBlockListener());
        } catch (Exception e) {
            logger.error("Error registering Wand Block Listener", e);
        }
        try {
            manager.registerListener(this, InteractEntityEvent.class, new WandEntityListener());
        } catch (Exception e){
            logger.error("Error registering Wand Entity Listener", e);
        }
    }

    @Listener
    public void registerData(GameInitializationEvent event) {
        logger.info("Registering custom data manipulators");
        game.getDataManager().register(WandData.class, ImmutableWandData.class, new WandDataBuilder());
    }

    @Listener
    public void configurePermissions(GamePostInitializationEvent event) {
        logger.info("Configuring permissions");
        game.getServiceManager().provide(PermissionService.class).get().getDefaults()
                .getSubjectData().setPermission(SubjectData.GLOBAL_CONTEXT, "foxcore.command.info", Tristate.TRUE);
    }

    private void configureCommands() {
        Text.Builder builder = Text.builder();
        builder.append(Text.of(TextColors.GOLD, "FoxCore\n"));
        builder.append(Text.of("Version: " + container.getVersion().orElse("Unknown") + "\n"));
        builder.append(Text.of("Author: gravityfox\n"));

        this.fcDispatcher = new FCCommandDispatcher("/foxcore", "Core commands for state and selections.");
        fcDispatcher.register(new CommandCurrent(), "current", "cur", "c");
        fcDispatcher.register(new CommandState(), "state", "buffer", "set", "s");
        fcDispatcher.register(new CommandPosition(), "position", "pos", "p");
        fcDispatcher.register(new CommandFlush(), "flush", "clear", "wipe", "f");
        fcDispatcher.register(new CommandWand(), "wand", "tool", "stick", "w");
        //fcDispatcher.register(new CommandTest(), "test");
        fcDispatcher.register(new CommandDebug(), "debug");
        fcDispatcher.register(new CommandHUD(), "hud", "scoreboard");

        fcDispatcher.register(new CommandAbout(builder.build()), "about", "info");
    }

    private void registerPackets() {
        FCServerNetworkManager manager = FCServerNetworkManager.instance();
        manager.registerPacket(ServerPositionPacket.ID);
        manager.registerPacket(ServerPrintStringPacket.ID);
    }

    private void registerWands() {
        FCWandRegistry registry = FCWandRegistry.getInstance();
        registry.registerBuilder(PositionWand.type, new PositionWand.Factory());
        registry.registerBuilder(CounterWand.type, new CounterWand.Factory());
    }

    public Logger logger() {
        return logger;
    }

    public Game game() {
        return game;
    }

    public FCCommandDispatcher getFCDispatcher() {
        return fcDispatcher;
    }

    public FCServerNetworkManager.ServerChannel getFoxcoreNetworkChannel() {
        return foxcoreNetworkChannel;
    }

    private static final UUID FOX_UUID = UUID.fromString("f275f223-1643-4fac-9fb8-44aaf5b4b371");

    @Listener
    public void playerJoin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();
        if (player.getUniqueId().equals(FOX_UUID)) {
            logger.info("A code fox has slipped into the server.");
        }
        FCServerNetworkManager manager = FCServerNetworkManager.instance();
        manager.negotiateHandshake(player);
    }

    public Path getConfigDirectory() {
        return configDirectory;
    }

    public PluginContainer getContainer() {
        return container;
    }
}