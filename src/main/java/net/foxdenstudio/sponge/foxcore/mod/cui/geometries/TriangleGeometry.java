package net.foxdenstudio.sponge.foxcore.mod.cui.geometries;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import static org.lwjgl.opengl.GL11.*;

public class TriangleGeometry extends BaseGeometry {
    private Vector3d vertex1;
    private Vector3d vertex2;
    private Vector3d vertex3;

    private PreRender preRender;
    private PostRender postRender;

    public TriangleGeometry() {
        this(Vector3d.ZERO, Vector3d.ZERO, Vector3d.ZERO);
    }

    public TriangleGeometry(Vector3d vertex1, Vector3d vertex2, Vector3d vertex3) {
        this(vertex1, vertex2, vertex3, null, null);
    }

    public TriangleGeometry(Vector3d vertex1, Vector3d vertex2, Vector3d vertex3, PreRender preRender, PostRender postRender) {
        this.vertex1 = vertex1;
        this.vertex2 = vertex2;
        this.vertex3 = vertex3;
        this.preRender = preRender;
        this.postRender = postRender;
    }

    public Vector3d getVertex1() {
        return this.vertex1;
    }

    public TriangleGeometry setVertex1(Vector3d vertex1) {
        this.vertex1 = vertex1;
        return this;
    }

    public Vector3d getVertex2() {
        return this.vertex2;
    }

    public TriangleGeometry setVertex2(Vector3d vertex2) {
        this.vertex2 = vertex2;
        return this;
    }

    public Vector3d getVertex3() {
        return this.vertex3;
    }

    public TriangleGeometry setVertex3(Vector3d vertex3) {
        this.vertex3 = vertex3;
        return this;
    }

    public PreRender getPreRender() {
        return this.preRender;
    }

    public TriangleGeometry setPreRender(PreRender preRender) {
        this.preRender = preRender;
        return this;
    }

    public PostRender getPostRender() {
        return this.postRender;
    }

    public TriangleGeometry setPostRender(PostRender postRender) {
        this.postRender = postRender;
        return this;
    }

    @Override
    public void render(Tessellator tessellator, VertexBuffer vertexBuffer, float partialTicks, Vector3i offset) {
        glPushMatrix();

        glPushAttrib(GL_CULL_FACE);
        glDisable(GL_CULL_FACE);

        final Vector3d offsetNew = offset.toDouble();
        final Vector3d vertex1new = this.vertex1.add(offsetNew);
        final Vector3d vertex2new = this.vertex2.add(offsetNew);
        final Vector3d vertex3new = this.vertex3.add(offsetNew);

        if (this.preRender != null) {
            this.preRender.accept(tessellator, vertexBuffer, partialTicks);
            vertexBuffer.begin(GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
            vertexBuffer.pos(vertex1new.getX(), vertex1new.getY(), vertex1new.getZ()).color(this.preRender.getColorR(), this.preRender.getColorG(), this.preRender.getColorB(), this.preRender.getColorA()).endVertex();
            vertexBuffer.pos(vertex2new.getX(), vertex2new.getY(), vertex2new.getZ()).color(this.preRender.getColorR(), this.preRender.getColorG(), this.preRender.getColorB(), this.preRender.getColorA()).endVertex();
            vertexBuffer.pos(vertex3new.getX(), vertex3new.getY(), vertex3new.getZ()).color(this.preRender.getColorR(), this.preRender.getColorG(), this.preRender.getColorB(), this.preRender.getColorA()).endVertex();
            tessellator.draw();
        } else {
            vertexBuffer.begin(GL_TRIANGLES, DefaultVertexFormats.POSITION);
            vertexBuffer.pos(vertex1new.getX(), vertex1new.getY(), vertex1new.getZ()).endVertex();
            vertexBuffer.pos(vertex2new.getX(), vertex2new.getY(), vertex2new.getZ()).endVertex();
            vertexBuffer.pos(vertex3new.getX(), vertex3new.getY(), vertex3new.getZ()).endVertex();
            tessellator.draw();
        }
        if (this.postRender != null) {
            this.postRender.accept(tessellator, vertexBuffer, partialTicks);
        }

        glPopAttrib();

        glPopMatrix();
    }


}
