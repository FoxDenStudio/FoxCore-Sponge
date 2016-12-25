package net.foxdenstudio.sponge.foxcore.mod.rendernew.windows;

import net.minecraft.client.gui.GuiScreen;

import static org.lwjgl.opengl.GL11.*;

public class WindowRenderer extends GuiScreen {
    @Override
    public void initGui() {
        super.initGui();
        System.out.println("Initied");
    }

    @Override
    public void updateScreen() {
//        System.out.println("Update");

//        glColor3f(0, 1, 0);
//        glBegin(GL_QUADS);
//        glVertex2f(0, 0);
//        glVertex2f(100, 0);
//        glVertex2f(100, 100);
//        glVertex2f(0, 100);
//        glEnd();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        super.drawGradientRect(0, 0, 0, 0, 0x00ff00ff, 0x0000ffff);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    /*
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Post event) {
//        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        GlStateManager.pushAttrib();
        GlStateManager.color(1.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();

        glBegin(GL_QUADS);
        glVertex2d(0, 0);
        glVertex2d(0, 50);
        glVertex2d(50, 50);
        glVertex2d(50, 0);
        glEnd();

        GlStateManager.popAttrib();
    }
*/

}
