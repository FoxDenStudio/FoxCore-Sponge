package net.foxdenstudio.sponge.foxcore.mod.cui.geometries;

import com.flowpowered.math.vector.Vector3i;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;

public abstract class BaseGeometry {
    public abstract void render(final Tessellator tessellator, final VertexBuffer vertexBuffer, final float partialTicks, final Vector3i offset);
}
