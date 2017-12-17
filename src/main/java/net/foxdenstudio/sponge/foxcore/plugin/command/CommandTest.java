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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.foxdenstudio.sponge.foxcore.plugin.command.util.AdvCmdParser;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializer;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static org.spongepowered.api.text.format.TextColors.*;
import static org.spongepowered.api.text.format.TextStyles.BOLD;

public class CommandTest extends FCCommandBase {

    /*public CommandResult process(CommandSource source, String arguments) throws CommandException {
        if (!testPermission(source)) {
            source.sendMessage(Text.of(TextColors.RED, "You don't have permission to use this command!"));
            return CommandResult.empty();
        }
        AdvCmdParser.ParseResult parse = AdvCmdParser.builder()
                .arguments(arguments)
                .limit(3)
                .parse();

        Text.Builder builder = Text.builder();
        builder.append(Text.of(TextColors.GOLD, "-----------------------------\n"));
        builder.append(Text.of(TextColors.GOLD, "Raw: \"", TextColors.RESET, arguments, TextColors.GOLD, "\"\n"));
        builder.append(Text.of(TextColors.GOLD, "Args: \""));

        for (int i = 0; i < parse.args.length; i++) {
            builder.append(Text.of(TextColors.RESET, parse.args[i]));
            if (i < parse.args.length - 1) builder.append(Text.of(TextColors.GOLD, ", "));
        }
        builder.append(Text.of(TextColors.GOLD, "\"\n"));

        builder.append(Text.of(TextColors.GOLD, "Flags: \"", TextColors.RESET, parse.flags, TextColors.GOLD, "\"\n"));

        builder.append(Text.of(TextColors.GOLD, "Type: ", TextColors.RESET, parse.current.type, TextColors.GOLD, "\n"));
        builder.append(Text.of(TextColors.GOLD, "Token: \"", TextColors.RESET, parse.current.token, TextColors.GOLD, "\"\n"));
        builder.append(Text.of(TextColors.GOLD, "Index: ", TextColors.RESET, parse.current.index, TextColors.GOLD, "\n"));
        builder.append(Text.of(TextColors.GOLD, "Key: \"", TextColors.RESET, parse.current.key, TextColors.GOLD, "\"\n"));
        source.sendMessage(builder.build());
        return CommandResult.empty();
    }*/

    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        if (!testPermission(source)) {
            source.sendMessage(Text.of(RED, "You don't have permission to use this command!"));
            return CommandResult.empty();
        }

        /*Text text = Text.of(
                RED, BOLD, "hello world", Text.of(
                        GREEN, "I am a child", Text.of(
                                RESET, "and i am a grandchild"
                        )
                )
        );

        TextSerializer serializer = TextSerializers.JSON;

        String jsonString = serializer.serialize(text);

        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(jsonObject);

        source.sendMessage(Text.of(jsonString));
        //source.sendMessage(Text.of(prettyJson));
        //source.sendMessage(text);

        JsonObject custom = new JsonObject();*/




//        System.out.println(prettyJson);

        return CommandResult.empty();
    }

    /*@Override
    public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
        if (!testPermission(source)) return ImmutableList.of();
        AdvCmdParser.ParseResult parse = AdvCmdParser.builder()
                .arguments(arguments)
                .excludeCurrent(true)
                .autoCloseQuotes(true)
                .parse();
        Text.Builder builder = Text.builder();
        builder.append(Text.of(TextColors.GOLD, "\n-----------------------------\n"));
        builder.append(Text.of(TextColors.GOLD, "Args: \"", TextColors.RESET, Arrays.toString(parse.args), TextColors.GOLD, "\"\n"));
        builder.append(Text.of(TextColors.GOLD, "Flags: \"", TextColors.RESET, parse.flags, TextColors.GOLD, "\"\n"));
        builder.append(Text.of(TextColors.GOLD, "Type: ", TextColors.RESET, parse.current.type, TextColors.GOLD, "\n"));
        builder.append(Text.of(TextColors.GOLD, "Token: \"", TextColors.RESET, parse.current.token, TextColors.GOLD, "\"\n"));
        builder.append(Text.of(TextColors.GOLD, "Index: ", TextColors.RESET, parse.current.index, TextColors.GOLD, "\n"));
        builder.append(Text.of(TextColors.GOLD, "Key: \"", TextColors.RESET, parse.current.key, TextColors.GOLD, "\"\n"));
        builder.append(Text.of(TextColors.GOLD, "Prefix: \"", TextColors.RESET, parse.current.prefix, TextColors.GOLD, "\"\n"));
        source.sendMessage(builder.build());
        return ImmutableList.of();
    }*/

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException {
        return ImmutableList.of();
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return source.hasPermission("foxcore.command.debug.test");
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.empty();
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return Optional.empty();
    }

    @Override
    public Text getUsage(CommandSource source) {
        return Text.of("test [mystery args]...");
    }


    /*@Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        if (!testPermission(source)) {
            source.sendMessage(Text.of(TextColors.RED, "You don't have permission to use this command!"));
            return CommandResult.empty();
        }
        if (source instanceof Player) {
            Player player = ((Player) source);
            FoxCoreMain.instance().getFoxcoreNetworkChannel().sendDebug(player);
            source.sendMessage(Text.of("Sent debug packet!"));
        } else {
            source.sendMessage(Text.of("You must be a player to use this command!"));
        }
        return CommandResult.empty();
    }*/
}
