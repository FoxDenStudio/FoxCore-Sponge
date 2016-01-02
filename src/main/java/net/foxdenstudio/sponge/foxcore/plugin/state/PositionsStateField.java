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
import net.foxdenstudio.sponge.foxcore.common.FCHelper;
import net.foxdenstudio.sponge.foxcore.plugin.command.util.AdvCmdParse;
import net.foxdenstudio.sponge.foxcore.plugin.command.util.ProcessResult;
import net.foxdenstudio.sponge.foxcore.plugin.command.util.SourceState;
import net.foxdenstudio.sponge.foxcore.plugin.network.FCPacketManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Iterator;

public class PositionsStateField extends ListStateFieldBase<Vector3i> {

    public static final String ID = "position";

    private SourceState sourceState;

    public PositionsStateField(String name, SourceState sourceState) {
        super(name);
        this.sourceState = sourceState;
    }

    @Override
    public Text state() {
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
    public ProcessResult add(CommandSource source, String arguments) throws CommandException {
        AdvCmdParse.ParseResult parse = AdvCmdParse.builder().arguments(arguments).parse2();
        
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
                x = (int) FCHelper.parseCoordinate(pPos.getX(), parse.args[0]);
            } catch (NumberFormatException e) {
                throw new ArgumentParseException(Text.of("Unable to parse \"" + parse.args[0] + "\"!"), e, parse.args[0], 0);
            }
            try {
                y = (int) FCHelper.parseCoordinate(pPos.getY(), parse.args[1]);
            } catch (NumberFormatException e) {
                throw new ArgumentParseException(Text.of("Unable to parse \"" + parse.args[1] + "\"!"), e, parse.args[1], 1);
            }
            try {
                z = (int) FCHelper.parseCoordinate(pPos.getZ(), parse.args[2]);
            } catch (NumberFormatException e) {
                throw new ArgumentParseException(Text.of("Unable to parse \"" + parse.args[2] + "\"!"), e, parse.args[2], 2);
            }
        } else {
            throw new CommandException(Text.of("Too many arguments!"));
        }
        this.list.add(new Vector3i(x, y, z));
        if (source instanceof Player) {
            FCPacketManager.instance().sendPos((Player) source, FCHelper.getPositions(source));
        }
        return ProcessResult.of(true, Text.of("Successfully added position (" + x + ", " + y + ", " + z + ") to your state buffer!"));
    }

    @Override
    public ProcessResult subtract(CommandSource source, String arguments) throws CommandException {
        AdvCmdParse.ParseResult parse = AdvCmdParse.builder().arguments(arguments).parse2();
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
            FCPacketManager.instance().sendPos((Player) source, FCHelper.getPositions(source));
        }
        return ProcessResult.of(true, Text.of("Successfully removed position from your state buffer!"));
    }

    @Override
    public void flush() {
        super.flush();
        if (sourceState.getSource() instanceof Player) {
            FCPacketManager.instance().sendPos((Player) sourceState.getSource(), FCHelper.getPositions(sourceState.getSource()));
        }

    }
}