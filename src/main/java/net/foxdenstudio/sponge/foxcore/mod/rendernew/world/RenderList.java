package net.foxdenstudio.sponge.foxcore.mod.rendernew.world;

import net.foxdenstudio.sponge.foxcore.mod.rendernew.world.geometry.Geometry;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

/**
 * Created by Fox on 12/4/2016.
 */
public class RenderList extends ArrayList<Geometry> {

    public void sortZ(double x, double y, double z) {
        try {
            for (Geometry geo : this) {
                geo.distance = geo.position.distanceSquared(x, y, z);
            }
            this.sort((o1, o2) -> o1.distance > o2.distance ? -1 : (o1.distance < o2.distance ? 1 : 0));
        } catch (ConcurrentModificationException ignored) {
        }
    }
}
