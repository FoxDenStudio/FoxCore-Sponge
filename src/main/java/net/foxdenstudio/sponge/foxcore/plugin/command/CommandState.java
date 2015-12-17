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
import net.foxdenstudio.sponge.foxcore.plugin.command.util.AdvCmdParse;
import net.foxdenstudio.sponge.foxcore.plugin.state.FCStateManager;
import net.foxdenstudio.sponge.foxcore.plugin.state.IStateField;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import java.util.*;

public class CommandState implements CommandCallable {

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        if (!testPermission(source)) {
            source.sendMessage(Texts.of(TextColors.RED, "You don't have permission to use this command!"));
            return CommandResult.empty();
        }
        AdvCmdParse parse = AdvCmdParse.builder().arguments(arguments).limit(1).build();
        String[] args = parse.getArgs();
        TextBuilder output = Texts.builder().append(Texts.of(TextColors.GOLD, "-----------------------------------------------------\n"));
        int flag = 0;
        Collection<IStateField> fields;
        if (args.length == 0) {
            fields = FCStateManager.instance().getStateMap().get(source).getMap().values();
        } else {
            fields = new ArrayList<>();
            for (String alias : args) {
                IStateField temp = FCStateManager.instance().getStateMap().get(source).getFromAlias(alias);
                if (temp != null) fields.add(temp);
            }
            if (fields.isEmpty()) {
                fields = FCStateManager.instance().getStateMap().get(source).getMap().values();
            }
        }
        IStateField field = null;
        for (Iterator<IStateField> it = fields.iterator(); it.hasNext(); ) {
            field = it.next();
            if (field != null && !field.isEmpty()) {
                output.append(Texts.of(TextColors.GREEN, field.getName() + ":\n"));
                output.append(field.state());
                if (it.hasNext()) output.append(Texts.of("\n"));
                flag++;
            }
        }
        if (flag == 0) source.sendMessage(Texts.of("Your current state buffer is clear!"));
        else source.sendMessage(output.build());
        return CommandResult.empty();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
        return ImmutableList.of();
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return source.hasPermission("foxcore.command.state.state") || source.hasPermission("foxguard.command.state.state");
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
        return Texts.of("state [fields]...");
    }
}
