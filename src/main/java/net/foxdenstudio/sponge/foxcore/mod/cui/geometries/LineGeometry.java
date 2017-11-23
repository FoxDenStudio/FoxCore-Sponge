package net.foxdenstudio.sponge.foxcore.mod.cui.geometries;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import static org.lwjgl.opengl.GL11.*;

public class LineGeometry extends BaseGeometry {

    private Vector3d vertex1;
    private Vector3d vertex2;
    private PreRender preRender;
    private PostRender postRender;

    public LineGeometry(Vector3d vertex1, Vector3d vertex2) {
        this(vertex1, vertex2, null, null);
    }

    public LineGeometry(Vector3d vertex1, Vector3d vertex2, PreRender preRender, PostRender postRender) {
        this.vertex1 = vertex1;
        this.vertex2 = vertex2;
        this.preRender = preRender;
        this.postRender = postRender;
    }

    @Override
    public void render(Tessellator tessellator, VertexBuffer vertexBuffer, float partialTicks, Vector3i offset) {
        glPushMatrix();
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth(4);

        final Vector3d offsetNew = offset.toDouble();
        final Vector3d vertex1new = this.vertex1.add(offsetNew);
        final Vector3d vertex2new = this.vertex2.add(offsetNew);

        if (this.preRender != null) {
            this.preRender.accept(tessellator, vertexBuffer, partialTicks);
            vertexBuffer.begin(GL_LINES, DefaultVertexFormats.POSITION_COLOR);
            vertexBuffer.pos(vertex1new.getX(), vertex1new.getY(), vertex1new.getZ()).color(this.preRender.getColorR(), this.preRender.getColorG(), this.preRender.getColorB(), this.preRender.getColorA()).endVertex();
            vertexBuffer.pos(vertex2new.getX(), vertex2new.getY(), vertex2new.getZ()).color(this.preRender.getColorR(), this.preRender.getColorG(), this.preRender.getColorB(), this.preRender.getColorA()).endVertex();
            tessellator.draw();
        } else {
            vertexBuffer.begin(GL_LINES, DefaultVertexFormats.POSITION);
            vertexBuffer.pos(vertex1new.getX(), vertex1new.getY(), vertex1new.getZ()).endVertex();
            vertexBuffer.pos(vertex2new.getX(), vertex2new.getY(), vertex2new.getZ()).endVertex();
            tessellator.draw();
        }
        if (this.postRender != null) {
            this.postRender.accept(tessellator, vertexBuffer, partialTicks);
        }

        glPopMatrix();
    }
}
