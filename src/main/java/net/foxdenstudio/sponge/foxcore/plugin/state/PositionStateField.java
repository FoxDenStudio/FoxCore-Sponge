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

package net.foxdenstudio.sponge.foxcore.plugin.state;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;
import net.foxdenstudio.sponge.foxcore.common.FCUtil;
import net.foxdenstudio.sponge.foxcore.plugin.command.util.AdvCmdParser;
import net.foxdenstudio.sponge.foxcore.plugin.command.util.ProcessResult;
import net.foxdenstudio.sponge.foxcore.plugin.state.factory.IStateFieldFactory;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.GuavaCollectors;
import org.spongepowered.api.util.StartsWithPredicate;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PositionStateField extends ListStateFieldBase<Vector3i> {

    public static final String ID = "position";

    public PositionStateField(String name, SourceState sourceState) {
        super(name, sourceState);
    }

    @Override
    public Text currentState(CommandSource source) {
        Text.Builder builder = Text.builder();
        int index = 1;
        for (Iterator<Vector3i> it = this.list.iterator(); it.hasNext(); ) {
            Vector3i pos = it.next();
            builder.append(Text.of("  " + (index++) + ": " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()));
            if (it.hasNext()) builder.append(Text.of("\n"));
        }
        return builder.build();
    }

    @Override
    public Text detailedState(CommandSource source, String args) {
        return currentState(source);
    }

    @Override
    public ProcessResult modify(CommandSource source, String arguments) throws CommandException {
        AdvCmdParser.ParseResult parse = AdvCmdParser.builder().arguments(arguments).limit(1).parseLastFlags(false).parse();
        String newArgs = parse.args.length > 1 ? parse.args[1] : "";
        if (parse.args.length == 0 || parse.args[0].equalsIgnoreCase("add")) {
            return add(source, newArgs);
        } else if (parse.args[0].equalsIgnoreCase("remove")) {
            return remove(source, newArgs);
        }
        return ProcessResult.of(false, Text.of("Not a valid operation!"));
    }

    @Override
    public List<String> modifySuggestions(CommandSource source, String arguments) throws CommandException {
        AdvCmdParser.ParseResult parse = AdvCmdParser.builder()
                .arguments(arguments)
                .excludeCurrent(true)
                .autoCloseQuotes(true)
                .parse();
        if (parse.current.type.equals(AdvCmdParser.CurrentElement.ElementType.ARGUMENT)) {
            if (parse.current.index == 0) {
                return ImmutableList.of("add", "remove").stream()
                        .filter(new StartsWithPredicate(parse.current.token))
                        .collect(GuavaCollectors.toImmutableList());
            } else if (parse.current.index < 4 && parse.args[0].equals("add")) {
                if (parse.current.token.isEmpty())
                    return ImmutableList.of(parse.current.prefix + "~");
                else return ImmutableList.of(parse.current.prefix + parse.current.token + " ");
            }
        } else if (parse.current.type.equals(AdvCmdParser.CurrentElement.ElementType.COMPLETE))
            return ImmutableList.of(parse.current.prefix + " ");
        return ImmutableList.of();
    }


    @Override
    public Optional<Text> getScoreboardTitle() {
        return Optional.of(Text.of(TextColors.GREEN, this.name,
                TextColors.YELLOW, " (", this.list.size(), ")"));
    }

    @Override
    public List<Text> getScoreboardText() {
        int[] index = {1};
        return this.list.stream()
                .map(vector3i -> Text.of("  " + index[0]++ + ": " + vector3i.toString()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean showScoreboard() {
        return !list.isEmpty();
    }

    @Override
    public boolean prioritizeLast() {
        return false;
    }

    public ProcessResult add(CommandSource source, String arguments) throws CommandException {
        AdvCmdParser.ParseResult parse = AdvCmdParser.builder().arguments(arguments).parse();

        int x, y, z;
        Vector3i pPos = null;
        if (source instanceof Player)
            pPos = ((Player) source).getLocation().getBlockPosition();
        if (parse.args.length == 0) {
            if (pPos == null)
                throw new CommandException(Text.of("Must specify coordinates!"));
            x = pPos.getX();
            y = pPos.getY();
            z = pPos.getZ();
        } else if (parse.args.length > 0 && parse.args.length < 3) {
            throw new CommandException(Text.of("Not enough arguments!"));
        } else if (parse.args.length == 3) {
            if (pPos == null)
                pPos = Vector3i.ZERO;
            try {
                x = (int) FCUtil.parseCoordinate(pPos.getX(), parse.args[0]);
            } catch (NumberFormatException e) {
                throw new ArgumentParseException(Text.of("Unable to parse \"" + parse.args[0] + "\"!"), e, parse.args[0], 0);
            }
            try {
                y = (int) FCUtil.parseCoordinate(pPos.getY(), parse.args[1]);
            } catch (NumberFormatException e) {
                throw new ArgumentParseException(Text.of("Unable to parse \"" + parse.args[1] + "\"!"), e, parse.args[1], 1);
            }
            try {
                z = (int) FCUtil.parseCoordinate(pPos.getZ(), parse.args[2]);
            } catch (NumberFormatException e) {
                throw new ArgumentParseException(Text.of("Unable to parse \"" + parse.args[2] + "\"!"), e, parse.args[2], 2);
            }
        } else {
            throw new CommandException(Text.of("Too many arguments!"));
        }
        this.list.add(new Vector3i(x, y, z));
        if (source instanceof Player) {
            FCUtil.updatePositions((Player) source);
        }
        sourceState.updateScoreboard();
        return ProcessResult.of(true, Text.of("Successfully added position (" + x + ", " + y + ", " + z + ") to your state buffer!"));
    }

    public List<String> addSuggestions(CommandSource source, String arguments) throws CommandException {
        AdvCmdParser.ParseResult parse = AdvCmdParser.builder()
                .arguments(arguments)
                .excludeCurrent(true)
                .autoCloseQuotes(true)
                .parse();
        if (parse.current.type.equals(AdvCmdParser.CurrentElement.ElementType.ARGUMENT) && parse.current.index < 3 && parse.current.token.isEmpty()) {
            return ImmutableList.of(parse.current.prefix + "~");
        } else if (parse.current.type.equals(AdvCmdParser.CurrentElement.ElementType.COMPLETE))
            return ImmutableList.of(parse.current.prefix + " ");
        return ImmutableList.of();
    }

    public ProcessResult remove(CommandSource source, String arguments) throws CommandException {
        if (this.list.size() == 0) throw new CommandException(Text.of("No elements to remove!"));
        AdvCmdParser.ParseResult parse = AdvCmdParser.builder().arguments(arguments).parse();
        int index = this.list.size();
        if (parse.args.length > 0) {
            try {
                index = Integer.parseInt(parse.args[0]);
            } catch (NumberFormatException e) {
                throw new ArgumentParseException(Text.of("Not a valid index!"), parse.args[0], 0);
            }
        }
        try {
            this.list.remove(index - 1);
        } catch (IndexOutOfBoundsException e) {
            throw new ArgumentParseException(Text.of("Index out of bounds! (1 - " + this.list.size()), parse.args[0], 0);
        }
        if (source instanceof Player) {
            FCUtil.updatePositions((Player) source);
        }
        return ProcessResult.of(true, Text.of("Successfully removed position from your state buffer!"));
    }

    @Override
    public void flush() {
        super.flush();
        if (sourceState.getSource() instanceof Player) {
            FCUtil.updatePositions((Player) sourceState.getSource());
        }
    }

    public static class Factory implements IStateFieldFactory {

        @Override
        public IStateField createStateField(SourceState sourceState) {
            return new PositionStateField("Positions", sourceState);
        }
    }
}