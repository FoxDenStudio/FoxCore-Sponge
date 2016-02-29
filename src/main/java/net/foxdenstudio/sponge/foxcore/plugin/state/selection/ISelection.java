package net.foxdenstudio.sponge.foxcore.plugin.state.selection;

import com.flowpowered.math.vector.Vector3i;
import net.foxdenstudio.sponge.foxcore.plugin.util.IBounded;
import org.spongepowered.api.text.Text;

public interface ISelection extends Iterable<Vector3i>, IBounded {

    Text details();

    int size();

}
