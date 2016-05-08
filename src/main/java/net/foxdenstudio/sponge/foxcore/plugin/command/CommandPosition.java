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
import net.foxdenstudio.sponge.foxcore.common.FCUtil;
import net.foxdenstudio.sponge.foxcore.plugin.command.util.ProcessResult;
import net.foxdenstudio.sponge.foxcore.plugin.state.FCStateManager;
import net.foxdenstudio.sponge.foxcore.plugin.state.PositionStateField;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.Optional;

public class CommandPosition implements CommandCallable {

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        if (!testPermission(source)) {
            source.sendMessage(Text.of(TextColors.RED, "You don't have permission to use this command!"));
            return CommandResult.empty();
        }
        PositionStateField positionsField = (PositionStateField) FCStateManager.instance().getStateMap().get(source).getOrCreate(PositionStateField.ID).get();
        ProcessResult result = positionsField.add(source, arguments);
        if (result.isSuccess()) {
            if (result.getMessage().isPresent()) {
                if (!FCUtil.hasColor(result.getMessage().get())) {
                    source.sendMessage(result.getMessage().get().toBuilder().color(TextColors.GREEN).build());
                } else {
                    source.sendMessage(result.getMessage().get());
                }
            } else {
                source.sendMessage(Text.of(TextColors.GREEN, "Successfully added data to the Positions field!"));
            }
        } else {
            if (result.getMessage().isPresent()) {
                if (!FCUtil.hasColor(result.getMessage().get())) {
                    source.sendMessage(result.getMessage().get().toBuilder().color(TextColors.RED).build());
                } else {
                    source.sendMessage(result.getMessage().get());
                }
            } else {
                source.sendMessage(Text.of(TextColors.RED, "Failed to add data to the Positions field!"));
            }
        }
        return CommandResult.empty();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
        if (!testPermission(source)) return ImmutableList.of();
        PositionStateField positionsField = (PositionStateField) FCStateManager.instance().getStateMap().get(source).getOrCreate(PositionStateField.ID).get();
        return positionsField.addSuggestions(source, arguments);
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return source.hasPermission("foxcore.command.state.add.position");
    }

    @Override
    public Optional<? extends Text> getShortDescription(CommandSource source) {
        return Optional.of(Text.of("Shorthand command for working with the positions field."));
    }

    @Override
    public Optional<? extends Text> getHelp(CommandSource source) {
        return Optional.of(Text.of("Adds positions from the positions state field. You can specify coordinates."));
    }

    @Override
    public Text getUsage(CommandSource source) {
        if (source instanceof Player)
            return Text.of("position [<x> <y> <z>]");
        else return Text.of("position <x> <y> <z>");
    }

}
