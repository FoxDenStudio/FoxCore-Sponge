package net.foxdenstudio.sponge.foxcore.plugin.util;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.world.World;

/**
 * Created by Fox on 3/29/2016.
 */
public interface IBounded {

    boolean contains(int x, int y, int z, World world);

    default boolean contains(Vector3i vec, World world) {
        return this.contains(vec.getX(), vec.getY(), vec.getZ(), world);
    }

    boolean contains(double x, double y, double z, World world);

    default boolean contains(Vector3d vec, World world) {
        return this.contains(vec.getX(), vec.getY(), vec.getZ(), world);
    }

    default boolean isInChunk(Vector3i chunk, World world) {
        return true;
    }
}
