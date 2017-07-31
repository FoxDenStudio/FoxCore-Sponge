package net.foxdenstudio.sponge.foxcore.mod.cui.windows;

import com.flowpowered.math.vector.Vector4f;
import net.foxdenstudio.sponge.foxcore.mod.cui.CUI;
import net.foxdenstudio.sponge.foxcore.mod.cui.RenderUtils;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.Point;

import javax.annotation.Nonnull;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class Window {
    private final double headSize = 15;
    private final double frameWidth = 2;
    private final Vector4f frameColor = new Vector4f(.15f, .15f, .15f, 1);
    private final Vector4f bodyColor = new Vector4f(.25f, .25f, .25f, 1);
    private final ArrayList<Component> components;
    private int positionX;
    private int positionY;
    private String title;
    private int decorationType;
    private int width;
    private int height;
    private boolean visible;
    private Point lastClickLocation;
    private boolean currentlyDraggable;
    private boolean pinned;

    public Window() {
        this("");
    }

    public Window(@Nonnull final String title) {
        this.title = title;
        this.decorationType = WindowDecorationType.ALL.value;
        this.components = new ArrayList<>();
//        final LabelComponent lC = new LabelComponent("Hello Foxy!").setPositionX(6).setPositionY(5);
//        this.addComponent(lC);
//        this.addComponent(new ButtonComponent("Yiff!").setPositionX(65).setPositionY(5).setClickHandler(i -> lC.setText("Yiff clicked!")));
    }

    public void addComponent(@Nonnull final Component component) {
        this.components.add(component);
    }

    public void render(@Nonnull final CUI cui) {
        final Tessellator tessellator = cui.getTessellator();
        final VertexBuffer vertexBuffer = cui.getVertexBuffer();

        final float frameColorR = this.frameColor.getX();
        final float frameColorG = this.frameColor.getY();
        final float frameColorB = this.frameColor.getZ();
        final float frameColorA = this.frameColor.getW();
        // Frame
        vertexBuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        vertexBuffer.pos(0, 0, 0).color(frameColorR, frameColorG, frameColorB, frameColorA).endVertex();
        vertexBuffer.pos(0, this.height, 0).color(frameColorR, frameColorG, frameColorB, frameColorA).endVertex();
        vertexBuffer.pos(this.width, this.height, 0).color(frameColorR, frameColorG, frameColorB, frameColorA).endVertex();
        vertexBuffer.pos(this.width, 0, 0).color(frameColorR, frameColorG, frameColorB, frameColorA).endVertex();
        tessellator.draw();

        // Head
        vertexBuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        vertexBuffer.pos(0, 0, 0).color(frameColorR, frameColorG, frameColorB, frameColorA).endVertex();
        vertexBuffer.pos(0, this.headSize, 0).color(frameColorR, frameColorG, frameColorB, frameColorA).endVertex();
        vertexBuffer.pos(this.width, this.headSize, 0).color(frameColorR, frameColorG, frameColorB, frameColorA).endVertex();
        vertexBuffer.pos(this.width, 0, 0).color(frameColorR, frameColorG, frameColorB, frameColorA).endVertex();
        tessellator.draw();

        RenderUtils.renderText(this.title, this.frameWidth + 1, (this.headSize / 2) - 4, new Vector4f(1, 1, 1, 1));

        glPushMatrix();
        glTranslated(this.frameWidth, this.headSize, 0);
        this.renderBody(tessellator, vertexBuffer);
        glPopMatrix();
    }

    private void renderBody(final Tessellator tessellator, final VertexBuffer vertexBuffer) {
        final double h = this.getHeight() - this.headSize - this.frameWidth;
        final double w = this.getWidth() - (this.frameWidth * 2);

        final float bodyColorR = this.bodyColor.getX();
        final float bodyColorG = this.bodyColor.getY();
        final float bodyColorB = this.bodyColor.getZ();
        final float bodyColorA = this.bodyColor.getW();

        vertexBuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        vertexBuffer.pos(0, 0, 0).color(bodyColorR, bodyColorG, bodyColorB, bodyColorA).endVertex();
        vertexBuffer.pos(0, h, 0).color(bodyColorR, bodyColorG, bodyColorB, bodyColorA).endVertex();
        vertexBuffer.pos(w, h, 0).color(bodyColorR, bodyColorG, bodyColorB, bodyColorA).endVertex();
        vertexBuffer.pos(w, 0, 0).color(bodyColorR, bodyColorG, bodyColorB, bodyColorA).endVertex();
        tessellator.draw();

        for (final Component component : this.components) {
            glPushMatrix();
            glTranslated(component.getPositionX(), component.getPositionY(), 0);
            component.render(tessellator, vertexBuffer);
            glPopMatrix();
        }
    }

    public int getPositionX() {
        return this.positionX;
    }

    @Nonnull
    public Window setPositionX(int positionX) {
        this.positionX = positionX;
        return this;
    }

    public int getPositionY() {
        return this.positionY;
    }

    @Nonnull
    public Window setPositionY(int positionY) {
        this.positionY = positionY;
        return this;
    }

    @Nonnull
    public String getTitle() {
        return this.title;
    }

    @Nonnull
    public Window setTitle(String title) {
        this.title = title;
        return this;
    }

    public int getDecorationType() {
        return this.decorationType;
    }

    @Nonnull
    public Window setDecorationType(int decorationType) {
        this.decorationType = decorationType;
        return this;
    }

    public int getWidth() {
        return this.width;
    }

    @Nonnull
    public Window setWidth(final int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return this.height;
    }

    @Nonnull
    public Window setHeight(final int height) {
        this.height = height;
        return this;
    }

    public boolean isVisible() {
        return this.visible;
    }

    @Nonnull
    public Window setVisible(final boolean visible) {
        this.visible = visible;
        return this;
    }

    public void mouseMoved(int x, int y) {
    }

    public void mouseClicked(int x, int y, int mouseButton) {
        this.lastClickLocation = new Point(x, y);
        if (mouseButton == 0) {
            final boolean isMouseInHead = (x >= 0) && (x <= this.width) && (y >= 0) && (y <= this.headSize);
            this.currentlyDraggable = isMouseInHead;
            if (!isMouseInHead) {
                for (final Component component : this.components) {
                    if (component.mouseClicked(((int) (x - component.getPositionX())), ((int) (y - this.headSize - component.getPositionY())), mouseButton)) {
                        break;
                    }
                }
            }
        }
    }

    public void mouseReleased(int x, int y, int mouseButton) {
        if (mouseButton == 0) {
            if (!this.currentlyDraggable) {
                //do button clicks and stuff at some point
                for (final Component component : this.components) {
                    if (component.mouseReleased(((int) (x - component.getPositionX())), ((int) (y - this.headSize - component.getPositionY())), mouseButton)) {
                        break;
                    }
                }
            }
            this.currentlyDraggable = false;
        }
        this.lastClickLocation = null;
    }

    public void mouseDrag(int x, int y, int mouseButton) {
        if (mouseButton == 0) {
            if (this.currentlyDraggable) {
                this.positionX += x - this.lastClickLocation.getX();
                this.positionY += y - this.lastClickLocation.getY();
            }
        }
    }

    public boolean isPinned() {
        return this.pinned;
    }

    @Nonnull
    public Window setPinned(boolean pinned) {
        this.pinned = pinned;
        return this;
    }

    private enum WindowDecorationType {
        ALL(1), NO_BORDERS(2), NO_CLOSE(4), NO_PIN(8), NOTHING(16);

        private final int value;

        WindowDecorationType(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }
}
