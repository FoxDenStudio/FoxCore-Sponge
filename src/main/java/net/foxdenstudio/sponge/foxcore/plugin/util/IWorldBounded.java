package net.foxdenstudio.sponge.foxcore.plugin.util;


import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Created by Fox on 6/5/2017.
 */
public interface IWorldBounded extends IWorldlessBounded, IBounded {

    World getWorld();

    @Override
    default boolean contains(int x, int y, int z, World world) {
        return world == this.getWorld() && this.contains(x, y, z);
    }

    @Override
    default boolean contains(Vector3i vec, World world) {
        return world == this.getWorld() && this.contains(vec);
    }

    @Override
    default boolean containsBlock(Location<World> loc) {
        return loc.getExtent() == this.getWorld() && this.contains(loc.getBlockPosition());
    }

    @Override
    default boolean contains(double x, double y, double z, World world) {
        return world == this.getWorld() && this.contains(x, y, z);
    }

    @Override
    default boolean contains(Vector3d vec, World world) {
        return world == this.getWorld() && this.contains(vec);
    }

    @Override
    default boolean contains(Location<World> loc) {
        return loc.getExtent() == this.getWorld() && this.contains(loc.getPosition());
    }

    @Override
    default boolean isInChunk(Vector3i chunk, World world) {
        return world == this.getWorld() && this.isInChunk(chunk);
    }
}
