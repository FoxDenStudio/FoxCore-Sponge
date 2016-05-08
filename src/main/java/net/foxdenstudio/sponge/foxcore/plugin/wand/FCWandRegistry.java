package net.foxdenstudio.sponge.foxcore.plugin.wand;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by Fox on 4/30/2016.
 */
public class FCWandRegistry {

    private final FCWandRegistry instance = new FCWandRegistry();

    private Map<String, Supplier<IWand>> wandTypes = new HashMap<>();

    private FCWandRegistry() {
    }

    public FCWandRegistry getInstance() {
        return instance;
    }

    public boolean register(String key, Supplier<IWand> value) {
        if (!wandTypes.containsKey(key)) {
            wandTypes.put(key, value);
            return true;
        } else return false;
    }
}
