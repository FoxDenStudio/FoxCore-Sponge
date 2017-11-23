package net.foxdenstudio.sponge.foxcore.mod.cui.geometries;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import static org.lwjgl.opengl.GL11.*;

public class QuadrangleGeometry extends BaseGeometry {
    private Vector3d vertex1;
    private Vector3d vertex2;
    private Vector3d vertex3;
    private Vector3d vertex4;

    private PreRender preRender;
    private PostRender postRender;

    public QuadrangleGeometry() {
        this(Vector3d.ZERO, Vector3d.ZERO, Vector3d.ZERO, Vector3d.ZERO);
    }

    public QuadrangleGeometry(Vector3d vertex1, Vector3d vertex2, Vector3d vertex3, Vector3d vertex4) {
        this(vertex1, vertex2, vertex3, vertex4, null, null);
    }

    public QuadrangleGeometry(Vector3d vertex1, Vector3d vertex2, Vector3d vertex3, Vector3d vertex4, PreRender preRender, PostRender postRender) {
        this.vertex1 = vertex1;
        this.vertex2 = vertex2;
        this.vertex3 = vertex3;
        this.vertex4 = vertex4;
//        System.out.println(vertex1 + " | " + vertex2 + "  | " + vertex3 + " | " + vertex4);
        this.preRender = preRender;
        this.postRender = postRender;
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
        final Vector3d vertex4new = this.vertex4.add(offsetNew);

        if (this.preRender != null) {
            this.preRender.accept(tessellator, vertexBuffer, partialTicks);
            vertexBuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            vertexBuffer.pos(vertex1new.getX(), vertex1new.getY(), vertex1new.getZ()).color(this.preRender.getColorR(), this.preRender.getColorG(), this.preRender.getColorB(), this.preRender.getColorA()).endVertex();
            vertexBuffer.pos(vertex2new.getX(), vertex2new.getY(), vertex2new.getZ()).color(this.preRender.getColorR(), this.preRender.getColorG(), this.preRender.getColorB(), this.preRender.getColorA()).endVertex();
            vertexBuffer.pos(vertex3new.getX(), vertex3new.getY(), vertex3new.getZ()).color(this.preRender.getColorR(), this.preRender.getColorG(), this.preRender.getColorB(), this.preRender.getColorA()).endVertex();
            vertexBuffer.pos(vertex4new.getX(), vertex4new.getY(), vertex4new.getZ()).color(this.preRender.getColorR(), this.preRender.getColorG(), this.preRender.getColorB(), this.preRender.getColorA()).endVertex();
            tessellator.draw();
        } else {
            vertexBuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
            vertexBuffer.pos(vertex1new.getX(), vertex1new.getY(), vertex1new.getZ()).endVertex();
            vertexBuffer.pos(vertex2new.getX(), vertex2new.getY(), vertex2new.getZ()).endVertex();
            vertexBuffer.pos(vertex3new.getX(), vertex3new.getY(), vertex3new.getZ()).endVertex();
            vertexBuffer.pos(vertex4new.getX(), vertex4new.getY(), vertex4new.getZ()).endVertex();
            tessellator.draw();
        }
        if (this.postRender != null) {
            this.postRender.accept(tessellator, vertexBuffer, partialTicks);
        }

        glPopAttrib();

        glPopMatrix();
    }
}
