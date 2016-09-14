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

import net.foxdenstudio.sponge.foxcore.common.util.CacheMap;
import net.foxdenstudio.sponge.foxcore.plugin.command.CommandHUD;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scoreboard.Score;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SourceState {

    private CommandSource source;

    private Map<String, IStateField> state = new CacheMap<>((key, map) -> {
        if (key instanceof String) {
            IStateField field = FCStateManager.instance().newStateField((String) key, this);
            if (field != null) {
                map.put((String) key, field);
                return field;
            }
        }
        return null;
    });

    public SourceState(CommandSource source) {
        this.source = source;
    }

    public Map<String, IStateField> getMap() {
        return this.state;
    }

    public Optional<IStateField> getOrCreate(String identifier) {
        return Optional.ofNullable(this.state.get(identifier));
    }

    public Optional<IStateField> get(Class<? extends IStateField> clazz) {
        for (Map.Entry<String, IStateField> entry : this.state.entrySet()) {
            if (clazz.isInstance(entry.getValue())) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

    public Optional<IStateField> getOrCreateFromAlias(String alias) {
        String id = FCStateManager.instance().getID(alias);
        if (id != null) {
            return Optional.of(this.state.get(id));
        } else return Optional.empty();
    }

    public void flush() {
        this.flush(true);
    }

    public void flush(boolean updateScoreboard) {
        state.values().forEach(IStateField::flush);
        if (updateScoreboard) updateScoreboard();
    }

    public void flush(String field) {
        this.flush(true, field);
    }

    public void flush(boolean updateScoreboard, String field) {
        if (state.containsKey(field)) state.get(field).flush();
        if (updateScoreboard) updateScoreboard();
    }

    public void flush(String... fields) {
        this.flush(true, fields);
    }

    public void flush(boolean updateScoreboard, String... fields) {
        for (String field : fields) {
            this.flush(false, field);
        }
        if (updateScoreboard) updateScoreboard();
    }

    public CommandSource getSource() {
        return source;
    }

    public void updateScoreboard() {
        if (source instanceof Player && CommandHUD.instance().getIsHUDEnabled().get(source)) {
            Scoreboard scoreboard = Scoreboard.builder().build();
            Objective objective = Objective.builder().displayName(Text.of(TextColors.GOLD, "    Foxguard State    ")).name("foxguardstate").criterion(Criteria.DUMMY).build();
            scoreboard.addObjective(objective);
            Map<IStateField, List<Text>> stateText = new HashMap<>();
            this.state.values().stream().filter(IStateField::showScoreboard).forEach(field -> stateText.put(field, field.getScoreboardText()));
            int entries = stateText.values().stream().mapToInt(List::size).sum();
            int available = 15 - stateText.size();
            if (available < 1) {
                int index = 15;
                for (IStateField field : stateText.keySet()) {
                    Score score = objective.getOrCreateScore(field.getScoreboardTitle().orElse(Text.of(TextColors.GREEN, field.getName())));
                    score.setScore(index--);
                    if (index < 1) break;
                }
            } else if (entries > available) {
                Map<IStateField, Double> score = new HashMap<>();
                for (Map.Entry<IStateField, List<Text>> entry : stateText.entrySet()) {
                    score.put(entry.getKey(), ((double) entry.getValue().size()) * available / entries);
                }
                Map<IStateField, Integer> count = new HashMap<>();
                for (Map.Entry<IStateField, Double> entry : score.entrySet()) {
                    count.put(entry.getKey(), (int) Math.floor(entry.getValue()));
                    entry.setValue(entry.getValue() % 1);
                }
                int left = available - count.values().stream().mapToInt(Integer::intValue).sum();
                List<IStateField> priorityList = score.keySet().stream().sorted((o1, o2) -> score.get(o2).compareTo(score.get(o1))).collect(Collectors.toList());
                for (int i = 0; i < left; i++) {
                    IStateField field = priorityList.get(i);
                    count.put(field, count.get(field) + 1);
                }
                int index = 15;
                for (Map.Entry<IStateField, List<Text>> entry : stateText.entrySet()) {
                    Score fieldScore = objective.getOrCreateScore(entry.getKey().getScoreboardTitle().orElse(Text.of(TextColors.GREEN, entry.getKey().getName())));
                    fieldScore.setScore(index--);
                    final int end = entry.getKey().prioritizeLast() ? entry.getValue().size() : count.get(entry.getKey());
                    for (int i = (entry.getKey().prioritizeLast() ? entry.getValue().size() - count.get(entry.getKey()) : 0); i < end; i++) {
                        Score lineScore = objective.getOrCreateScore(entry.getValue().get(i));
                        lineScore.setScore(index--);
                    }
                }
            } else {
                int index = entries + (int) (stateText.values().stream().filter(list -> list.size() > 0).count());
                for (Map.Entry<IStateField, List<Text>> entry : stateText.entrySet()) {
                    Score fieldScore = objective.getOrCreateScore(entry.getKey().getScoreboardTitle().orElse(Text.of(TextColors.GREEN, entry.getKey().getName())));
                    fieldScore.setScore(index--);
                    for (Text line : entry.getValue()) {
                        Score lineScore = objective.getOrCreateScore(line);
                        lineScore.setScore(index--);
                    }
                }
            }
            scoreboard.updateDisplaySlot(objective, DisplaySlots.SIDEBAR);
            Optional<Scoreboard> serverScoreboard = Sponge.getServer().getServerScoreboard();
            if (objective.getScores().size() == 0 && serverScoreboard.isPresent()) {
                ((Player) source).setScoreboard(serverScoreboard.get());
            } else {
                ((Player) source).setScoreboard(scoreboard);
            }
        }
    }
}
