package net.foxdenstudio.sponge.foxcore.mod.cui.windows.builtin;

import net.foxdenstudio.sponge.foxcore.mod.cui.windows.Component;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_QUADS;

public class LogTextArea extends Component {

    private final ArrayList<Component> components;
    private double positionX;
    private double positionY;
    private double width;
    private double height;

    private int scrolledPosition = 0;
    private boolean autoScroll = true;

    public LogTextArea() {
        this.components = new ArrayList<>();
    }

    @Override
    public void render(Tessellator tessellator, VertexBuffer vertexBuffer) {
        boolean shouldRenderScrollbarY = false, shouldRenderScrollbarX = false;
        if (this.components.stream().mapToDouble(Component::getHeight).sum() > this.height) {
            shouldRenderScrollbarY = true;
        }
        if (this.components.stream().anyMatch(c -> c.getWidth() > this.width)) {
            shouldRenderScrollbarX = true;
        }

        //TODO Render Components at scrolled position

        shouldRenderScrollbarY = true;
        if (shouldRenderScrollbarY) {
            final float frameColorR = 1;
            final float frameColorG = 0;
            final float frameColorB = 0;
            final float frameColorA = 1;

            // Frame
            vertexBuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            vertexBuffer.pos(this.width - 10, 0, 0).color(frameColorR, frameColorG, frameColorB, frameColorA).endVertex();
            vertexBuffer.pos(this.width - 10, this.height, 0).color(frameColorR, frameColorG, frameColorB, frameColorA).endVertex();
            vertexBuffer.pos(this.width, this.height, 0).color(frameColorR, frameColorG, frameColorB, frameColorA).endVertex();
            vertexBuffer.pos(this.width, 0, 0).color(frameColorR, frameColorG, frameColorB, frameColorA).endVertex();
            tessellator.draw();
        }
    }

    @Override
    public double getPositionX() {
        return this.positionX;
    }

    public LogTextArea setPositionX(double positionX) {
        this.positionX = positionX;
        return this;
    }

    @Override
    public double getPositionY() {
        return this.positionY;
    }

    public LogTextArea setPositionY(double positionY) {
        this.positionY = positionY;
        return this;
    }

    @Override
    public double getWidth() {
        return this.width;
    }

    public LogTextArea setWidth(double width) {
        this.width = width;
        return this;
    }

    @Override
    public double getHeight() {
        return this.height;
    }

    public LogTextArea setHeight(double height) {
        this.height = height;
        return this;
    }

    @Override
    public String toString() {
        return "LogTextArea{" +
                "positionX=" + this.positionX +
                ", positionY=" + this.positionY +
                ", width=" + this.width +
                ", height=" + this.height +
                '}';
    }
}
