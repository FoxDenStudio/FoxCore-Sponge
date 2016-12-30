package net.foxdenstudio.sponge.foxcore.mod.rendernew.windows.components;


import net.foxdenstudio.sponge.foxcore.mod.rendernew.windows.WindowRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

import static org.lwjgl.opengl.GL11.*;

public abstract class Window {

    final FontRenderer fontRendererObj;
    @Nonnull
    private final WindowRenderer windowRenderer;
    private final Tessellator tessellator;
    private final VertexBuffer vertexbuffer;
    private boolean shouldRenderHead = true;
    private boolean shouldRenderFrame = true;
    private int posX;
    private int posY;
    private int width;
    private int height;
    private boolean pinned;
    private String title;


    public Window(@Nonnull WindowRenderer windowRenderer) {
        this.windowRenderer = windowRenderer;
        this.tessellator = Tessellator.getInstance();
        this.vertexbuffer = this.tessellator.getBuffer();
        this.fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
    }

    public void renderHead() {
        if (this.shouldRenderHead) {
            //BEGIN BACKGROUND OF HEAD
            glColor3f(.15f, .15f, .15f);
            this.vertexbuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
            this.vertexbuffer.pos(this.posX, this.posY, 0.0D).endVertex();
            this.vertexbuffer.pos(this.posX, this.posY + 15, 0.0D).endVertex();
            this.vertexbuffer.pos(this.posX + this.width, this.posY + 15, 0.0D).endVertex();
            this.vertexbuffer.pos(this.posX + this.width, this.posY, 0.0D).endVertex();
            this.tessellator.draw();
            //END BACKGROUND OF HEAD

            //BEGIN RENDER OF CONTROL BUTTONS
            int buttonSize = 13;
            int offset = 0;
            // BEGIN RENDER OF 'X'
            glColor3f(.7f, 0, 0);
            this.vertexbuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
            this.vertexbuffer.pos(this.posX + this.width - buttonSize, this.posY, 0.0D).endVertex();
            this.vertexbuffer.pos(this.posX + this.width - buttonSize, this.posY + 13, 0.0D).endVertex();
            this.vertexbuffer.pos(this.posX + this.width, this.posY + 13, 0.0D).endVertex();
            this.vertexbuffer.pos(this.posX + this.width, this.posY, 0.0D).endVertex();
            this.tessellator.draw();
            // END RENDER OF 'X'
            offset += buttonSize + 1;
            // BEGIN RENDER OF '>V'
            glColor4f(0f, 0, 0, .1f);
            this.vertexbuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
            this.vertexbuffer.pos(this.posX + this.width - buttonSize - offset, this.posY, 0.0D).endVertex();
            this.vertexbuffer.pos(this.posX + this.width - buttonSize - offset, this.posY + 13, 0.0D).endVertex();
            this.vertexbuffer.pos(this.posX + this.width - offset, this.posY + 13, 0.0D).endVertex();
            this.vertexbuffer.pos(this.posX + this.width - offset, this.posY, 0.0D).endVertex();
            this.tessellator.draw();
            // END RENDER OF '>V'
            //END RENDER OF CONTROL BUTTONS

            //BEGIN RENDER TEXT
            glDisable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
            glColor3f(1, 1, 1);
            final boolean old = this.fontRendererObj.getUnicodeFlag();
            this.fontRendererObj.setUnicodeFlag(true);
            // BEGIN RENDER TITLE
            this.fontRendererObj.drawStringWithShadow(this.title, ((float) this.posX) + 2, ((float) this.posY) + 2, -1);
            // END RENDER TITLE
            // BEGIN RENDER CONTROL BUTTONS
            offset = 0;
            this.fontRendererObj.drawStringWithShadow("X", (float) ((((this.posX + this.width) - buttonSize - offset) + (buttonSize / 2)) - (this.fontRendererObj.getStringWidth("X") / 2)), (float) this.posY + 2, -1);
            offset = buttonSize + 1;
            this.fontRendererObj.drawStringWithShadow(this.pinned ? "V" : ">", (float) ((((this.posX + this.width) - buttonSize - offset) + (buttonSize / 2)) - (this.fontRendererObj.getStringWidth(this.pinned ? "V" : ">") / 2)), (float) this.posY + 2, -1);
            // END RENDER CONTROL BUTTONS
            this.fontRendererObj.setUnicodeFlag(old);
            glEnable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
            //END RENDER TEXT
        }
    }

    public void renderFrame() {
        if (this.shouldRenderFrame) {
            int offset = 2;

            glColor3f(.15f, .15f, .15f);
            this.vertexbuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
            this.vertexbuffer.pos(this.posX - offset, this.posY - offset, 0.0D).endVertex();
            this.vertexbuffer.pos(this.posX - offset, this.posY + this.height + offset, 0.0D).endVertex();
            this.vertexbuffer.pos(this.posX + this.width + offset, this.posY + this.height + offset, 0.0D).endVertex();
            this.vertexbuffer.pos(this.posX + this.width + offset, this.posY - offset, 0.0D).endVertex();
            this.tessellator.draw();
        }
    }

    public void renderBackground() {
        glColor3f(.25f, .25f, .25f);
        this.vertexbuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
        this.vertexbuffer.pos(this.posX, this.posY, 0.0D).endVertex();
        this.vertexbuffer.pos(this.posX, this.posY + this.height, 0.0D).endVertex();
        this.vertexbuffer.pos(this.posX + this.width, this.posY + this.height, 0.0D).endVertex();
        this.vertexbuffer.pos(this.posX + this.width, this.posY, 0.0D).endVertex();
        this.tessellator.draw();
    }

    public void renderBody() {
        //BEGIN RENDER TEXT
        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glColor3f(1, 1, 1);
        final boolean old = this.fontRendererObj.getUnicodeFlag();
        this.fontRendererObj.setUnicodeFlag(true);
        // BEGIN RENDER
        final BlockPos playerLocation = Minecraft.getMinecraft().thePlayer.getPosition();
        this.fontRendererObj.drawStringWithShadow("Player X: " + playerLocation.getX(), ((float) this.posX) + 3, ((float) this.posY) + this.height - 30, -1);
        this.fontRendererObj.drawStringWithShadow("Player Y: " + playerLocation.getY(), ((float) this.posX) + 3, ((float) this.posY) + this.height - 20, -1);
        this.fontRendererObj.drawStringWithShadow("Player Z: " + playerLocation.getZ(), ((float) this.posX) + 3, ((float) this.posY) + this.height - 10, -1);
        // END RENDER
        this.fontRendererObj.setUnicodeFlag(old);
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        //END RENDER TEXT

    }

    public void render() {
        glPushMatrix();
        renderFrame();
        glPopMatrix();
        glPushMatrix();
        renderBackground();
        glPopMatrix();
        glPushMatrix();
        renderHead();
        glPopMatrix();
        glPushMatrix();
        renderBody();
        glPopMatrix();
    }

    public void setPosition(final int posX, final int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public boolean isPinned() {
        return this.pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public int getPosX() {
        return this.posX;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getPosY() {
        return this.posY;
    }

    @Override
    public String toString() {
        return "Window{" +
                "shouldRenderHead=" + this.shouldRenderHead +
                ", shouldRenderFrame=" + this.shouldRenderFrame +
                ", posX=" + this.posX +
                ", posY=" + this.posY +
                ", width=" + this.width +
                ", height=" + this.height +
                ", pinned=" + this.pinned +
                '}';
    }

    public void click(int x, int y) {
        if (this.shouldRenderHead) {
            int bW = 15;
            int bH = 15;
            int bX = this.posX + this.width - bW;
            int bY = this.posY;
            boolean gX = x >= bX;
            boolean lX = x <= bX + bW;
            boolean gY = y >= bY;
            boolean lY = y <= bY + bH;
            System.out.println("B1: " + gX + " | " + lX + " | " + gY + " | " + lY);
            if (gX && lX && gY && lY) {
                closeWindow();
                return;
            }
            bX -= bW;
            gX = x >= bX;
            lX = x <= bX + bW;
            gY = y >= bY;
            lY = y <= bY + bH;
            System.out.println("B2: " + gX + " | " + lX + " | " + gY + " | " + lY);
            if (gX && lX && gY && lY) {
                this.pinned = !this.pinned;
                return;
            }
        }
    }

    private void closeWindow() {
        this.windowRenderer.remove(this);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void drag(int x, int y) {
        setPosition(x - 5, y - 5);
    }
}
