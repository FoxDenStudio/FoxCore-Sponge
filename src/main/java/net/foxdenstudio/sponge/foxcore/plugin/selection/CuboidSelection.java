package net.foxdenstudio.sponge.foxcore.plugin.selection;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;
import net.foxdenstudio.sponge.foxcore.plugin.command.util.ProcessResult;
import net.foxdenstudio.sponge.foxcore.plugin.util.BoundingBox3;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class CuboidSelection implements ISelection {

    public BoundingBox3 boundingBox;

    public CuboidSelection(BoundingBox3 boundingBox) {
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
    public Text overview() {
        return Text.of(boundingBox);
    }

    @Override
    public Optional<Text> details() {
        return Optional.empty();
    }

    @Override
    public String type() {
        return "cuboid";
    }

    @Override
    public int size() {
        return (int) boundingBox.size();
    }

    @Override
    public Optional<BoundingBox3> bounds() {
        return Optional.of(boundingBox);
    }

    @Override
    public ProcessResult modify(CommandSource source, String arguments) throws CommandException {
        return ProcessResult.failure();
    }

    @Override
    public List<String> modifySuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException {
        return ImmutableList.of();
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
