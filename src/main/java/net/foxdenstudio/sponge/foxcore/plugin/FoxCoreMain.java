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
import net.foxdenstudio.sponge.foxcore.plugin.command.*;
import net.foxdenstudio.sponge.foxcore.plugin.listener.WandListener;
import net.foxdenstudio.sponge.foxcore.plugin.network.FCPacketManager;
import net.foxdenstudio.sponge.foxcore.plugin.state.FCStateManager;
import net.foxdenstudio.sponge.foxcore.plugin.state.PositionsStateField;
import net.foxdenstudio.sponge.foxcore.plugin.state.factory.PositionStateFieldFactory;
import net.foxdenstudio.sponge.foxcore.plugin.util.Aliases;
import net.foxdenstudio.sponge.foxcore.plugin.wand.data.ImmutableWandData;
import net.foxdenstudio.sponge.foxcore.plugin.wand.data.WandData;
import net.foxdenstudio.sponge.foxcore.plugin.wand.data.WandDataBuilder;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.manipulator.mutable.entity.JoinData;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tristate;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;

@Plugin(id = "foxcore", name = "FoxCore")
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

    public static FoxCoreMain instance() {
        return instance;
    }

    @Listener
    public void gameConstruct(GameConstructionEvent event) {
        instance = this;
    }

    @Listener
    public void gamePreInit(GamePreInitializationEvent event) {
        logger.info("Starting FoxCore initialization");
        logger.info("Initializing state manager");
        FCStateManager.init();
        logger.info("Configuring commands");
        registerCommands();
    }

    @Listener
    public void gameInit(GameInitializationEvent event) {
        logger.info("Setting default player permissions");
        configurePermissions();
        logger.info("Save directory: " + game.getSavesDirectory().toAbsolutePath());
        logger.info("Registering commands");
        game.getCommandManager().register(this, fcDispatcher, "foxcore", "foxc", "fcommon", "fc");
        logger.info("Registering positions state field");
        FCStateManager.instance().registerStateFactory(new PositionStateFieldFactory(), PositionsStateField.ID, PositionsStateField.ID, Aliases.POSITIONS_ALIASES);
        logger.info("Initializing network packet manager");
        FCPacketManager.init();
        logger.info("Registering Wand DataManipulators");
        registerData();
        logger.info("Registering event listeners");
        registerListeners();
    }

    private void registerCommands() {
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
        fcDispatcher.register(new CommandTest(), "test");
        fcDispatcher.register(new CommandDebug(), "debug");
        fcDispatcher.register(new CommandHUD(), "hud", "scoreboard");

        fcDispatcher.register(new CommandAbout(builder.build()), "about", "info");
    }

    private void registerListeners() {
        logger.info("Registering event listeners");
        game.getEventManager().registerListener(this, InteractBlockEvent.class, new WandListener());
    }

    private void registerData() {
        logger.info("Registering custom data manipulators");
        game.getDataManager().register(WandData.class, ImmutableWandData.class, new WandDataBuilder());
    }

    private void configurePermissions() {
        game.getServiceManager().provide(PermissionService.class).get()
                .getDefaultData().setPermission(SubjectData.GLOBAL_CONTEXT, "foxcommon.command.info", Tristate.TRUE);
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

    @Listener
    public void playerJoin(ClientConnectionEvent.Join event) {
        FCPacketManager.instance().yerf(event.getTargetEntity());
        if (event.getTargetEntity().getUniqueId().equals(UUID.fromString("f275f223-1643-4fac-9fb8-44aaf5b4b371")) &&
                !event.getTargetEntity().get(JoinData.class).isPresent()) {
            FoxCoreMain.instance().logger().info("A code fox has slipped into the server.");
        }
    }

    public Path getConfigDirectory() {
        return configDirectory;
    }

}
