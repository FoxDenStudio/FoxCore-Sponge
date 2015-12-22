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
import net.foxdenstudio.sponge.foxcore.plugin.event.WandListener;
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
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tristate;

import java.io.File;

@Plugin(id = "foxcore", name = "FoxCore", version = FoxCoreMain.VERSION)
public final class FoxCoreMain {

    public static final String VERSION = "0.3.0-SNAPSHOT";//VERSION

    private static FoxCoreMain instance;

    @Inject
    private Logger logger;
    @Inject
    private Game game;
    @Inject
    private EventManager eventManager;
    @Inject
    @ConfigDir(sharedRoot = true)
    private File configDirectory;

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
        logger.info("Setting default player permissions");
        configurePermissions();

    }

    @Listener
    public void gameInit(GameInitializationEvent event) {
        logger.info("Save directory: " + game.getSavesDirectory().toAbsolutePath());
        logger.info("Registering commands");
        game.getCommandManager().register(this, fcDispatcher, "foxcore", "foxc", "fcommon", "fc");
        logger.info("Registering positions state field");
        FCStateManager.instance().registerStateFactory(new PositionStateFieldFactory(), PositionsStateField.ID, Aliases.POSITIONS_ALIASES);
        logger.info("Initializing network packet manager");
        FCPacketManager.init();
        registerData();
        registerListeners();
    }

    private void registerCommands() {
        TextBuilder builder = Texts.builder();
        builder.append(Texts.of(TextColors.GOLD, "FoxCore\n"));
        builder.append(Texts.of("Version: " + FoxCoreMain.VERSION + "\n"));
        builder.append(Texts.of("Author: gravityfox\n"));

        this.fcDispatcher = new FCCommandDispatcher("/foxcore", "Core commands for state and selections.");
        fcDispatcher.register(new CommandState(), "state", "current", "cur");
        fcDispatcher.register(new CommandPosition(), "position", "pos", "p");
        fcDispatcher.register(new CommandAdd(), "add", "push");
        fcDispatcher.register(new CommandSubtract(), "subtract", "sub", "pop");
        fcDispatcher.register(new CommandFlush(), "flush", "clear", "wipe");
        fcDispatcher.register(new CommandWand(), "wand", "tool", "stick", "w");
        fcDispatcher.register(new CommandTest(), "test");

        fcDispatcher.register(new CommandAbout(builder.build()), "about", "info");
    }

    private void registerListeners(){
        logger.info("Registering event listeners");
        game.getEventManager().registerListener(this, InteractBlockEvent.class, new WandListener());
    }

    private void registerData(){
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
        FCPacketManager.instance().yiff(event.getTargetEntity());
    }
}
