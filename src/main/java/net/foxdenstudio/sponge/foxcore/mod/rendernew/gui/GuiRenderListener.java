package net.foxdenstudio.sponge.foxcore.mod.rendernew.gui;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Fox on 12/20/2016.
 */
public class GuiRenderListener {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        glPushAttrib(GL_CURRENT_BIT | GL_ENABLE_BIT | GL_COLOR_BUFFER_BIT);
        glPushMatrix();

        glColor3f(1, 1, 1);


        glBegin(GL_QUADS);

        glTexCoord2d(0, 0);
        glVertex2d(0, 0);

        glTexCoord2d(0, 1);
        glVertex2d(0, 500);

        glTexCoord2d(1, 1);
        glVertex2d(500, 500);

        glTexCoord2d(1, 0);
        glVertex2d(500, 0);

        glEnd();

        glPopMatrix();
        glPopAttrib();
    }


}
