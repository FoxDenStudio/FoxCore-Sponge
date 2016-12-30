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

package net.foxdenstudio.sponge.foxcore.plugin.listener;

import net.foxdenstudio.sponge.foxcore.plugin.util.FCPUtil;
import net.foxdenstudio.sponge.foxcore.plugin.wand.IWand;
import net.foxdenstudio.sponge.foxcore.plugin.wand.data.WandData;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.item.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Optional;


public class WandBlockListener implements EventListener<InteractBlockEvent> {

    @Override
    public void handle(@Nonnull InteractBlockEvent event) throws Exception {
        Object root = event.getCause().root();
        if (root instanceof Player) {
            Player player = (Player) root;
            if (event instanceof InteractBlockEvent.Primary.OffHand || event instanceof InteractBlockEvent.Secondary.OffHand)
                return;

            Optional<ItemStack> mainItem = player.getItemInHand(HandTypes.MAIN_HAND), offItem = player.getItemInHand(HandTypes.OFF_HAND);

            boolean computed = false;
            boolean permission = false;
            boolean air = false;
            if (mainItem.isPresent()) {
                ItemStack itemStack = mainItem.get();
                Optional<WandData> wandDataOptional = itemStack.get(WandData.class);
                if (wandDataOptional.isPresent()) {
                    if (permission = player.hasPermission("foxcore.wand.use")) {
                        WandData wandData = wandDataOptional.get();
                        IWand wand = wandData.getWand().get();

                        boolean cancel = false;
                        if (air = (event.getTargetBlock().equals(BlockSnapshot.NONE) || event.getTargetBlock().getState().getType().equals(BlockTypes.AIR))) {
                            if (event instanceof InteractBlockEvent.Primary) {
                                cancel = wand.leftClickAir(player);
                            } else if (event instanceof InteractBlockEvent.Secondary) {
                                cancel = wand.rightClickAir(player);
                            }
                        } else {
                            if (event instanceof InteractBlockEvent.Primary) {
                                cancel = wand.leftClickBlock(player, event.getTargetBlock());
                            } else if (event instanceof InteractBlockEvent.Secondary) {
                                cancel = wand.rightClickBlock(player, event.getTargetBlock());
                            }
                        }
                        itemStack.offer(wandData);
                        FCPUtil.updateWandLore(itemStack, wand);
                        player.setItemInHand(HandTypes.MAIN_HAND, itemStack);
                        if (cancel) event.setCancelled(true);
                    }
                    computed = true;
                }
            }
            if (offItem.isPresent()) {
                ItemStack itemStack = offItem.get();
                Optional<WandData> wandDataOptional = itemStack.get(WandData.class);
                if (wandDataOptional.isPresent()) {
                    if (computed ? permission : player.hasPermission("foxcore.wand.use")) {
                        WandData wandData = wandDataOptional.get();
                        IWand wand = wandData.getWand().get();

                        if (computed ? air : (event.getTargetBlock().equals(BlockSnapshot.NONE) || event.getTargetBlock().getState().getType().equals(BlockTypes.AIR))) {
                            if (event instanceof InteractBlockEvent.Primary) {
                                wand.leftClickAir(player);
                            } else if (event instanceof InteractBlockEvent.Secondary) {
                                wand.rightClickAir(player);
                            }
                        } else {
                            if (event instanceof InteractBlockEvent.Primary) {
                                wand.leftClickBlock(player, event.getTargetBlock());
                            } else if (event instanceof InteractBlockEvent.Secondary) {
                                wand.rightClickBlock(player, event.getTargetBlock());
                            }
                        }
                        itemStack.offer(wandData);
                        FCPUtil.updateWandLore(itemStack, wand);
                        player.setItemInHand(HandTypes.OFF_HAND, itemStack);
                    }
                }
            }
        }
    }

    /*@Override
    public void handle(InteractBlockEvent event) throws Exception {
        Optional<Player> playerOptional = event.getCause().first(Player.class);
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            if (!event.getTargetBlock().getState().getType().equals(BlockTypes.AIR) && player.getItemInHand().isPresent()) {
                ItemStack item = player.getItemInHand().get();

                if (item.get(WandData.class).isPresent() && player.hasPermission("foxcore.wand.use")) {
                    Vector3i pos = event.getTargetBlock().getPosition();
                    List<Vector3i> positions = FCCUtil.getPositions(player);
                    if (event instanceof InteractBlockEvent.Primary) {
                        if (positions.contains(pos)) {
                            positions.remove(positions.lastIndexOf(pos));
                            player.sendMessage(Text.of(TextColors.GREEN, "Successfully removed position (" + pos.getPosX() + ", " + pos.getPosY() + ", " + pos.getZ() + ")!"));
                            FCCUtil.updatePositions(player);
                        }
                    } else if (event instanceof InteractBlockEvent.Secondary) {
                        positions.add(pos);
                        player.sendMessage(Text.of(TextColors.GREEN, "Successfully added position (" + pos.getPosX() + ", " + pos.getPosY() + ", " + pos.getZ() + ")!"));
                        FCCUtil.updatePositions(player);
                    }
                    FCStateManager.instance().getStateMap().get(player).updateScoreboard();
                    event.setCancelled(true);
                }
            }
        }
    }*/


}
