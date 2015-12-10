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

import net.foxdenstudio.foxcore.state.factory.IStateFieldFactory;

import java.util.HashSet;
import java.util.Set;

import static net.foxdenstudio.foxcore.util.Aliases.isAlias;

public final class FCStateRegistry {

    private static FCStateRegistry instance;

    private Set<StateMapping> stateMappings = new HashSet<>();

    public static void init() {
        if (instance == null) instance = new FCStateRegistry();
    }

    public static FCStateRegistry instance() {
        return instance;
    }

    public boolean registerStateFactory(IStateFieldFactory factory, String identifier, String... aliases) {
        for (StateMapping mapping : this.stateMappings) {
            if (mapping.identifier.equals(identifier)) return false;
            for (String alias : aliases) {
                if (isAlias(mapping.aliases, alias)) return false;
            }
        }
        this.stateMappings.add(new StateMapping(factory, identifier, aliases));
        return true;
    }

    public IStateField newStateField(String alias) {
        StateMapping mapping = getMappingbyAlias(alias);
        if (mapping != null) {
            return mapping.factory.createStateField();
        } else return null;
    }

    public String getID(String alias) {
        StateMapping mapping = getMappingbyAlias(alias);
        if (mapping != null) {
            return mapping.identifier;
        } else return null;
    }

    private StateMapping getMappingbyID(String identifier) {
        for (StateMapping mapping : this.stateMappings) {
            if (mapping.identifier.equals(identifier)) return mapping;
        }
        return null;
    }

    private StateMapping getMappingbyAlias(String alias) {
        for (StateMapping mapping : this.stateMappings) {
            if (isAlias(mapping.aliases, alias)) return mapping;
        }
        return null;
    }

    private static class StateMapping {
        public final IStateFieldFactory factory;
        public final String identifier;
        public final String[] aliases;

        public StateMapping(IStateFieldFactory factory, String identifier, String[] aliases) {
            this.factory = factory;
            this.identifier = identifier;
            this.aliases = aliases;
        }
    }

}
