package net.foxdenstudio.sponge.foxcore.mod.windows.examples;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector4f;
import net.foxdenstudio.sponge.foxcore.mod.windows.Registry;
import net.foxdenstudio.sponge.foxcore.mod.windows.components.Button;
import net.foxdenstudio.sponge.foxcore.mod.windows.geometries.GeometrySquareButton;
import net.foxdenstudio.sponge.foxcore.mod.windows.parts.BasePart;
import net.foxdenstudio.sponge.foxcore.mod.windows.parts.WindowPart;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import javax.annotation.Nonnull;

import static org.lwjgl.opengl.GL11.*;

public class BasicWindow extends WindowPart {

    private final int headButtonSize = 13;
    private final Vector4f frameColor = new Vector4f(.15f, .15f, .15f, 1);
    private final Vector4f backgroundColor = new Vector4f(.25f, .25f, .25f, 1);
    private final Button button = new Button("Submit!");
    private int headSize = 15;
    private int frameWidth = 2;
    private GeometrySquareButton closeButton, pinButton;
    private int lastClickLocationX;
    private int lastClickLocationY;
    private boolean currentlyDraggable = false;

    public BasicWindow() {
        this.button.setPositionX(10);
        this.button.setPositionY(10);
        this.button.addClickHandler(0, (vector2i, integer) -> {
            System.out.println(vector2i + " | " + integer);
        });
        this.button.revalidate();

        layoutHeadButtons();
    }

    private void layoutHeadButtons() {
        final int startY = (this.headSize - this.headButtonSize) / 2;
        this.closeButton = new GeometrySquareButton(
                new Vector3d(getWidth() - this.headButtonSize - this.frameWidth, startY, 0),
                new Vector3d(getWidth() - this.frameWidth, startY + this.headButtonSize, 0),
                new Vector4f(.8f, 0, 0, 1),
                new Vector4f(1, 1, 1, 1),
                () -> "X"
        );
        this.pinButton = new GeometrySquareButton(
                new Vector3d(getWidth() - this.headButtonSize - this.headButtonSize - 1 - this.frameWidth, startY, 0),
                new Vector3d(getWidth() - this.headButtonSize - 1 - this.frameWidth, startY + this.headButtonSize, 0),
                new Vector4f(0f, 0, 0, .25f),
                new Vector4f(1, 1, 1, 1),
                () -> isPinned() ? "V" : ">"
        );
    }

    void renderFrame() {
        glColor3f(.15f, .15f, .15f);
        vertexBuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
        vertexBuffer.pos(0, 0, 0.0D).endVertex();
        vertexBuffer.pos(0, getHeight(), 0.0D).endVertex();
        vertexBuffer.pos(getWidth(), getHeight(), 0.0D).endVertex();
        vertexBuffer.pos(getWidth(), 0, 0.0D).endVertex();
        tessellator.draw();
    }

    void renderHead() {
        glColor3f(.15f, .15f, .15f);
        vertexBuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
        vertexBuffer.pos(0, 0, 0.0D).endVertex();
        vertexBuffer.pos(0, this.headSize, 0.0D).endVertex();
        vertexBuffer.pos(getWidth(), this.headSize, 0.0D).endVertex();
        vertexBuffer.pos(getWidth(), 0, 0.0D).endVertex();
        tessellator.draw();

        this.closeButton.render();
        this.pinButton.render();

        renderText(this.title, this.frameWidth + 1, (this.headSize - this.headButtonSize) / 2 + 2, new Vector4f(1, 1, 1, 1));
    }

    void renderBody() {
        glColor3f(.25f, .25f, .25f);
        vertexBuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
        vertexBuffer.pos(0, 0, 0.0D).endVertex();
        vertexBuffer.pos(0, getHeight() - this.headSize - this.frameWidth, 0.0D).endVertex();
        vertexBuffer.pos(getWidth() - this.frameWidth * 2, getHeight() - this.headSize - this.frameWidth, 0.0D).endVertex();
        vertexBuffer.pos(getWidth() - this.frameWidth * 2, 0, 0.0D).endVertex();
        tessellator.draw();


        this.button.render();

    }

    @Override
    public void render() {
        renderFrame();
        renderHead();
        glTranslated(this.frameWidth, this.headSize, 0);
        renderBody();
        super.render();
    }


    public Vector4f getFrameColor() {
        return this.frameColor;
    }

    public Vector4f getBackgroundColor() {
        return this.backgroundColor;
    }

    public int getHeadSize() {
        return this.headSize;
    }

    @Nonnull
    public BasicWindow setHeadSize(int headSize) {
        this.headSize = headSize;
        layoutHeadButtons();
        return this;
    }

    public int getFrameWidth() {
        return this.frameWidth;
    }

    @Nonnull
    public BasicWindow setFrameWidth(int frameWidth) {
        this.frameWidth = frameWidth;
        return this;
    }

    @Override
    public BasePart setWidth(int width) {
        layoutHeadButtons();
        return super.setWidth(width);
    }

    @Override
    public void mouseClicked(int x, int y, int buttonCode) {
        this.lastClickLocationX = x;
        this.lastClickLocationY = y;
        if (buttonCode == 0) {
            final boolean inHead = x >= 0 && x <= this.width && y >= 0 && y <= this.headSize;
            this.currentlyDraggable = inHead && !(this.closeButton.pointLiesWithin(x, y, 0) || this.pinButton.pointLiesWithin(x, y, 0));
        }
    }

    @Override
    public void mouseReleased(int x, int y, int buttonCode) {
        if (buttonCode == 0) {
            if (!this.currentlyDraggable) {
                if (this.closeButton.pointLiesWithin(x, y, 0)) {
                    this.close();
                } else if (this.pinButton.pointLiesWithin(x, y, 0)) {
                    this.setPinned(!this.isPinned());
                }
                y -= this.headSize;
                this.button.tryClick(x, y, buttonCode);
            }
            this.currentlyDraggable = false;
        }
        this.lastClickLocationX = -1;
        this.lastClickLocationY = -1;
    }

    @Override
    public void mouseDrag(int x, int y, int buttonCode) {
        if (buttonCode == 0) {
            if (this.currentlyDraggable) {
                this.positionX += x - this.lastClickLocationX;
                this.positionY += y - this.lastClickLocationY;
            }
        }
    }

    @Override
    public BasePart revalidate() {
        layoutHeadButtons();
        return this;
    }

    private void close() {
        System.out.println("Closing window: " + this.toString());
        Registry.getInstance().removePart(this);
    }


    @Override
    public String toString() {
        return "BasicWindow{" +
                "headButtonSize=" + this.headButtonSize +
                ", frameColor=" + this.frameColor +
                ", backgroundColor=" + this.backgroundColor +
                ", button=" + this.button +
                ", headSize=" + this.headSize +
                ", frameWidth=" + this.frameWidth +
                ", closeButton=" + this.closeButton +
                ", pinButton=" + this.pinButton +
                ", lastClickLocationX=" + this.lastClickLocationX +
                ", lastClickLocationY=" + this.lastClickLocationY +
                ", currentlyDraggable=" + this.currentlyDraggable +
                '}';
    }
}
