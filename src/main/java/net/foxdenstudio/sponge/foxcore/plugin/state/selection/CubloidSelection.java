package net.foxdenstudio.sponge.foxcore.plugin.state.selection;

import com.flowpowered.math.vector.Vector3i;
import net.foxdenstudio.sponge.foxcore.plugin.util.BoundingBox3;
import org.spongepowered.api.text.Text;

import java.util.Iterator;

public class CubloidSelection implements ISelection {

    BoundingBox3 boundingBox;

    public CubloidSelection(BoundingBox3 boundingBox) {
        this.boundingBox = boundingBox;
    }

    @Override
    public Iterator<Vector3i> iterator() {
        return new SelectionIterator();
    }

    @Override
    public boolean contains(int x, int y, int z) {
        return boundingBox.contains(x, y, z);
    }

    @Override
    public boolean contains(double x, double y, double z) {
        return boundingBox.contains(x, y, z);
    }

    @Override
    public Text details() {
        return Text.of(boundingBox);
    }

    @Override
    public int size() {
        return 0;
    }

    private class SelectionIterator implements Iterator<Vector3i> {
        Vector3i a = boundingBox.a;
        Vector3i b = boundingBox.b;

        int x = a.getX(), y = a.getY(), z = a.getZ();

        @Override
        public boolean hasNext() {
            return y <= b.getY();
        }

        @Override
        public Vector3i next() {
            if (hasNext()) {
                Vector3i v = new Vector3i(x, y, z);
                x++;
                if (x > b.getX()) {
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
