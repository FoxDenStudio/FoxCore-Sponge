package net.foxdenstudio.sponge.foxcore.mod.windows;

import net.foxdenstudio.sponge.foxcore.mod.FoxCoreClientMain;
import net.foxdenstudio.sponge.foxcore.mod.windows.examples.BasicWindow;
import net.foxdenstudio.sponge.foxcore.mod.windows.parts.BasePart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import static org.lwjgl.opengl.GL11.*;

public class RenderManager {

    public RenderManager() {
        Registry.getInstance().addParts(
                new BasicWindow()
                        .setTitle("FoxCore Window Test")
                        .setPositionX(10)
                        .setPositionY(10)
                        .setHeight(200)
                        .setWidth(150)
                        .setPinned(true)
                        .revalidate()
        );
    }

    public static void startGlScissor(int x, int y, int width, int height) {
        final Minecraft mc = Minecraft.getMinecraft();
        final ScaledResolution reso = new ScaledResolution(mc);
        final double scaleW = (double) mc.displayWidth / reso.getScaledWidth_double();
        final double scaleH = (double) mc.displayHeight / reso.getScaledHeight_double();

        if (width <= 0 || height <= 0) return;
        if (x < 0) x = 0;
        if (y < 0) y = 0;

        glEnable(GL_SCISSOR_TEST);
        glScissor((int) Math.floor((double) x * scaleW), (int) Math.floor((double) mc.displayHeight - ((double) (y + height) * scaleH)), (int) Math.floor((double) (x + width) * scaleW) - (int) Math.floor((double) x * scaleW), (int) Math.floor((double) mc.displayHeight - ((double) y * scaleH)) - (int) Math.floor((double) mc.displayHeight - ((double) (y + height) * scaleH))); //starts from lower left corner (minecraft starts from upper left)
    }

    public static void endGlScissor() {
        glDisable(GL_SCISSOR_TEST);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        glPushAttrib(GL_CURRENT_BIT | GL_ENABLE_BIT | GL_COLOR_BUFFER_BIT);
        glPushMatrix();

        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        if (FoxCoreClientMain.instance.getIsWindowControlActive()) {
            Registry.getInstance().getPartList().reverseStream().forEachOrdered(this::protectedRender);
        } else {
            Registry.getInstance().getPartList().reverseStream().filter(BasePart::isPinned).forEachOrdered(this::protectedRender);
        }

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glPopMatrix();
        glPopAttrib();
    }

    private void protectedRender(@Nonnull final BasePart basePart) {
        glPushMatrix();
        glTranslated(basePart.getPositionX(), basePart.getPositionY(), 0);
        startGlScissor(basePart.getPositionX(), basePart.getPositionY(), basePart.getWidth(), basePart.getHeight());
        basePart.render();
        endGlScissor();
        glPopMatrix();
    }
}
