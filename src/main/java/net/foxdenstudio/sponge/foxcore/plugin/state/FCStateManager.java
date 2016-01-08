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

import net.foxdenstudio.sponge.foxcore.plugin.state.factory.IStateFieldFactory;
import net.foxdenstudio.sponge.foxcore.plugin.util.CallbackHashMap;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.util.GuavaCollectors;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.foxdenstudio.sponge.foxcore.plugin.util.Aliases.isIn;

public final class FCStateManager {

    private static FCStateManager instance;

    private final Map<CommandSource, SourceState> stateMap = new CallbackHashMap<>((o, m) -> {
        if (o instanceof CommandSource) {
            SourceState state = new SourceState((CommandSource) o);
            m.put((CommandSource) o, state);
            return state;
        }
        return null;
    });
    private Set<StateMapping> stateMappings = new HashSet<>();

    public static void init() {
        if (instance == null) instance = new FCStateManager();
    }

    public static FCStateManager instance() {
        return instance;
    }

    public Map<CommandSource, SourceState> getStateMap() {
        return stateMap;
    }

    public boolean registerStateFactory(IStateFieldFactory factory, String identifier, String primaryAlias, String... aliases) {
        for (StateMapping mapping : this.stateMappings) {
            if (mapping.identifier.equals(identifier)) return false;
            for (String alias : aliases) {
                if (isIn(mapping.aliases, alias)) return false;
            }
        }
        this.stateMappings.add(new StateMapping(factory, identifier, primaryAlias, aliases));
        return true;
    }

    public IStateField newStateField(String alias, SourceState sourceState) {
        StateMapping mapping = getMappingbyAlias(alias);
        if (mapping != null) {
            return mapping.factory.createStateField(sourceState);
        } else return null;
    }

    public String getID(String alias) {
        StateMapping mapping = getMappingbyAlias(alias);
        if (mapping != null) {
            return mapping.identifier;
        } else return null;
    }

    public List<String> getPrimaryAliases() {
        String[] array = new String[this.stateMappings.size()];
        return this.stateMappings.stream().map(stateMapping -> stateMapping.primaryAlias).collect(GuavaCollectors.toImmutableList());
    }

    private StateMapping getMappingbyID(String identifier) {
        for (StateMapping mapping : this.stateMappings) {
            if (mapping.identifier.equals(identifier)) return mapping;
        }
        return null;
    }

    private StateMapping getMappingbyAlias(String alias) {
        for (StateMapping mapping : this.stateMappings) {
            if (isIn(mapping.aliases, alias)) return mapping;
        }
        return null;
    }

    private static class StateMapping {
        public final IStateFieldFactory factory;
        public final String identifier;
        public final String primaryAlias;
        public final String[] aliases;

        public StateMapping(IStateFieldFactory factory, String identifier, String primaryAlias, String[] aliases) {
            this.factory = factory;
            this.identifier = identifier;
            this.primaryAlias = primaryAlias;
            this.aliases = aliases;
        }
    }

}
