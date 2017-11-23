package net.foxdenstudio.sponge.foxcore.mod.cui.region;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Region {
    final String type;
    final String name;
    final UUID owner;
    final boolean enabled;
    final String world;
    final String ownerDisplayName;
    final Map<String, Object> data;
    final List<String> links;

    public Region(String type, String name, UUID owner, boolean enabled, String world, String ownerDisplayName, Map<String, Object> data, List<String> links) {
        this.type = type;
        this.name = name;
        this.owner = owner;
        this.enabled = enabled;
        this.world = world;
        this.ownerDisplayName = ownerDisplayName;
        this.data = data;
        this.links = links;
    }

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public UUID getOwner() {
        return this.owner;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getWorld() {
        return this.world;
    }

    public String getOwnerDisplayName() {
        return this.ownerDisplayName;
    }

    public Map<String, Object> getData() {
        return this.data;
    }

    public List<String> getLinks() {
        return this.links;
    }
}
