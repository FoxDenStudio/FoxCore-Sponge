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
import net.foxdenstudio.sponge.foxcore.plugin.state.FCStateManager;
import net.foxdenstudio.sponge.foxcore.plugin.state.IStateField;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.GuavaCollectors;
import org.spongepowered.api.util.StartsWithPredicate;

import java.util.*;

import static net.foxdenstudio.sponge.foxcore.plugin.util.Aliases.isIn;

public class CommandCurrent implements CommandCallable {

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        if (!testPermission(source)) {
            source.sendMessage(Text.of(TextColors.RED, "You don't have permission to use this command!"));
            return CommandResult.empty();
        }
        AdvCmdParser.ParseResult parse = AdvCmdParser.builder().arguments(arguments).parse();
        Text.Builder output = Text.builder().append(Text.of(TextColors.GOLD, "\n-----------------------------------------------------\n"));
        int flag = 0;
        Collection<IStateField> fields;
        if (parse.args.length == 0) {
            fields = FCStateManager.instance().getStateMap().get(source).getMap().values();
        } else {
            fields = new ArrayList<>();
            for (String alias : parse.args) {
                Optional<IStateField> temp = FCStateManager.instance().getStateMap().get(source).getOrCreateFromAlias(alias);
                if (temp.isPresent()) fields.add(temp.get());
            }
            if (fields.isEmpty()) {
                fields = FCStateManager.instance().getStateMap().get(source).getMap().values();
            }
        }
        IStateField field;
        for (Iterator<IStateField> it = fields.iterator(); it.hasNext(); ) {
            field = it.next();
            if (field != null && !field.isEmpty()) {
                output.append(Text.of(TextColors.GREEN, field.getName() + ":\n"));
                output.append(field.currentState());
                if (it.hasNext()) output.append(Text.of("\n"));
                flag++;
            }
        }
        if (flag == 0) source.sendMessage(Text.of("Your current state buffer is clear!"));
        else source.sendMessage(output.build());
        return CommandResult.empty();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
        if (!testPermission(source)) return ImmutableList.of();
        AdvCmdParser.ParseResult parse = AdvCmdParser.builder().arguments(arguments).excludeCurrent(true).autoCloseQuotes(true).parse();
        System.out.println(parse.current.type);
        if (parse.current.type.equals(AdvCmdParser.CurrentElement.ElementType.ARGUMENT))
            return FCStateManager.instance().getPrimaryAliases().stream()
                    .filter(new StartsWithPredicate(parse.current.token))
                    .filter(alias -> !isIn(parse.args, alias))
                    .collect(GuavaCollectors.toImmutableList());
        else if (parse.current.type.equals(AdvCmdParser.CurrentElement.ElementType.COMPLETE))
            return ImmutableList.of(parse.current.prefix + " ");
        return ImmutableList.of();
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return source.hasPermission("foxcore.command.state.current");
    }

    @Override
    public Optional<? extends Text> getShortDescription(CommandSource source) {
        return Optional.of(Text.of("Displays the current contents of your state buffer."));
    }

    @Override
    public Optional<? extends Text> getHelp(CommandSource source) {
        return Optional.of(Text.of("If fields are specified, this command will only print out the data of those particular fields."));
    }

    @Override
    public Text getUsage(CommandSource source) {
        return Text.of("current [field]...");
    }
}
