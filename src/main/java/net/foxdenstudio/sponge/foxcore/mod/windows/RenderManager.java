package net.foxdenstudio.sponge.foxcore.mod.windows;

import net.foxdenstudio.sponge.foxcore.mod.FoxCoreClientMain;
import net.foxdenstudio.sponge.foxcore.mod.windows.examples.BasicWindow;
import net.foxdenstudio.sponge.foxcore.mod.windows.parts.IBasePart;
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
        Registry.getInstance().addWindow(
                new BasicWindow()
                        .setTitle("FoxCore Window Test")
                        .setPositionX(10)
                        .setPositionY(10)
                        .setHeight(200)
                        .setWidth(150)
                        .setPinned(true)
        );
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        glPushAttrib(GL_CURRENT_BIT | GL_ENABLE_BIT | GL_COLOR_BUFFER_BIT | GL_BLEND | GL_TEXTURE_2D | GL_LIGHTING);
        glPushMatrix();

        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_LIGHTING);

        if (FoxCoreClientMain.instance.getIsWindowControlActive()) {
            Registry.getInstance().getParts().values().forEach(this::protectedRender);
        } else {
            Registry.getInstance().getParts().values().stream().filter(IBasePart::isPinned).forEach(this::protectedRender);
        }

        glPopMatrix();
        glPopAttrib();
    }

    private void protectedRender(@Nonnull final IBasePart iBasePart) {

//        final Framebuffer framebuffer = new Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, false);
//        framebuffer.bindFramebuffer(true);
        glTranslated(iBasePart.getPositionX(), iBasePart.getPositionY(), 0);

//        glColor3f(0, 0, 1);
//        this.vertexBuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
//        this.vertexBuffer.pos(iBasePart.getPositionX(), iBasePart.getPositionY(), 0.0D).endVertex();
//        this.vertexBuffer.pos(iBasePart.getPositionX(), iBasePart.getPositionY() + iBasePart.getHeight(), 0.0D).endVertex();
//        this.vertexBuffer.pos(iBasePart.getPositionX() + iBasePart.getWidth(), iBasePart.getPositionY() + iBasePart.getHeight(), 0.0D).endVertex();
//        this.vertexBuffer.pos(iBasePart.getPositionX() + iBasePart.getWidth(), iBasePart.getPositionY(), 0.0D).endVertex();
//        this.tessellator.draw();

        startGlScissor(iBasePart.getPositionX(), iBasePart.getPositionY(), iBasePart.getWidth(), iBasePart.getHeight());
        iBasePart.render();
        endGlScissor();

//        framebuffer.framebufferRender(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
//        framebuffer.unbindFramebuffer();
//        framebuffer.deleteFramebuffer();
//        final Framebuffer framebuffer = Minecraft.getMinecraft().getFramebuffer();
//
//        framebuffer.bindFramebuffer(true);
//        framebuffer.setFramebufferColor(0, 0, 1, 1);
//        glColor3f(1, 0, 0);
//        FoxCoreClientMain.logger.debug(iBasePart.getPositionX() + " | " + iBasePart.getPositionY() + " | " + iBasePart.getHeight() + " | " + iBasePart.getWidth());
//        glEnable(GL_STENCIL_TEST);
//
//        glStencilFunc(GL_ALWAYS, 1, 1);
//        glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);
//        this.vertexBuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
//        this.vertexBuffer.pos(iBasePart.getPositionX(), iBasePart.getPositionY(), 0.0D).endVertex();
//        this.vertexBuffer.pos(iBasePart.getPositionX(), iBasePart.getPositionY() + iBasePart.getHeight(), 0.0D).endVertex();
//        this.vertexBuffer.pos(iBasePart.getPositionX() + iBasePart.getWidth(), iBasePart.getPositionY() + iBasePart.getHeight(), 0.0D).endVertex();
//        this.vertexBuffer.pos(iBasePart.getPositionX() + iBasePart.getWidth(), iBasePart.getPositionY(), 0.0D).endVertex();
//        this.tessellator.draw();

//        glColor3f(0, 1, 0);
//        glStencilFunc(GL_EQUAL, 1, 1);
//        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
//        iBasePart.render();
//        glDisable(GL_STENCIL_TEST);
//        framebuffer.unbindFramebuffer();
//        framebuffer.framebufferRender(iBasePart.getWidth(), iBasePart.getHeight());
    }

    public void startGlScissor(int x, int y, int width, int height) {
        final Minecraft mc = Minecraft.getMinecraft();

        ScaledResolution reso = new ScaledResolution(mc);

        double scaleW = (double) mc.displayWidth / reso.getScaledWidth_double();
        double scaleH = (double) mc.displayHeight / reso.getScaledHeight_double();

        if (width <= 0 || height <= 0) {
            return;
        }
        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }

        glEnable(GL_SCISSOR_TEST);

        glScissor((int) Math.floor((double) x * scaleW), (int) Math.floor((double) mc.displayHeight - ((double) (y + height) * scaleH)), (int) Math.floor((double) (x + width) * scaleW) - (int) Math.floor((double) x * scaleW), (int) Math.floor((double) mc.displayHeight - ((double) y * scaleH)) - (int) Math.floor((double) mc.displayHeight - ((double) (y + height) * scaleH))); //starts from lower left corner (minecraft starts from upper left)
    }

    public void endGlScissor() {
        glDisable(GL_SCISSOR_TEST);
    }
}
