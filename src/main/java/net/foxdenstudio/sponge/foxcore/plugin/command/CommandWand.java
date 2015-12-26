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
import net.foxdenstudio.sponge.foxcore.plugin.FoxCoreMain;
import net.foxdenstudio.sponge.foxcore.plugin.command.util.AdvCmdParse;
import net.foxdenstudio.sponge.foxcore.plugin.wand.data.WandData;
import net.foxdenstudio.sponge.foxcore.plugin.wand.data.WandType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.data.manipulator.mutable.item.LoreData;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.foxdenstudio.sponge.foxcore.plugin.util.Aliases.PLAYER_ALIASES;
import static net.foxdenstudio.sponge.foxcore.plugin.util.Aliases.isAlias;

public class CommandWand implements CommandCallable {

    private static final Function<Map<String, String>, Function<String, Consumer<String>>> MAPPER = map -> key -> value -> {
        if (isAlias(PLAYER_ALIASES, key) && !map.containsKey("player")) {
            map.put("player", value);
        }
    };

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        if (!testPermission(source)) {
            source.sendMessage(Texts.of(TextColors.RED, "You don't have permission to use this command!"));
            return CommandResult.empty();
        }
        AdvCmdParse parse = AdvCmdParse.builder().arguments(arguments).flagMapper(MAPPER).build();
        String[] args = parse.getArgs();

        Player player = null;
        if (source instanceof Player) player = (Player) source;
        if (parse.getFlagmap().containsKey("player"))
            player = Sponge.getGame().getServer().getPlayer(parse.getFlagmap().get("player")).orElse(null);
        if (player == null) throw new CommandException(Texts.of("You must specify a player to give the wand to!"));
        WandData wandData = Sponge.getDataManager().getManipulatorBuilder(WandData.class).get().create();
        if (args.length > 0) {
            WandType type = WandType.from(args[0]);
            if(type == null) throw new CommandException(Texts.of("Not a valid wand type!"));
            wandData.setWandType(type);
        }
        LoreData loreData = Sponge.getDataManager().getManipulatorBuilder(LoreData.class).get().create();
        final ListValue<Text> lore = loreData.lore();
        lore.add(Texts.of("Position Wand"));
        loreData.set(lore);
        ItemStack stack = ItemStack.of(ItemTypes.GOLDEN_AXE, 1);
        stack.offer(wandData);
        stack.offer(loreData);
        stack.offer(Sponge.getDataManager().getManipulatorBuilder(EnchantmentData.class).get().create());

        if (player.getItemInHand().isPresent()) {
            Entity entity = player.getWorld().createEntity(EntityTypes.ITEM, player.getLocation().getPosition()).get();
            entity.offer(Keys.REPRESENTED_ITEM, stack.createSnapshot());
            player.getWorld().spawnEntity(entity, Cause.of(FoxCoreMain.instance()));
        } else {
            player.setItemInHand(stack);
        }

        source.sendMessage(Texts.of("Successfully created wand!"));

        return CommandResult.empty();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
        return ImmutableList.of();
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return source.hasPermission("foxcore.command.wand.create");
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
        return Texts.of("wand <type>");
    }

}
