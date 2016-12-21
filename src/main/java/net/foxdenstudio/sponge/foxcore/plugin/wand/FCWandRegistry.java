package net.foxdenstudio.sponge.foxcore.plugin.wand;

import com.google.common.collect.ImmutableSet;
import net.foxdenstudio.sponge.foxcore.plugin.util.Aliases;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Fox on 4/30/2016.
 */
public class FCWandRegistry {

    private static final FCWandRegistry instance = new FCWandRegistry();

    private Map<String, IWandFactory> wandBuilders = new HashMap<>();
    private Map<Integer, IWand> wandCache = new HashMap<>();

    private FCWandRegistry() {
    }

    public static FCWandRegistry getInstance() {
        return instance;
    }

    public boolean registerBuilder(String key, IWandFactory value) {
        if (!wandBuilders.containsKey(key)) {
            wandBuilders.put(key, value);
            return true;
        } else return false;
    }

    public IWandFactory getBuilder(String key) {
        return wandBuilders.get(key);
    }

    public IWandFactory getBuilderFromAlias(String alias) {
        for (IWandFactory factory : wandBuilders.values()) {
            if (Aliases.isIn(factory.aliases(), alias)) return factory;
        }
        return null;
    }

    public Set<String> getTypes() {
        return ImmutableSet.copyOf(wandBuilders.keySet());
    }
}
