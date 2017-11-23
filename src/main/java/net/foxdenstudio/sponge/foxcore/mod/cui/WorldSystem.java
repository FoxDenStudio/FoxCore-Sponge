package net.foxdenstudio.sponge.foxcore.mod.cui;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import net.foxdenstudio.sponge.foxcore.mod.cui.geometries.LineGeometry;
import net.foxdenstudio.sponge.foxcore.mod.cui.geometries.PreRender;
import net.foxdenstudio.sponge.foxcore.mod.cui.geometries.RectangleGeometry;
import net.foxdenstudio.sponge.foxcore.mod.render.Highlight;
import net.foxdenstudio.sponge.foxcore.mod.render.HighlightList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class WorldSystem {
    public static final PreRender PRE_RENDER = new PreRender() {
        @Override
        public void accept(Tessellator tessellator, VertexBuffer vertexBuffer, float partialTicks) {
            this.setColorR(1);
            this.setColorB(1);
            this.setColorG(0);
            this.setColorA(1);
        }
    };
    private final CUI cui;
    private final Tessellator tessellator;
    private final VertexBuffer vertexBuffer;
    private HighlightList list;

    public WorldSystem(@Nonnull final CUI cui, @Nonnull final Tessellator tessellator, @Nonnull final VertexBuffer vertexBuffer, Minecraft minecraft) {
        this.cui = cui;
        this.tessellator = tessellator;
        this.vertexBuffer = vertexBuffer;
        this.list = new HighlightList(Minecraft.getMinecraft());
//        this.list.add(new Highlight(new Vector3i(0, 80, 0), new Vector3f(1, 0, 0)));//new Highlight(new Vector3i(0, 80, 0), new Vector3f(1, 0, 0)));
    }

    public void render(float partialTicks) {
        final EntityPlayerSP player = Minecraft.getMinecraft().player;
        double playerX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
        double playerY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
        double playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;
        this.list.sortZ(playerX, playerY, playerZ);

        glPushMatrix();
//        glPushAttrib(GL_ENABLE_BIT);// | GL_COLOR_BUFFER_BIT | GL_CURRENT_BIT | GL_DEPTH_BUFFER_BIT);
//        glDisable(GL_TEXTURE_2D);
        glPushAttrib(GL_ENABLE_BIT | GL_COLOR_BUFFER_BIT | GL_CURRENT_BIT | GL_DEPTH_BUFFER_BIT);

        glDisable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_ALPHA_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDepthMask(false);

        Vector3i offset = new Vector3i(-playerX, -playerY, -playerZ);

        glTranslated(-playerX - offset.getX(), -playerY - offset.getY(), -playerZ - offset.getZ());
//        this.list.render(offset);
        PreRender PRE_RENDER = new PreRender() {
            @Override
            public void accept(Tessellator tessellator, VertexBuffer vertexBuffer, float partialTicks) {
                final float alpha = (1f - ((System.currentTimeMillis()) % 2000) / (float) 2000) % 1f;
                this.setColorA(alpha / 8 + 0.1f);
                this.setColorR(1);
                this.setColorB(1);
                this.setColorG(1);
            }
        };
        final RectangleGeometry[] faces = new RectangleGeometry[6];
        final LineGeometry[] outlines = new LineGeometry[12];
        final Vector3d point1 = Vector3d.from(-25, 1000, -20);
        final Vector3d point2 = Vector3d.from(-25, -100, -20);
        final Vector3d point3 = Vector3d.from(25, -100, -20);
        final Vector3d point4 = Vector3d.from(-25, -100, 20);
        final Vector3d point5 = Vector3d.from(25, 1000, 20);
        final Vector3d point6 = Vector3d.from(25, 1000, -20);
        final Vector3d point7 = Vector3d.from(-25, 1000, 20);
        final Vector3d point8 = Vector3d.from(25, -100, 20);

        faces[0] = new RectangleGeometry(point1, point2, point3, PRE_RENDER, null);
        faces[1] = new RectangleGeometry(point4, point2, point3, PRE_RENDER, null);
        faces[2] = new RectangleGeometry(point5, point6, point3, PRE_RENDER, null);
        faces[3] = new RectangleGeometry(point5, point7, point4, PRE_RENDER, null);
        faces[4] = new RectangleGeometry(point1, point7, point4, PRE_RENDER, null);
        faces[5] = new RectangleGeometry(point7, point1, point6, PRE_RENDER, null);

        PRE_RENDER = new PreRender() {
            @Override
            public void accept(Tessellator tessellator, VertexBuffer vertexBuffer, float partialTicks) {
                final float alpha = (1f - ((System.currentTimeMillis()) % 2000) / (float) 2000) % 1f;
                this.setColorA(alpha);
                this.setColorR(1);
                this.setColorG(1);
                this.setColorB(1);
            }
        };
        outlines[0] = new LineGeometry(point1, point2, PRE_RENDER, null);
        outlines[1] = new LineGeometry(point1, point6, PRE_RENDER, null);
        outlines[2] = new LineGeometry(point1, point7, PRE_RENDER, null);
        outlines[3] = new LineGeometry(point6, point3, PRE_RENDER, null);
        outlines[4] = new LineGeometry(point2, point3, PRE_RENDER, null);
        outlines[5] = new LineGeometry(point7, point5, PRE_RENDER, null);
        outlines[6] = new LineGeometry(point5, point6, PRE_RENDER, null);
        outlines[7] = new LineGeometry(point7, point4, PRE_RENDER, null);
        outlines[8] = new LineGeometry(point5, point8, PRE_RENDER, null);
        outlines[9] = new LineGeometry(point2, point4, PRE_RENDER, null);
        outlines[10] = new LineGeometry(point8, point4, PRE_RENDER, null);
        outlines[11] = new LineGeometry(point8, point3, PRE_RENDER, null);
        for (int i = 0; i < faces.length; i++) {
            if (faces[i] != null) {
                faces[i].render(this.tessellator, this.vertexBuffer, partialTicks, offset);
            }
        }
        for (int i = 0; i < outlines.length; i++) {
            if (outlines[i] != null) {
                outlines[i].render(this.tessellator, this.vertexBuffer, partialTicks, offset);
            }
        }

        glPopAttrib();
        glPopMatrix();
    }

    public void showRegionEnds(Integer lowerX, Integer upperX, Integer lowerZ, Integer upperZ) {

        final Vector3f color = new Vector3f(1, 0, 0);
        for (int i = -1000; i < 5000; i++) {
            this.list.add(new Highlight(new Vector3i(lowerX, i, lowerZ), color));
            this.list.add(new Highlight(new Vector3i(lowerX, i, upperZ), color));
            this.list.add(new Highlight(new Vector3i(upperX, i, lowerZ), color));
            this.list.add(new Highlight(new Vector3i(upperX, i, upperZ), color));
        }
    }

    public void hideRegionEnds() {
        this.list.clear();
    }

    public void showHiddenRegionEnds(Integer lowerX, Integer upperX, Integer lowerZ, Integer upperZ) {
        for (int i = -1000; i < 5000; i++) {
            this.list.add(new Highlight(new Vector3i(lowerX, i, lowerZ)));
            this.list.add(new Highlight(new Vector3i(lowerX, i, upperZ)));
            this.list.add(new Highlight(new Vector3i(upperX, i, lowerZ)));
            this.list.add(new Highlight(new Vector3i(upperX, i, upperZ)));
        }
    }
}
