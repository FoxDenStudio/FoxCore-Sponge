package net.foxdenstudio.sponge.foxcore.mod.rendernew.world.geometry;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;

/**
 * Created by Fox on 12/4/2016.
 */
public abstract class Geometry {

    protected final Tessellator tessellator = Tessellator.getInstance();
    protected final VertexBuffer vertexBuffer = this.tessellator.getBuffer();
    public double distance;
    public Vector3d position;
    public Vector4f color;

    abstract public void render();


}
