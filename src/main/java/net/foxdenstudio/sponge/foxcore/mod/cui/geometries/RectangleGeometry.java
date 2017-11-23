package net.foxdenstudio.sponge.foxcore.mod.cui.geometries;

import com.flowpowered.math.vector.Vector3d;

public class RectangleGeometry extends QuadrangleGeometry {

    public RectangleGeometry(final Vector3d vertex1, final Vector3d vertex2, final Vector3d vertex3) {
        this(vertex1, vertex2, vertex3, null, null);
    }

    public RectangleGeometry(final Vector3d vertex1, final Vector3d vertex2, final Vector3d vertex3, PreRender preRender, PostRender postRender) {
        super(vertex1, vertex2, vertex3, vertex1.add(vertex3).sub(vertex2), preRender, postRender);
//        super(vertex1, vertex2, vertex3, Vector3d.from(vertex3.getX(), vertex1.getY(),
//                vertex1.getZ() == vertex2.getZ() ? vertex3.getZ() : vertex1.getZ()
//        ), preRender, postRender);
    }

}
