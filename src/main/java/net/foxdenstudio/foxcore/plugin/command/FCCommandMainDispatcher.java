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

package net.foxdenstudio.foxcore.plugin.command;

import net.foxdenstudio.foxcore.plugin.command.util.SourceState;
import net.foxdenstudio.foxcore.plugin.util.CallbackHashMap;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.dispatcher.Disambiguator;
import org.spongepowered.api.command.dispatcher.SimpleDispatcher;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;

import java.util.Map;
import java.util.Optional;

public final class FCCommandMainDispatcher extends FCCommandDispatcher {

    private static FCCommandMainDispatcher instance;
    private final Map<CommandSource, SourceState> stateMap = new CallbackHashMap<>((o, m) -> {
        if (o instanceof CommandSource) {
            SourceState state = new SourceState();
            m.put((CommandSource) o, state);
            return state;
        }
        return null;
    });

    public FCCommandMainDispatcher(String primaryAlias) {
        this(primaryAlias, SimpleDispatcher.FIRST_DISAMBIGUATOR);
    }

    public FCCommandMainDispatcher(String primaryAlias, Disambiguator disambiguatorFunc) {
        super(primaryAlias, null, disambiguatorFunc);
        instance = this;
    }

    public static FCCommandMainDispatcher getInstance() {
        return instance;
    }

    public Map<CommandSource, SourceState> getStateMap() {
        return stateMap;
    }

    @Override
    public Optional<? extends Text> getShortDescription(CommandSource source) {
        return Optional.of(Texts.of("FoxGuard commands for managing world protection. Use /help foxguard for subcommands."));
    }
}
