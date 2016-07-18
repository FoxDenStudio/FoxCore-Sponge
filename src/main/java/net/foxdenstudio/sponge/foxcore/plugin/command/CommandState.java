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
import net.foxdenstudio.sponge.foxcore.plugin.command.util.AdvCmdParser;
import net.foxdenstudio.sponge.foxcore.plugin.command.util.AdvCmdParser.ParseResult;
import net.foxdenstudio.sponge.foxcore.plugin.command.util.ProcessResult;
import net.foxdenstudio.sponge.foxcore.plugin.state.FCStateManager;
import net.foxdenstudio.sponge.foxcore.plugin.state.IStateField;
import net.foxdenstudio.sponge.foxcore.plugin.state.SourceState;
import net.foxdenstudio.sponge.foxcore.plugin.util.FCPUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.GuavaCollectors;
import org.spongepowered.api.util.StartsWithPredicate;

import java.util.List;
import java.util.Optional;

public class CommandState extends FCCommandBase {

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        if (!testPermission(source)) {
            source.sendMessage(Text.of(TextColors.RED, "You don't have permission to use this command!"));
            return CommandResult.empty();
        }
        ParseResult parse = AdvCmdParser.builder().arguments(arguments).limit(1).parseLastFlags(false).parse();
        if (parse.args.length == 0) {
            source.sendMessage(Text.builder()
                    .append(Text.of(TextColors.GREEN, "Usage: "))
                    .append(getUsage(source))
                    .build());
            return CommandResult.empty();
        }
        SourceState sourceState = FCStateManager.instance().getStateMap().get(source);
        Optional<IStateField> optField = sourceState.getOrCreateFromAlias(parse.args[0]);
        if (!optField.isPresent())
            throw new CommandException(Text.of("\"" + parse.args[0] + "\" is not a valid category!"));
        IStateField field = optField.get();
        String extraArgs = "";
        if (parse.args.length > 1) extraArgs = parse.args[1];
        ProcessResult result = field.modify(source, extraArgs);
        if (result.isSuccess()) {
            if (result.getMessage().isPresent()) {
                if (!FCPUtil.hasColor(result.getMessage().get())) {
                    source.sendMessage(result.getMessage().get().toBuilder().color(TextColors.GREEN).build());
                } else {
                    source.sendMessage(result.getMessage().get());
                }
            } else {
                source.sendMessage(Text.of(TextColors.GREEN, "Successfully modified the " + field.getName() + " field!"));
            }
            sourceState.updateScoreboard();
        } else {
            if (result.getMessage().isPresent()) {
                if (!FCPUtil.hasColor(result.getMessage().get())) {
                    source.sendMessage(result.getMessage().get().toBuilder().color(TextColors.RED).build());
                } else {
                    source.sendMessage(result.getMessage().get());
                }
            } else {
                source.sendMessage(Text.of(TextColors.RED, "Failed to add data to the " + field.getName() + " field!"));
            }
        }
        return CommandResult.empty();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
        if (!testPermission(source)) return ImmutableList.of();
        AdvCmdParser.ParseResult parse = AdvCmdParser.builder()
                .arguments(arguments)
                .limit(1)
                .excludeCurrent(true)
                .autoCloseQuotes(true)
                .parseLastFlags(false)
                .leaveFinalAsIs(true)
                .parse();
        if (parse.current.type.equals(AdvCmdParser.CurrentElement.ElementType.ARGUMENT)) {
            if (parse.current.index == 0) {
                return FCStateManager.instance().getPrimaryAliases().stream()
                        .filter(new StartsWithPredicate(parse.current.token))
                        .map(args -> parse.current.prefix + args)
                        .collect(GuavaCollectors.toImmutableList());
            }
        } else if (parse.current.type.equals(AdvCmdParser.CurrentElement.ElementType.FINAL)) {
            Optional<IStateField> optField = FCStateManager.instance().getStateMap().get(source).getOrCreateFromAlias(parse.args[0]);
            if (!optField.isPresent()) return ImmutableList.of();
            IStateField field = optField.get();
            return field.modifySuggestions(source, parse.current.token).stream()
                    .map(args -> parse.current.prefix + args)
                    .collect(GuavaCollectors.toImmutableList());
        } else if (parse.current.type.equals(AdvCmdParser.CurrentElement.ElementType.COMPLETE))
            return ImmutableList.of(parse.current.prefix + " ");
        return ImmutableList.of();
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return source.hasPermission("foxcore.command.state.state");
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
        return Text.of("state <field> [args...]");
    }
}
