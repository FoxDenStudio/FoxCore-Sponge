package net.foxdenstudio.sponge.foxcore.plugin.state.selection;

import com.flowpowered.math.vector.Vector3i;
import net.foxdenstudio.sponge.foxcore.plugin.util.BoundingBox3;
import net.foxdenstudio.sponge.foxcore.plugin.util.IWorldBounded;
import org.spongepowered.api.text.Text;

public interface ISelection extends Iterable<Vector3i>, IWorldBounded {

    Text details();

    int size();

    BoundingBox3 bounds();
}
