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
import net.foxdenstudio.sponge.foxcore.plugin.FCConfigManager;
import net.foxdenstudio.sponge.foxcore.plugin.FoxCoreMain;
import net.foxdenstudio.sponge.foxcore.plugin.command.util.AdvCmdParser;
import net.foxdenstudio.sponge.foxcore.plugin.command.util.FlagMapper;
import net.foxdenstudio.sponge.foxcore.plugin.wand.FCWandRegistry;
import net.foxdenstudio.sponge.foxcore.plugin.wand.IWand;
import net.foxdenstudio.sponge.foxcore.plugin.wand.IWandFactory;
import net.foxdenstudio.sponge.foxcore.plugin.wand.data.WandData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.data.manipulator.mutable.item.LoreData;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.GuavaCollectors;
import org.spongepowered.api.util.StartsWithPredicate;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static net.foxdenstudio.sponge.foxcore.plugin.util.Aliases.PLAYER_ALIASES;
import static net.foxdenstudio.sponge.foxcore.plugin.util.Aliases.isIn;

public class CommandWand extends FCCommandBase {

    private static final String[] HAND_ALIASES = {"hand", "h"};
    private static final String[] ITEM_ALIASES = {"item", "itemstack", "i", "it"};
    private static final String[] CREATE_ALIASES = {"c", "cr", "create"};
    private static final String[] MODIFY_ALIASES = {"m", "md", "mod", "modify"};
    private static final String[] DUPLICATE_ALIASES = {"d", "dup", "dupe", "duplicate"};

    private static final FlagMapper MAPPER = map -> key -> value -> {
        if (isIn(PLAYER_ALIASES, key) && !map.containsKey("player")) {
            map.put("player", value);
            return true;
        } else if (isIn(HAND_ALIASES, key) && !map.containsKey("hand")) {
            map.put("hand", value);
            return true;
        } else if (isIn(ITEM_ALIASES, key) && !map.containsKey("item")) {
            map.put("item", value);
            return true;
        }
        return false;
    };

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        if (!testPermission(source)) {
            source.sendMessage(Text.of(TextColors.RED, "You don't have permission to use this command!"));
            return CommandResult.empty();
        }
        AdvCmdParser.ParseResult parse = AdvCmdParser.builder()
                .arguments(arguments)
                .flagMapper(MAPPER)
                .limit(1)
                .parseLastFlags(true)
                .parse();


        Player player = null;
        if (source instanceof Player) player = (Player) source;
        if (parse.flags.containsKey("player"))
            player = Sponge.getGame().getServer().getPlayer(parse.flags.get("player")).orElse(null);
        if (player == null) throw new CommandException(Text.of("You must specify a player to give the wand to!"));

        if (parse.args.length < 1) throw new CommandException(Text.of("Must specify a wand type"));
        IWandFactory factory = FCWandRegistry.getInstance().getBuilderFromAlias(parse.args[0]);
        if (factory == null) throw new CommandException(Text.of("\"" + parse.args[0] + "\" is not a valid wand type!"));
        IWand wand = factory.create(parse.args.length > 1 ? parse.args[1] : "");
        if (wand == null) throw new CommandException(Text.of("Unable to create wand!"));

        WandData wandData = Sponge.getDataManager().getManipulatorBuilder(WandData.class).get().create();
        wandData.setWand(wand);

        List<Text> wandLore = wand.getLore();
        LoreData loreData = Sponge.getDataManager().getManipulatorBuilder(LoreData.class).get().create();
        final ListValue<Text> lore = loreData.lore();
        if (wandLore != null) lore.addAll(wand.getLore());
        loreData.set(lore);

        boolean mainHand = false, offHand = false;
        Optional<ItemStack> mainHandItemOptional = player.getItemInHand(HandTypes.MAIN_HAND),
                offHandItemOptional = player.getItemInHand(HandTypes.OFF_HAND);
        if (parse.flags.containsKey("hand")) {
            String handString = parse.flags.get("hand");
            if (handString.isEmpty()) {
                if (mainHandItemOptional.isPresent()) {
                    mainHand = true;
                } else if (offHandItemOptional.isPresent()) {
                    offHand = true;
                }
            } else if (handString.equalsIgnoreCase("main")) {
                mainHand = true;
            } else if (handString.equalsIgnoreCase("off")) {
                offHand = true;
            } else if (handString.equalsIgnoreCase("both")) {
                mainHand = true;
                offHand = true;
            }
        }

        ItemType itemType = FCConfigManager.getInstance().getDefaultWandItemType();
        if (parse.flags.containsKey("item")) {
            String itemTypeName = parse.flags.get("item");
            Optional<ItemType> itemTypeOptional = Sponge.getRegistry().getType(ItemType.class, itemTypeName);
            if (itemTypeOptional.isPresent()) {
                itemType = itemTypeOptional.get();
            } else {
                throw new CommandException(Text.of("\"" + itemTypeName + "\" is not a valid item type!"));
            }
        }

        if (mainHand) {
            ItemStack stack = mainHandItemOptional.orElse(ItemStack.of(itemType, 1));

            stack.offer(wandData);
            stack.offer(loreData);
            stack.offer(Keys.DISPLAY_NAME, wand.getItemName());
            Optional<EnchantmentData> enchantmentDataOptional = stack.getOrCreate(EnchantmentData.class);
            if (enchantmentDataOptional.isPresent()) stack.offer(enchantmentDataOptional.get());
            else stack.offer(Sponge.getDataManager().getManipulatorBuilder(EnchantmentData.class).get().create());

            player.setItemInHand(HandTypes.MAIN_HAND, stack);
        }

        if (offHand) {
            ItemStack stack = offHandItemOptional.orElse(ItemStack.of(itemType, 1));

            stack.offer(wandData);
            stack.offer(loreData);
            stack.offer(Keys.DISPLAY_NAME, wand.getItemName());
            Optional<EnchantmentData> enchantmentDataOptional = stack.getOrCreate(EnchantmentData.class);
            if (enchantmentDataOptional.isPresent()) stack.offer(enchantmentDataOptional.get());
            else stack.offer(Sponge.getDataManager().getManipulatorBuilder(EnchantmentData.class).get().create());

            player.setItemInHand(HandTypes.OFF_HAND, stack);
        }

        if (!(mainHand || offHand)) {
            ItemStack stack = ItemStack.of(itemType, 1);

            stack.offer(wandData);
            stack.offer(loreData);
            stack.offer(Keys.DISPLAY_NAME, wand.getItemName());
            Optional<EnchantmentData> enchantmentDataOptional = stack.getOrCreate(EnchantmentData.class);
            if (enchantmentDataOptional.isPresent()) stack.offer(enchantmentDataOptional.get());
            else stack.offer(Sponge.getDataManager().getManipulatorBuilder(EnchantmentData.class).get().create());

            if (mainHandItemOptional.isPresent()) {
                Entity entity = player.getWorld().createEntity(EntityTypes.ITEM, player.getLocation().getPosition());
                entity.offer(Keys.REPRESENTED_ITEM, stack.createSnapshot());
                player.getWorld().spawnEntity(entity, Cause.builder()
                        .named(NamedCause.SOURCE, SpawnCause.builder().type(SpawnTypes.CUSTOM).build())
                        .named("plugin", FoxCoreMain.instance())
                        .build());
            } else {
                player.setItemInHand(HandTypes.MAIN_HAND, stack);
            }
        }

        source.sendMessage(Text.of("Successfully created wand" + (mainHand && offHand ? "s" : "") + "!"));

        return CommandResult.empty();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException {
        if (!testPermission(source)) return ImmutableList.of();
        AdvCmdParser.ParseResult parse = AdvCmdParser.builder()
                .arguments(arguments)
                .flagMapper(MAPPER)
                .limit(1)
                .excludeCurrent(true)
                .autoCloseQuotes(true)
                .parse();
        if (parse.current.type.equals(AdvCmdParser.CurrentElement.ElementType.ARGUMENT))
            return FCWandRegistry.getInstance().getTypes().stream()
                    .sorted()
                    .filter(new StartsWithPredicate(parse.current.token))
                    .map(args -> parse.current.prefix + args)
                    .collect(GuavaCollectors.toImmutableList());
        else if (parse.current.type.equals(AdvCmdParser.CurrentElement.ElementType.LONGFLAGKEY))
            return ImmutableList.of("player", "hand", "item").stream()
                    .filter(new StartsWithPredicate(parse.current.token))
                    .map(args -> parse.current.prefix + args)
                    .collect(GuavaCollectors.toImmutableList());
        else if (parse.current.type.equals(AdvCmdParser.CurrentElement.ElementType.LONGFLAGVALUE)) {
            if (isIn(PLAYER_ALIASES, parse.current.key))
                return Sponge.getGame().getServer().getOnlinePlayers().stream()
                        .map(Player::getName)
                        .sorted()
                        .filter(new StartsWithPredicate(parse.current.token))
                        .map(args -> parse.current.prefix + args)
                        .collect(GuavaCollectors.toImmutableList());
            else if (isIn(ITEM_ALIASES, parse.current.key)) {
                return Sponge.getRegistry().getAllOf(ItemType.class).stream()
                        .filter(itemType -> itemType != ItemTypes.NONE)
                        .map(ItemType::getId)
                        .sorted()
                        .filter(new StartsWithPredicate(parse.current.token))
                        .map(args -> parse.current.prefix + args)
                        .collect(GuavaCollectors.toImmutableList());
            } else if (isIn(HAND_ALIASES, parse.current.key)) {
                return ImmutableList.of("main", "off", "both").stream()
                        .filter(new StartsWithPredicate(parse.current.token))
                        .map(args -> parse.current.prefix + args)
                        .collect(GuavaCollectors.toImmutableList());
            }
        } else if (parse.current.type.equals(AdvCmdParser.CurrentElement.ElementType.FINAL)) {
            IWandFactory factory = FCWandRegistry.getInstance().getBuilderFromAlias(parse.args[0]);
            if (factory == null) return ImmutableList.of();
            return factory.createSuggestions(parse.current.token, source, targetPosition);
        } else if (parse.current.type.equals(AdvCmdParser.CurrentElement.ElementType.COMPLETE))
            return ImmutableList.of(parse.current.prefix + " ");
        return ImmutableList.of();
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return source.hasPermission("foxcore.command.wand");
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
        return Text.of("wand [--p:<player>]");
    }

}
