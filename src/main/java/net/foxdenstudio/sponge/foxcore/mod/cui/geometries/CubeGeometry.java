package net.foxdenstudio.sponge.foxcore.mod.cui.geometries;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;

public class CubeGeometry extends BaseGeometry {

    private final QuadrangleGeometry[] faces = new QuadrangleGeometry[6];
    private final boolean[] hideFace = new boolean[6];
    private final PreRender preRender = new PreRender() {
        @Override
        public void accept(Tessellator tessellator, VertexBuffer vertexBuffer, float partialTicks) {
            this.setColorR(0);
            this.setColorG(0);
            this.setColorB(1);
            this.setColorA(1);
        }
    };

    public CubeGeometry(final Vector3d vertex1, final Vector3d vertex2, final Vector3d vertex3) {
//        this.faces[0] = new RectangleGeometry(, , , this.preRender, null);
//        this.faces[1] = new RectangleGeometry(, , , this.preRender, null);
//        this.faces[2] = new RectangleGeometry(, , , this.preRender, null);
//        this.faces[3] = new RectangleGeometry(, , , this.preRender, null);
//        this.faces[4] = new RectangleGeometry(, , , this.preRender, null);
//        this.faces[5] = new RectangleGeometry(, , , this.preRender, null);
    }

    @Override
    public void render(Tessellator tessellator, VertexBuffer vertexBuffer, float partialTicks, Vector3i offset) {
        for (int i = 0; i < this.faces.length; i++) {
            if (this.faces[i] != null) {
                if (!this.hideFace[i]) {
                    this.faces[i].render(tessellator, vertexBuffer, partialTicks, offset);
                }
            }
        }
    }
}
