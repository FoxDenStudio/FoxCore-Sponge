package net.foxdenstudio.sponge.foxcore.mod.windows.parts;

import com.flowpowered.math.vector.Vector4f;
import net.foxdenstudio.sponge.foxcore.mod.windows.Dependency;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.lwjgl.opengl.GL11.*;

public abstract class BasePart {
    protected static final Tessellator tessellator = Tessellator.getInstance();
    protected static final VertexBuffer vertexBuffer = tessellator.getBuffer();
    protected static final FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
    protected int height;
    protected int positionX;
    protected int positionY;
    protected int width;
    protected boolean pinned;

    public static void renderText(@Nonnull final String text, float x, float y, Vector4f color) {
        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glColor4f(color.getX(), color.getY(), color.getZ(), color.getW());
        final boolean old = fontRendererObj.getUnicodeFlag();
        fontRendererObj.setUnicodeFlag(true);
        fontRendererObj.drawStringWithShadow(text, x, y, ((((int) (255 * color.getX()))) << 24) + ((((int) (255 * color.getY()))) << 16) + ((((int) (255 * color.getZ()))) << 8) + (((int) (255 * color.getW()))));
        fontRendererObj.setUnicodeFlag(old);
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
    }

    public static int textWidth(@Nonnull final String text) {
        return fontRendererObj.getStringWidth(text);
    }

    @Nullable
    public Dependency getDependencies() {
        return new Dependency();
    }

    public abstract void render();

    public int getPositionX() {
        return this.positionX;
    }

    public BasePart setPositionX(int positionX) {
        this.positionX = positionX;
        return this;
    }

    public int getPositionY() {
        return this.positionY;
    }

    public BasePart setPositionY(int positionY) {
        this.positionY = positionY;
        return this;
    }

    public int getWidth() {
        return this.width;
    }

    public BasePart setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return this.height;
    }

    public BasePart setHeight(int height) {
        this.height = height;
        return this;
    }

    public boolean isPinned() {
        return this.pinned;
    }

    public BasePart setPinned(boolean pinned) {
        this.pinned = pinned;
        return this;
    }

    public abstract void mouseClicked(int x, int y, int buttonCode);

    public abstract void mouseReleased(int x, int y, int buttonCode);

    public abstract void mouseDrag(int x, int y, int buttonCode);

    public abstract BasePart revalidate();
}
