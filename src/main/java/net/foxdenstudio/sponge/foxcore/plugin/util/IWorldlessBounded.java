package net.foxdenstudio.sponge.foxcore.plugin.util;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

public interface IWorldlessBounded {

    boolean contains(int x, int y, int z);

    default boolean contains(Vector3i vec) {
        return this.contains(vec.getX(), vec.getY(), vec.getZ());
    }

    boolean contains(double x, double y, double z);

    default boolean contains(Vector3d vec) {
        return this.contains(vec.getX(), vec.getY(), vec.getZ());
    }

    default boolean isInChunk(Vector3i chunk) {
        return true;
    }

}
