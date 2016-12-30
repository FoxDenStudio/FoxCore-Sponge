package net.foxdenstudio.sponge.foxcore.mod.windows.examples;

import net.foxdenstudio.sponge.foxcore.mod.windows.parts.WindowPart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import javax.annotation.Nonnull;

import static org.lwjgl.opengl.GL11.*;

public class BasicWindow extends WindowPart {

    private final float[] frameColor = {.15f, .15f, .15f};
    private final float[] backgroundColor = {.25f, .25f, .25f};
    private final FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
    private int headSize = 15;
    private int frameWidth = 2;

    void renderFrame() {
        glColor3f(.15f, .15f, .15f);
        this.vertexBuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
        this.vertexBuffer.pos(0, 0, 0.0D).endVertex();
        this.vertexBuffer.pos(0, getHeight(), 0.0D).endVertex();
        this.vertexBuffer.pos(getWidth(), getHeight(), 0.0D).endVertex();
        this.vertexBuffer.pos(getWidth(), 0, 0.0D).endVertex();
        this.tessellator.draw();
    }

    void renderHead() {
        glColor3f(.15f, .15f, .15f);
        this.vertexBuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
        this.vertexBuffer.pos(0, 0, 0.0D).endVertex();
        this.vertexBuffer.pos(0, this.headSize, 0.0D).endVertex();
        this.vertexBuffer.pos(getWidth(), this.headSize, 0.0D).endVertex();
        this.vertexBuffer.pos(getWidth(), 0, 0.0D).endVertex();
        this.tessellator.draw();

        final int buttonSize = 13;

        glColor3f(.7f, 0, 0);
        this.vertexBuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
        this.vertexBuffer.pos(getWidth() - buttonSize - this.frameWidth, (this.headSize - buttonSize) / 2, 0.0D).endVertex();
        this.vertexBuffer.pos(getWidth() - buttonSize - this.frameWidth, (this.headSize - buttonSize) / 2 + buttonSize, 0.0D).endVertex();
        this.vertexBuffer.pos(getWidth() - this.frameWidth, (this.headSize - buttonSize) / 2 + buttonSize, 0.0D).endVertex();
        this.vertexBuffer.pos(getWidth() - this.frameWidth, (this.headSize - buttonSize) / 2, 0.0D).endVertex();
        this.tessellator.draw();


        // BEGIN RENDER TITLE
        renderText(this.title, this.frameWidth + 1, (this.headSize - buttonSize) / 2 + 2);
        // END RENDER TITLE
        // BEGIN RENDER CONTROL BUTTONS
        final int buttonXBaseOffset = getWidth() - buttonSize / 2;
        renderText("X", (buttonXBaseOffset - (this.fontRendererObj.getStringWidth("X") / 2)) - this.frameWidth, (this.headSize - buttonSize) / 2 + 2);
        renderText(isPinned() ? "V" : ">", (((buttonXBaseOffset - buttonSize)) - (this.fontRendererObj.getStringWidth(isPinned() ? "V" : ">") / 2)) - this.frameWidth, (this.headSize - buttonSize) / 2 + 2);
        // END RENDER CONTROL BUTTONS
    }

    void renderBody() {
        glColor3f(.25f, .25f, .25f);
        this.vertexBuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
        this.vertexBuffer.pos(0, 0, 0.0D).endVertex();
        this.vertexBuffer.pos(0, getHeight() - this.headSize - this.frameWidth, 0.0D).endVertex();
        this.vertexBuffer.pos(getWidth() - this.frameWidth * 2, getHeight() - this.headSize - this.frameWidth, 0.0D).endVertex();
        this.vertexBuffer.pos(getWidth() - this.frameWidth * 2, 0, 0.0D).endVertex();
        this.tessellator.draw();
    }

    @Override
    public void render() {
        renderFrame();
        renderHead();
        glTranslated(this.frameWidth, this.headSize, 0);
        renderBody();
        super.render();
    }

    private void renderText(@Nonnull final String text, float x, float y) {
        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glColor3f(1, 1, 1);
        final boolean old = this.fontRendererObj.getUnicodeFlag();
        this.fontRendererObj.setUnicodeFlag(true);
        this.fontRendererObj.drawStringWithShadow(text, x, y, -1);
        this.fontRendererObj.setUnicodeFlag(old);
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
    }

    public float[] getFrameColor() {
        return this.frameColor;
    }

    public float[] getBackgroundColor() {
        return this.backgroundColor;
    }

    public int getHeadSize() {
        return this.headSize;
    }

    public BasicWindow setHeadSize(int headSize) {
        this.headSize = headSize;
        return this;
    }

    public int getFrameWidth() {
        return this.frameWidth;
    }

    public BasicWindow setFrameWidth(int frameWidth) {
        this.frameWidth = frameWidth;
        return this;
    }
}
