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

package net.foxdenstudio.sponge.foxcore.common.util;

import com.flowpowered.math.vector.Vector3f;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.regex.Pattern;

public final class FCCUtil {

    public static final String[] colorNames = {"black", "darkblue", "darkgreen", "darkaqua", "darkred", "darkpurple", "gold", "gray",
            "darkgray", "blue", "green", "aqua", "red", "lightpurple", "yellow", "white"};

    public static double parseCoordinate(double sPos, String arg) throws NumberFormatException {
        if (arg.equals("~")) {
            return sPos;
        } else if (arg.startsWith("~")) {
            return sPos + Double.parseDouble(arg.substring(1));
        } else {
            return Integer.parseInt(arg);
        }
    }

    public static int parseCoordinate(int sPos, String arg) throws NumberFormatException {
        if (arg.equals("~")) {
            return sPos;
        } else if (arg.startsWith("~")) {
            return sPos + Integer.parseInt(arg.substring(1));
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

    public static FCPattern parseUserRegex(String regex) throws CommandException {
        String[] split = regex.split("(?<!\\\\)/", -1);
        if (split.length > 2) {
            String flagsString = split[split.length - 1];
            regex = regex.substring(1, regex.lastIndexOf("/"));
            int flags = 0;
            if (flagsString.contains("i")) flags |= Pattern.CASE_INSENSITIVE;
            return new FCPattern(Pattern.compile(regex, flags), flagsString.contains("a"));
        } else throw new CommandException(Text.of("Regex missing a second slash!"));
    }

    public static int colorCodeFromName(String name) {
        for (int i = 0; i < colorNames.length; i++) {
            if (colorNames[i].equalsIgnoreCase(name)) return i;
        }
        return -1;
    }

    public static boolean checkPermissionString(String permission) {
        return permission.matches("[a-zA-Z0-9\\-_.]") && !permission.matches("^.*\\.\\..*$") && !permission.startsWith(".") && !permission.endsWith(".");
    }

    public static String toCapitalCase(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public static class FCPattern {
        public Pattern pattern;
        public boolean matchAny;

        public FCPattern(Pattern pattern, boolean matchAny) {
            this.pattern = pattern;
            this.matchAny = matchAny;
        }

        public boolean matches(String string) {
            if (matchAny) return pattern.matcher(string).find();
            else return pattern.matcher(string).matches();
        }
    }

}
