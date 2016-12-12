package net.foxdenstudio.sponge.foxcore.mod.rendernew.world.renderable;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import net.foxdenstudio.sponge.foxcore.mod.rendernew.world.geometry.Geometry;
import net.foxdenstudio.sponge.foxcore.mod.rendernew.world.geometry.GeometryLine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Fox on 12/4/2016.
 */
public class Highlight implements IRenderable {

    private static final double OFFSET = 0.01;
    private static final float OPACITY = 0.5f;

    Vector3i pos;
    Vector3f color;

    List<Geometry> geometries = new ArrayList<>();

    public Highlight(Vector3i pos) {
        this(pos, Vector3f.ONE);
    }

    public Highlight(Vector3i pos, Vector3f color) {
        this.pos = pos;
        this.color = color;
    }

    @Override
    public Collection<Geometry> getGeometry() {
        if (geometries.isEmpty()) {
            final Vector3d posD = pos.toDouble();
            final Vector3d v1 = posD.add(-OFFSET, -OFFSET, -OFFSET);
            final Vector3d v2 = posD.add(1 + OFFSET, -OFFSET, -OFFSET);
            final Vector3d v3 = posD.add(-OFFSET, 1 + OFFSET, -OFFSET);
            final Vector3d v4 = posD.add(-OFFSET, -OFFSET, 1 + OFFSET);
            final Vector3d v5 = posD.add(1 + OFFSET, 1 + OFFSET, -OFFSET);
            final Vector3d v6 = posD.add(-OFFSET, 1 + OFFSET, 1 + OFFSET);
            final Vector3d v7 = posD.add(1 + OFFSET, -OFFSET, 1 + OFFSET);
            final Vector3d v8 = posD.add(1 + OFFSET, 1 + OFFSET, 1 + OFFSET);

            geometries.add(new GeometryLine(v1, v2, color, OPACITY));
            geometries.add(new GeometryLine(v1, v3, color, OPACITY));
            geometries.add(new GeometryLine(v1, v4, color, OPACITY));
            geometries.add(new GeometryLine(v2, v5, color, OPACITY));
            geometries.add(new GeometryLine(v2, v7, color, OPACITY));
            geometries.add(new GeometryLine(v3, v5, color, OPACITY));
            geometries.add(new GeometryLine(v3, v6, color, OPACITY));
            geometries.add(new GeometryLine(v4, v6, color, OPACITY));
            geometries.add(new GeometryLine(v4, v7, color, OPACITY));
            geometries.add(new GeometryLine(v5, v8, color, OPACITY));
            geometries.add(new GeometryLine(v6, v8, color, OPACITY));
            geometries.add(new GeometryLine(v7, v8, color, OPACITY));
        }
        return geometries;
    }
}
