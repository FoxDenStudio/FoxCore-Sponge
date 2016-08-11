package net.foxdenstudio.sponge.foxcore.plugin.wand;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fox on 4/30/2016.
 */
public class FCWandRegistry {

    private final FCWandRegistry instance = new FCWandRegistry();

    private Map<String, IWandBuilder> wandBuilders = new HashMap<>();

    private FCWandRegistry() {
    }

    public FCWandRegistry getInstance() {
        return instance;
    }

    public boolean registerBuilder(String key, IWandBuilder value) {
        if (!wandBuilders.containsKey(key)) {
            wandBuilders.put(key, value);
            return true;
        } else return false;
    }

    public IWandBuilder getBuilder(String key) {
        return wandBuilders.get(key);
    }
}
