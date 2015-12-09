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

package net.foxdenstudio.foxcore;

import com.google.inject.Inject;
import net.foxdenstudio.foxcore.commands.*;
import net.foxdenstudio.foxcore.state.FCStateRegistry;
import net.foxdenstudio.foxcore.state.PositionsStateField;
import net.foxdenstudio.foxcore.state.factory.PositionStateFieldFactory;
import net.foxdenstudio.foxcore.util.Aliases;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;

@Plugin(id = "foxcore", name = "FoxCore", version = FoxCoreMain.PLUGIN_VERSION)
public class FoxCoreMain {

    public static final String PLUGIN_VERSION = "SNAPSHOT";

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

    private FCCommandMainDispatcher fcDispatcher;

    public static FoxCoreMain instance() {
        return instance;
    }

    @Listener
    public void gameConstruct(GameConstructionEvent event) {
        instance = this;
    }

    @Listener
    public void gamePreInit(GamePreInitializationEvent event) {
        FCStateRegistry.init();
        registerCommands();
    }

    @Listener
    public void gameInit(GameInitializationEvent event) {
        game.getCommandManager().register(this, fcDispatcher, "foxcore", "foxc", "fcommon", "fc");
        FCStateRegistry.instance().registerStateFactory(new PositionStateFieldFactory(), PositionsStateField.ID, Aliases.POSITIONS_ALIASES);
    }

    private void registerCommands() {
        TextBuilder builder = Texts.builder();
        builder.append(Texts.of(TextColors.GOLD, "FoxCore\n"));
        builder.append(Texts.of("Version: " + FoxCoreMain.PLUGIN_VERSION + "\n"));
        builder.append(Texts.of("Author: gravityfox\n"));

        FCCommandMainDispatcher fcDispatcher = new FCCommandMainDispatcher("/foxcore");
        this.fcDispatcher = fcDispatcher;
        fcDispatcher.register(new CommandState(), "state", "current", "cur");
        fcDispatcher.register(new CommandPosition(), "position", "pos", "p");
        fcDispatcher.register(new CommandAdd(), "add", "push");
        fcDispatcher.register(new CommandSubtract(), "subtract", "sub", "pop");
        fcDispatcher.register(new CommandFlush(), "flush", "clear", "wipe");

        fcDispatcher.register(new CommandAbout(builder.build()), "about", "info");
    }

    public Logger logger() {
        return logger;
    }

    public Game game() {
        return game;
    }

    public FCCommandMainDispatcher getFCDispatcher() {
        return fcDispatcher;
    }
}
