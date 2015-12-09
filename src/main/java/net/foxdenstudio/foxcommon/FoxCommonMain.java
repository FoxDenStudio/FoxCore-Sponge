/*
 * This file is part of FoxCommon, licensed under the MIT License (MIT).
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

package net.foxdenstudio.foxcommon;

import com.google.inject.Inject;
import net.foxdenstudio.foxcommon.commands.*;
import net.foxdenstudio.foxcommon.state.FCStateRegistry;
import net.foxdenstudio.foxcommon.state.PositionsStateField;
import net.foxdenstudio.foxcommon.state.factory.PositionStateFieldFactory;
import net.foxdenstudio.foxcommon.util.Aliases;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;

@Plugin(id = "foxcommon", name = "FoxCommon", version = FoxCommonMain.PLUGIN_VERSION)
public class FoxCommonMain {

    public static final String PLUGIN_VERSION = "SNAPSHOT";

    private static FoxCommonMain instance;

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

    public static FoxCommonMain instance() {
        return instance;
    }

    @Listener
    public void gameConstruct(GameConstructionEvent event) {
        instance = this;
    }

    @Listener
    public void gamePreInit(GamePreInitializationEvent event) {
        FCStateRegistry.init();
    }

    @Listener
    public void gameInit(GameInitializationEvent event) {
        registerCommands();
        FCStateRegistry.instance().registerStateFactory(new PositionStateFieldFactory(), PositionsStateField.ID, Aliases.POSITIONS_ALIASES);
    }

    private void registerCommands() {
        FCCommandMainDispatcher fcDispatcher = new FCCommandMainDispatcher("/foxcommon");
        this.fcDispatcher = fcDispatcher;
        fcDispatcher.register(new CommandState(), "state", "current", "cur");
        fcDispatcher.register(new CommandPosition(), "position", "pos", "p");
        fcDispatcher.register(new CommandAdd(), "add", "push");
        fcDispatcher.register(new CommandSubtract(), "subtract", "sub", "pop");
        fcDispatcher.register(new CommandFlush(), "flush", "clear", "wipe");

        game.getCommandManager().register(this, fcDispatcher, "foxcommon", "foxc", "fcommon", "fc");
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
