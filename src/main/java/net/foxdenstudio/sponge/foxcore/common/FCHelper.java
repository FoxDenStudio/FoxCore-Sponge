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

package net.foxdenstudio.sponge.foxcore.common;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import net.foxdenstudio.sponge.foxcore.plugin.state.FCStateManager;
import net.foxdenstudio.sponge.foxcore.plugin.state.PositionsStateField;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tristate;

import java.util.Arrays;
import java.util.List;

public final class FCHelper {
    public static double parseCoordinate(double sPos, String arg) throws NumberFormatException {
        if (arg.equals("~")) {
            return sPos;
        } else if (arg.startsWith("~")) {
            return sPos + Double.parseDouble(arg.substring(1));
        } else {
            return Integer.parseInt(arg);
        }
    }

    public static <T> boolean contains(T[] array, T value) {
        for (T element : array) {
            if (element.equals(value)) return true;
        }
        return false;
    }

    @SafeVarargs
    public static <T> T[] concatAll(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    public static String readableTristate(Tristate state) {
        switch (state) {
            case UNDEFINED:
                return "Passthrough";
            case TRUE:
                return "True";
            case FALSE:
                return "False";
            default:
                return "Wait wat?";
        }
    }

    public static Text readableTristateText(Tristate state) {
        switch (state) {
            case UNDEFINED:
                return Texts.of(TextColors.YELLOW, "Passthrough");
            case TRUE:
                return Texts.of(TextColors.GREEN, "True");
            case FALSE:
                return Texts.of(TextColors.RED, "False");
            default:
                return Texts.of(TextColors.LIGHT_PURPLE, "Wait wat?");
        }
    }

    public static boolean isUserOnList(List<User> list, User user) {
        //System.out.println(user.getUniqueId());
        for (User u : list) {
            //System.out.println(u.getUniqueId());
            if (u.getUniqueId().equals(user.getUniqueId())) return true;
        }
        return false;
    }

    public static boolean hasColor(Text text) {
        if (text.getColor() != TextColors.NONE && text.getColor() != TextColors.RESET) return true;
        for (Text child : text.getChildren()) {
            if (hasColor(child)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static List<Vector3i> getPositions(CommandSource source) {
        return ((PositionsStateField) FCStateManager.instance().getStateMap().get(source).get(PositionsStateField.ID)).getList();
    }

    public static Vector3f RGBfromHSV(double h, double s, double v) {
        int i;
        double dr, dg, db;
        double f, p, q, t;
        if (s == 0) {
            return new Vector3f(v, v, v);
        }
        h /= 60;
        i = (int) Math.floor(h);
        f = h - i;
        p = v * (1 - s);
        q = v * (1 - s * f);
        t = v * (1 - s * (1 - f));
        switch (i) {
            case 0:
                dr = v;
                dg = t;
                db = p;
                break;
            case 1:
                dr = q;
                dg = v;
                db = p;
                break;
            case 2:
                dr = p;
                dg = v;
                db = t;
                break;
            case 3:
                dr = p;
                dg = q;
                db = v;
                break;
            case 4:
                dr = t;
                dg = p;
                db = v;
                break;
            default:
                dr = v;
                dg = p;
                db = q;
                break;
        }
        return new Vector3f(dr, dg, db);
    }

}
