package net.foxdenstudio.sponge.foxcore.mod.rendernew.world;

import com.flowpowered.math.vector.Vector3i;
import net.foxdenstudio.sponge.foxcore.mod.rendernew.world.geometry.Geometry;
import net.foxdenstudio.sponge.foxcore.mod.rendernew.world.renderable.Highlight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Fox on 12/4/2016.
 */
public class RenderManager {

    private static RenderManager inst;

    private Minecraft mc;
    private RenderList list;

    private RenderManager(Minecraft mc) {
        this.mc = mc;
        this.list = new RenderList();

        list.addAll(new Highlight(new Vector3i(0, 80, 0)).getGeometry());
    }

    public static RenderManager instance() {
        if (inst == null) {
            inst = new RenderManager(Minecraft.getMinecraft());
        }
        return inst;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {

        //System.out.println("TEST");
        EntityPlayerSP player = mc.thePlayer;
        float partialTicks = event.getPartialTicks();
        double playerX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
        double playerY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
        double playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;
        list.sortZ(playerX, playerY, playerZ);

        glPushAttrib(GL_ENABLE_BIT | GL_COLOR_BUFFER_BIT | GL_CURRENT_BIT | GL_DEPTH_BUFFER_BIT);

        glDisable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_ALPHA_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDepthMask(false);
        //glDisable(GL_DEPTH_TEST);

        glLineWidth(2f);

        for (Geometry geo : list) {
            glPushMatrix();
            glTranslated(geo.position.getX(), geo.position.getY(), geo.position.getZ());
            geo.render();
            glPopMatrix();
        }

        glPopAttrib();
    }
}
