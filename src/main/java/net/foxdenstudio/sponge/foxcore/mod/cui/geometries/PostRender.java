package net.foxdenstudio.sponge.foxcore.mod.cui.geometries;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;

public abstract class PostRender {
    abstract void accept(Tessellator tessellator, VertexBuffer vertexBuffer, float partialTicks);
}