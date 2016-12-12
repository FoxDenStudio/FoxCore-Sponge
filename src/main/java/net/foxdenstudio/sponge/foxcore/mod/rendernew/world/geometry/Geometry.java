package net.foxdenstudio.sponge.foxcore.mod.rendernew.world.geometry;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3f;

import java.util.Vector;

/**
 * Created by Fox on 12/4/2016.
 */
public abstract class Geometry {

    public double distance;
    public Vector3d position;
    public Vector3f color;
    public float opacity;

    abstract public void render();



}
