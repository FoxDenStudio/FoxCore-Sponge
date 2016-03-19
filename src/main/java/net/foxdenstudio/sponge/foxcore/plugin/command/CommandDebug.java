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

package net.foxdenstudio.sponge.foxcore.plugin.command;

import com.google.common.collect.ImmutableList;
import net.foxdenstudio.sponge.foxcore.plugin.util.CacheMap;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CommandDebug implements CommandCallable {

    private static CommandDebug instance;
    private Map<CommandSource, Boolean> debug = new CacheMap<>((k, m) -> Boolean.FALSE);

    public CommandDebug() {
        if (instance == null) instance = this;
    }

    public static CommandDebug instance() {
        return instance;
    }

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        if (debug.get(source).equals(Boolean.FALSE)) {
            debug.put(source, Boolean.TRUE);
            source.sendMessage(Text.of(TextColors.GREEN, "Debug mode enabled!"));
        } else {
            debug.remove(source);
            source.sendMessage(Text.of(TextColors.RED, "Debug mode disabled!"));
        }
        return CommandResult.empty();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
        return ImmutableList.of();
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return source.hasPermission("foxcore.command.debug.debug");
    }

    @Override
    public Optional<? extends Text> getShortDescription(CommandSource source) {
        return Optional.of(Text.of("Toggles debug mode."));
    }

    @Override
    public Optional<? extends Text> getHelp(CommandSource source) {
        return Optional.of(Text.of("Enabling debug mode gives additional output from the plugin.\n" +
                "While debug output may actually be useful to server owners, it is not an official feature."));
    }

    @Override
    public Text getUsage(CommandSource source) {
        return Text.of("debug");
    }

    public Map<CommandSource, Boolean> getDebug() {
        return debug;
    }

    @Listener
    public void onPlayerDisconnect(ClientConnectionEvent.Disconnect event) {
        this.debug.remove(event.getTargetEntity());
    }
}
