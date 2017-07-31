package net.foxdenstudio.sponge.foxcore.mod.cui.windows;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;

public abstract class Component {
    public abstract void render(Tessellator tessellator, VertexBuffer vertexBuffer);

    public abstract double getPositionX();

    public abstract double getPositionY();

    public abstract double getWidth();

    public abstract double getHeight();

    public boolean mouseClicked(int x, int y, int mouseButton) {
        return false;
    }

    public boolean mouseReleased(int x, int y, int mouseButton) {
        return false;
    }

    public abstract String toString();
}
