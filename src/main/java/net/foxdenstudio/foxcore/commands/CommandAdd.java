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

package net.foxdenstudio.foxcore.commands;

import com.google.common.collect.ImmutableList;
import net.foxdenstudio.foxcore.commands.util.AdvCmdParse;
import net.foxdenstudio.foxcore.commands.util.ProcessResult;
import net.foxdenstudio.foxcore.state.IStateField;
import net.foxdenstudio.foxcore.util.FCHelper;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.Optional;

public class CommandAdd implements CommandCallable {

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        if (!testPermission(source)) {
            source.sendMessage(Texts.of(TextColors.RED, "You don't have permission to use this command!"));
            return CommandResult.empty();
        }
        AdvCmdParse parse = AdvCmdParse.builder().arguments(arguments).limit(1).parseLastFlags(false).build();
        String[] args = parse.getArgs();
        if (args.length == 0) {
            source.sendMessage(Texts.builder()
                    .append(Texts.of(TextColors.GREEN, "Usage: "))
                    .append(getUsage(source))
                    .build());
            return CommandResult.empty();
        }
        IStateField field = FCCommandMainDispatcher.getInstance().getStateMap().get(source).getFromAlias(args[0]);
        if (field == null) throw new CommandException(Texts.of("\"" + args[0] + "\" is not a valid category!"));
        String extraArgs = "";
        if (args.length > 1) extraArgs = args[1];
        ProcessResult result = field.add(source, extraArgs);
        if (result.isSuccess()) {
            if (result.getMessage().isPresent()) {
                if (!FCHelper.hasColor(result.getMessage().get())) {
                    source.sendMessage(result.getMessage().get().builder().color(TextColors.GREEN).build());
                } else {
                    source.sendMessage(result.getMessage().get());
                }
            } else {
                source.sendMessage(Texts.of(TextColors.GREEN, "Successfully added data to the " + field.getName() + " field!"));
            }
        } else {
            if (result.getMessage().isPresent()) {
                if (!FCHelper.hasColor(result.getMessage().get())) {
                    source.sendMessage(result.getMessage().get().builder().color(TextColors.RED).build());
                } else {
                    source.sendMessage(result.getMessage().get());
                }
            } else {
                source.sendMessage(Texts.of(TextColors.RED, "Failed to add data to the " + field.getName() + " field!"));
            }
        }
        return CommandResult.empty();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
        return ImmutableList.of();
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return source.hasPermission("foxcore.command.state.add") || source.hasPermission("foxguard.command.state.add");
    }

    @Override
    public Optional<? extends Text> getShortDescription(CommandSource source) {
        return Optional.empty();
    }

    @Override
    public Optional<? extends Text> getHelp(CommandSource source) {
        return Optional.empty();
    }

    @Override
    public Text getUsage(CommandSource source) {
        return Texts.of("add <region [--w:<worldname>] | handler> <name>");
    }
}
