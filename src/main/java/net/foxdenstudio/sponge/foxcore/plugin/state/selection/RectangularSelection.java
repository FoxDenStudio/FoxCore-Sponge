package net.foxdenstudio.sponge.foxcore.plugin.state.selection;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.text.Text;

import java.util.Iterator;

public class RectangularSelection implements ISelection {

    Vector3i lower;
    Vector3i upper;

    public RectangularSelection(Vector3i lower, Vector3i upper) {
        this.lower = lower;
        this.upper = upper;
    }

    @Override
    public Iterator<Vector3i> iterator() {
        return new RectIterator();
    }

    @Override
    public boolean contains(int x, int y, int z) {
        return false;
    }

    @Override
    public boolean contains(Vector3i vec) {
        return false;
    }

    @Override
    public boolean contains(double x, double y, double z) {
        return false;
    }

    @Override
    public boolean contains(Vector3d vec) {
        return false;
    }

    @Override
    public Text details() {
        return Text.of(lower + ", " + upper);
    }

    private class RectIterator implements Iterator<Vector3i> {
        Vector3i a = lower;
        Vector3i b = upper;

        int x = a.getX(), y = a.getY(), z = a.getZ();

        @Override
        public boolean hasNext() {
            return y <= b.getY();
        }

        @Override
        public Vector3i next() {
            if(hasNext()){
                Vector3i v = new Vector3i(x,y,z);
                x++;
                if(x > b.getX()){
                    x = a.getX();
                    z++;
                }
                if (z > b.getZ()) {
                    z = a.getZ();
                    y++;
                }
                return v;
            } else return null;
        }
    }
}
