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

package net.foxdenstudio.foxcore.state;

import com.flowpowered.math.vector.Vector3i;
import net.foxdenstudio.foxcore.command.util.AdvCmdParse;
import net.foxdenstudio.foxcore.command.util.ProcessResult;
import net.foxdenstudio.foxcore.util.FCHelper;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;

import java.util.Iterator;

public class PositionsStateField extends ListStateFieldBase<Vector3i> {

    public static final String ID = "position";

    public PositionsStateField(String name) {
        super(name);
    }

    @Override
    public Text state() {
        TextBuilder builder = Texts.builder();
        int index = 1;
        for (Iterator<Vector3i> it = this.list.iterator(); it.hasNext(); ) {
            Vector3i pos = it.next();
            builder.append(Texts.of("  " + (index++) + ": " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()));
            if (it.hasNext()) builder.append(Texts.of("\n"));
        }
        return builder.build();
    }

    @Override
    public ProcessResult add(CommandSource source, String arguments) throws CommandException {
        String[] args = {};
        if (!arguments.isEmpty()) args = arguments.split(" +");
        int x, y, z;
        Vector3i pPos = null;
        if (source instanceof Player)
            pPos = ((Player) source).getLocation().getBlockPosition();
        if (args.length == 0) {
            if (pPos == null)
                throw new CommandException(Texts.of("Must specify coordinates!"));
            x = pPos.getX();
            y = pPos.getY();
            z = pPos.getZ();
        } else if (args.length > 0 && args.length < 3) {
            throw new CommandException(Texts.of("Not enough arguments!"));
        } else if (args.length == 3) {
            if (pPos == null)
                pPos = Vector3i.ZERO;
            try {
                x = FCHelper.parseCoordinate(pPos.getX(), args[0]);
            } catch (NumberFormatException e) {
                throw new ArgumentParseException(Texts.of("Unable to parse \"" + args[0] + "\"!"), e, args[0], 0);
            }
            try {
                y = FCHelper.parseCoordinate(pPos.getY(), args[1]);
            } catch (NumberFormatException e) {
                throw new ArgumentParseException(Texts.of("Unable to parse \"" + args[1] + "\"!"), e, args[1], 1);
            }
            try {
                z = FCHelper.parseCoordinate(pPos.getZ(), args[2]);
            } catch (NumberFormatException e) {
                throw new ArgumentParseException(Texts.of("Unable to parse \"" + args[2] + "\"!"), e, args[2], 2);
            }
        } else {
            throw new CommandException(Texts.of("Too many arguments!"));
        }
        this.list.add(new Vector3i(x, y, z));
        return ProcessResult.of(true, Texts.of("Successfully added position (" + x + ", " + y + ", " + z + ") to your state buffer!"));
    }

    @Override
    public ProcessResult subtract(CommandSource source, String arguments) throws CommandException {
        AdvCmdParse parse = AdvCmdParse.builder().arguments(arguments).build();
        String[] args = parse.getArgs();
        int index = this.list.size();
        if (args.length > 1) {
            try {
                index = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                throw new ArgumentParseException(Texts.of("Not a valid index!"), args[1], 1);
            }
        }
        try {
            this.list.remove(index - 1);
        } catch (IndexOutOfBoundsException e) {
            throw new ArgumentParseException(Texts.of("Index out of bounds! (1 - " + this.list.size()), args[1], 1);
        }
        return ProcessResult.of(true, Texts.of("Successfully removed position from your state buffer!"));
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }
}
