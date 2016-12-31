package net.foxdenstudio.sponge.foxcore.mod.rendernew.world.geometry;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector4f;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glColor4f;

public class GeometrySquare extends Geometry {
    protected Vector3d start, end;

    public GeometrySquare(Vector3d start, Vector3d end, Vector4f color) {
        this.start = start;
        this.end = end;
        this.color = color;

        this.position = start.add(end).div(2);
    }

    @Override
    public void render() {
        glColor4f(this.color.getX(), this.color.getY(), this.color.getZ(), this.color.getW());

        this.vertexBuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
        this.vertexBuffer.pos(this.start.getX(), this.start.getY(), this.start.getZ()).endVertex();
        this.vertexBuffer.pos(this.start.getX(), this.end.getY(), this.start.getZ()).endVertex();
        this.vertexBuffer.pos(this.end.getX(), this.end.getY(), this.end.getZ()).endVertex();
        this.vertexBuffer.pos(this.end.getX(), this.start.getY(), this.end.getZ()).endVertex();
        this.tessellator.draw();
    }

    public boolean pointLiesWithin(double x, double y, double z) {
        final boolean isInX = x >= this.start.getX() && x <= this.end.getX();
        final boolean isInY = y >= this.start.getY() && y <= this.end.getY();
        final boolean isInZ = z >= this.start.getZ() && z <= this.end.getZ();
        return isInX && isInY && isInZ;
    }
}
