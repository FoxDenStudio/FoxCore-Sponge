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

package net.foxdenstudio.sponge.foxcore.plugin.command.util;

import net.foxdenstudio.sponge.foxcore.plugin.util.CallbackHashMap;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Fox on 12/1/2015.
 * Project: SpongeForge
 */
public class AdvCmdParse {

    public static final Function<Map<String, String>, Function<String, Consumer<String>>>
            DEFAULT_MAPPER = map -> key -> value -> map.put(key, value);

    private static final String regex = "(?:--[\\w-]*[:=])?([\"'])(?:\\\\.|[^\\\\])*?\\1|(?:\\\\.|[^\"'\\s])+";

    public static AdvCmdParse builder() {
        return new AdvCmdParse();
    }

    // Holds the raw input to be parsed
    private String arguments = "";
    // Designates how many separate arguments to parse. Flags do not count toward this number
    // Once the limit has been reached, the rest of the input is bundled as one argument
    private int limit = 0;
    // Determines whether flags should be stripped out of the ending block, if it exists. Only effective if limit != 0
    private boolean extractSubFlags = false;
    // Destructive function to handle flag input. A map, key, and value are provided.
    // This function determines how the key-value pair is inserted into the map.
    private Function<Map<String, String>, Function<String, Consumer<String>>> flagMapper = DEFAULT_MAPPER;
    // Determines whether to unescape ending block.
    // Set to true if the block needs to be fed through a parser that doesn't understand escaped characters.
    private boolean unescapeLast = false;
    // Determines whether unclosed quotes should be automatically closed. Usually for suggestion parsing.
    private boolean autoCloseQuotes = false;
    // Determines whether flags in between the final block and the preceding arguments should be parsed as part of the command or not.
    // If set to true, the flags are not included in the final block, and vice versa.
    private boolean parseLastFlags = true;
    // Determines whether or not to parse the current element. This is used to exclude incomplete elements.
    private boolean excludeCurrent = false;
    // Determines whether to do any parsing after the limit is reached.
    // If set to true, all parsing is stopped as soon as the final block is reached. Comments are disabled as well.
    private boolean leaveFinalAsIs = false;

    private AdvCmdParse() {
    }

    public AdvCmdParse arguments(String arguments) {
        this.arguments = arguments;
        return this;
    }

    public AdvCmdParse limit(int limit) {
        this.limit = limit;
        return this;
    }

    public AdvCmdParse extractSubFlags(boolean subFlags) {
        this.extractSubFlags = subFlags;
        return this;
    }

    public AdvCmdParse unescapeLast(boolean unescapeLast) {
        this.unescapeLast = unescapeLast;
        return this;
    }

    public AdvCmdParse autoCloseQuotes(boolean autoCloseQuotes) {
        this.autoCloseQuotes = autoCloseQuotes;
        return this;
    }

    public AdvCmdParse parseLastFlags(boolean parseLastFlags) {
        this.parseLastFlags = parseLastFlags;
        return this;
    }

    public AdvCmdParse flagMapper(Function<Map<String, String>, Function<String, Consumer<String>>> flagMapper) {
        this.flagMapper = flagMapper;
        return this;
    }

    public AdvCmdParse excludeCurrent(boolean excludeCurrent) {
        this.excludeCurrent = excludeCurrent;
        return this;
    }

    public AdvCmdParse leaveFinalAsIs(boolean leaveFinalAsIs) {
        this.leaveFinalAsIs = leaveFinalAsIs;
        return this;
    }

    @Deprecated
    public ParseResult parse() throws CommandException {
        ParseResult parseResult = new ParseResult();
        boolean inQuote = false;

        // Regex Pattern for identifying arguments and flags. It respects quotation marks and escape characters.
        Pattern pattern = Pattern.compile(regex);
        // Check for unclosed quotes
        {
            String toStrip = arguments;
            while (true) {
                Matcher tempMatcher = pattern.matcher(toStrip);
                if (!tempMatcher.find()) break;
                toStrip = toStrip.substring(0, tempMatcher.start()) + toStrip.substring(tempMatcher.end());
            }
            Pattern pattern2 = Pattern.compile("[\"']");
            Matcher matcher = pattern2.matcher(toStrip);
            if (matcher.find()) {
                if (autoCloseQuotes) {
                    if (matcher.group().equals("\"")) arguments += "\"";
                    else if (matcher.group().equals("'")) arguments += "'";
                    inQuote = true;
                } else {
                    throw new CommandException(Text.of("You must close all quotes!"));
                }
            }
        }

        // List of string arguments that were not parsed as flags
        List<String> argsList = new ArrayList<>();
        // Matcher for identifying arguments and flags.
        Matcher matcher = pattern.matcher(arguments);
        // Iterate through matches
        while (matcher.find()) {
            String result = matcher.group();
            if (excludeCurrent && matcher.end() == arguments.length() && (inQuote || !arguments.substring(arguments.length() - 1).matches("[\"']")))
                break;
            // Makes "---" mark the end of the command. Effectively allows command comments
            // It also means that flag names cannot start with hyphens
            if (!result.startsWith("---")) {
                // Throws out any results that are empty
                if (result.equals("--") || result.equals("-")) continue;
                // Parses result as long flag.
                // Format is --<flagname>:<value> Where value can be a quoted string. "=" is also a valid separator
                // If a limit is specified, the flags will be cut out of the final string
                // Setting extractSubFlags to true forces flags within the final string to be left as-is
                // This is useful if the final string is it's own command and needs to be re-parsed
                if (result.startsWith("--") && (extractSubFlags || limit == 0 || argsList.size() < limit || (parseLastFlags && argsList.size() <= limit))) {
                    // Trims the prefix
                    result = result.substring(2);
                    // Splits once by ":" or "="
                    String[] parts = result.split("[:=]", 2);
                    // Throw an exception if the key contains a quote character, as that shouldn't be allowed
                    if (parts[0].contains("\"") || parts[0].contains("'"))
                        throw new CommandException(Text.of("You may not have quotes in long flag keys!"));
                    // Default value in case a value isn't specified
                    String value = "";
                    // Retrieves value if it exists
                    if (parts.length > 1) value = unescapeString(parts[1]);
                    // Applies the flagMapper function.
                    // This is a destructive function that takes 3 parameters and returns nothing. (Destructive consumer)
                    flagMapper.apply(parseResult.flagmap)
                            .apply(parts[0])
                            .accept(value);

                    // Parses result as a short flag. Limit behavior is the same as long flags
                    // multiple letters are treated as multiple flags. Repeating letters add a second flag with a repetition
                    // Example: "-aab" becomes flags "a", "aa", and "b"
                } else if (result.startsWith("-") && result.substring(1, 2).matches("[\\D]")
                        && (extractSubFlags || limit == 0 || argsList.size() < limit || (parseLastFlags && argsList.size() <= limit))) {
                    // Trims prefix
                    result = result.substring(1);
                    // Iterates through each letter
                    for (String str : result.split("")) {
                        // Checks to make sure that the flag letter is alphabetic. Throw exception if it doesn't
                        if (str.matches("[a-zA-Z]")) {
                            // Checks if the flag already exists, and repeat the letter until it doesn't
                            String temp = str;
                            while (parseResult.flagmap.containsKey(temp)) {
                                temp += str;
                            }
                            // Applies destructive flagMapper function.
                            flagMapper.apply(parseResult.flagmap).apply(temp).accept("");
                        } else if (str.matches("[:=-]")) {
                            throw new CommandException(Text.of("You may only have alphabetic short flags! Did you mean to use a long flag (two dashes)?"));
                        } else {
                            throw new CommandException(Text.of("You may only have alphabetic short flags!"));
                        }
                    }

                    // Simply adds the result to the argument list. Quotes are trimmed.
                    // Fallback if the result isn't a flag.
                } else {
                    if (limit != 0 && argsList.size() >= limit && !unescapeLast) argsList.add(result);
                    else argsList.add(unescapeString(result));
                }
            } else break;
        }

        // This part converts the argument list to the final argument array.
        // A number of arguments are copied to a new list less than or equal to the limit.
        // The rest of the arguments, if any, are concatenated together.
        List<String> finalList = new ArrayList<>();
        String finalString = "";
        for (
                int i = 0;
                i < argsList.size(); i++)

        {
            if (limit == 0 || i < limit) {
                finalList.add(argsList.get(i));
            } else {
                finalString += argsList.get(i);
                if (i + 1 < argsList.size()) {
                    finalString += " ";
                }
            }
        }

        // Converts final argument list to an array.
        parseResult.args = finalList.toArray(new String[finalList.size()]);

        return parseResult;
    }

    public ParseResult parse2() throws CommandException {
        ParseResult parseResult = new ParseResult();
        boolean inQuote = false;

        // Regex Pattern for identifying arguments and flags. It respects quotation marks and escape characters.
        Pattern pattern = Pattern.compile(regex);
        // Check for unclosed quotes
        {
            String toStrip = arguments;
            while (true) {
                Matcher tempMatcher = pattern.matcher(toStrip);
                if (!tempMatcher.find()) break;
                toStrip = toStrip.substring(0, tempMatcher.start()) + toStrip.substring(tempMatcher.end());
            }
            Pattern pattern2 = Pattern.compile("[\"']");
            Matcher matcher = pattern2.matcher(toStrip);
            if (matcher.find()) {
                if (autoCloseQuotes) {
                    if (matcher.group().equals("\"")) arguments += "\"";
                    else if (matcher.group().equals("'")) arguments += "'";
                    inQuote = true;
                } else {
                    throw new CommandException(Text.of("You must close all quotes!"));
                }
            }
        }

        // List of string arguments that were not parsed as flags
        List<String> argsList = new ArrayList<>();
        // Matcher for identifying arguments and flags.
        Matcher matcher = pattern.matcher(arguments);
        boolean lastIsCurrent = !(arguments.length() == 0) && (inQuote || !arguments.substring(arguments.length() - 1).matches("[\"' ]"));

        // Iterate through matches
        while (matcher.find()) {
            String result = matcher.group();
            boolean include = true;
            if (excludeCurrent && lastIsCurrent && matcher.end() == arguments.length())
                include = false;
            // Makes "---" mark the end of the command. Effectively allows command comments
            // It also means that flag names cannot start with hyphens
            if (!result.startsWith("---")) {
                // Throws out any results that are empty
                if (result.equals("--")) {
                    parseResult.currentElement = new CurrentElement(CurrentElement.ElementType.LONGFLAGKEY, "", 0, "");
                    continue;
                } else if (result.equals("-")) {
                    parseResult.currentElement = new CurrentElement(CurrentElement.ElementType.SHORTFLAG, "", 0, "");
                    continue;
                }
                // Parses result as long flag.
                // Format is --<flagname>:<value> Where value can be a quoted string. "=" is also a valid separator
                // If a limit is specified, the flags will be cut out of the final string
                // Setting extractSubFlags to true forces flags within the final string to be left as-is
                // This is useful if the final string is it's own command and needs to be re-parsed
                if (result.startsWith("--") && (extractSubFlags || limit == 0 || argsList.size() < limit || (parseLastFlags && argsList.size() <= limit))) {
                    // Trims the prefix
                    result = result.substring(2);
                    // Splits once by ":" or "="
                    String[] parts = result.split("[:=]", 2);
                    // Throw an exception if the key contains a quote character, as that shouldn't be allowed
                    if (parts[0].matches("^.*[^\\w-].*$"))
                        throw new CommandException(Text.of("Long flag keys must be alphanumeric (Includes underscores and hyphens)!"));
                    // Default value in case a value isn't specified
                    String value = "";
                    // Retrieves value if it exists
                    if (parts.length > 1) {
                        value = unescapeString(parts[1]);
                        parseResult.currentElement = new CurrentElement(CurrentElement.ElementType.LONGFLAGVALUE, value, 0, parts[0]);
                    } else {
                        parseResult.currentElement = new CurrentElement(CurrentElement.ElementType.LONGFLAGKEY, parts[0], 0, "");
                    }
                    if (include) {
                        // Applies the flagMapper function.
                        // This is a destructive function that takes 3 parameters and returns nothing. (Destructive consumer)
                        flagMapper.apply(parseResult.flagmap)
                                .apply(parts[0])
                                .accept(value);
                    }
                    // Parses result as a short flag. Limit behavior is the same as long flags
                    // multiple letters are treated as multiple flags. Repeating letters add a second flag with a repetition
                    // Example: "-aab" becomes flags "a", "aa", and "b"
                } else if (result.startsWith("-") && result.substring(1).matches("^.*[^\\d\\.].*$")
                        && (extractSubFlags || limit == 0 || argsList.size() < limit || (parseLastFlags && argsList.size() <= limit))) {
                    // Trims prefix
                    result = result.substring(1);
                    // Iterates through each letter
                    for (String str : result.split("")) {
                        // Checks to make sure that the flag letter is alphabetic. Throw exception if it doesn't
                        if (str.matches("[a-zA-Z]")) {
                            // Checks if the flag already exists, and repeat the letter until it doesn't
                            String temp = str;
                            while (parseResult.flagmap.containsKey(temp)) {
                                temp += str;
                            }
                            // Applies destructive flagMapper function.
                            flagMapper.apply(parseResult.flagmap).apply(temp).accept("");
                        } else if (str.matches("[:=-]")) {
                            throw new CommandException(Text.of("You may only have alphabetic short flags! Did you mean to use a long flag (two dashes)?"));
                        } else {
                            throw new CommandException(Text.of("You may only have alphabetic short flags!"));
                        }
                    }
                    parseResult.currentElement = new CurrentElement(CurrentElement.ElementType.SHORTFLAG, "", 0, "");

                    // Simply adds the result to the argument list. Quotes are trimmed.
                    // Fallback if the result isn't a flag.
                } else {
                    if (leaveFinalAsIs && limit != 0 && argsList.size() >= limit) {
                        String finalBlock = arguments.substring(matcher.start(), arguments.length() - (inQuote ? 1 : 0));
                        parseResult.currentElement = new CurrentElement(CurrentElement.ElementType.FINAL, finalBlock, argsList.size(), "");
                        argsList.add(finalBlock);
                        break;
                    }

                    if (limit != 0 && argsList.size() >= limit && !unescapeLast) {
                        parseResult.currentElement = new CurrentElement(CurrentElement.ElementType.ARGUMENT, result, argsList.size(), "");
                        argsList.add(result);
                    } else {
                        String arg = unescapeString(result);
                        parseResult.currentElement = new CurrentElement(CurrentElement.ElementType.ARGUMENT, arg, argsList.size(), "");
                        argsList.add(arg);
                    }
                }
            } else {
                parseResult.currentElement = new CurrentElement(CurrentElement.ElementType.COMMENT, arguments.substring(matcher.start() + 3), 0, "");
                break;
            }
        }

        if (!lastIsCurrent && (parseResult.currentElement == null || parseResult.currentElement.type != CurrentElement.ElementType.COMMENT)) {
            parseResult.currentElement = new CurrentElement(CurrentElement.ElementType.ARGUMENT, "", argsList.size(), "");
        }

        // This part converts the argument list to the final argument array.
        // A number of arguments are copied to a new list less than or equal to the limit.
        // The rest of the arguments, if any, are concatenated together.
        List<String> finalList = new ArrayList<>();
        String finalString = "";
        for (int i = 0; i < argsList.size(); i++) {
            if (limit == 0 || i < limit) {
                finalList.add(argsList.get(i));
            } else {
                finalString += argsList.get(i);
                if (i + 1 < argsList.size()) {
                    finalString += " ";
                }
            }
        }
        if (!finalString.isEmpty()) {
            finalList.add(finalString);
        }

        if (parseResult.currentElement != null && parseResult.currentElement.type == CurrentElement.ElementType.ARGUMENT && parseResult.currentElement.index >= limit)
            parseResult.currentElement = new CurrentElement(CurrentElement.ElementType.FINAL, finalString + (lastIsCurrent ? "" : " "), finalList.size() - 1, "");
        // Converts final argument list to an array.
        parseResult.args = finalList.toArray(new String[finalList.size()]);

        return parseResult;
    }

    private String unescapeString(String str) {
        if (str.startsWith("\"") || str.startsWith("'")) str = str.substring(1, str.length() - 1);
        String newStr = "";
        for (int i = 0; i < str.length(); i++) {
            String letter = str.substring(i, i + 1);
            if (letter.equals("\\")) {
                if (i + 2 <= str.length()) {
                    String escape = str.substring(i + 1, i + 2);
                    switch (escape) {
                        case "n":
                            escape = "\n";
                    }
                    newStr += escape;
                    i++;
                }
            } else {
                newStr += letter;
            }
        }
        return newStr;
    }

    public static class ParseResult {
        public String[] args = {};
        public Map<String, String> flagmap = new CallbackHashMap<>((key, map) -> "");
        public CurrentElement currentElement = null;
    }

    public static class CurrentElement {
        public final ElementType type;
        public final String token;
        public final int index;
        public final String key;

        public CurrentElement(ElementType type, String token, int index, String key) {
            this.type = type;
            this.token = token;
            this.index = index;
            this.key = key;
        }

        public enum ElementType {
            ARGUMENT,
            SHORTFLAG,
            LONGFLAGKEY,
            LONGFLAGVALUE,
            FINAL,
            COMMENT
        }
    }
}
