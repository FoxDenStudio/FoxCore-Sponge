package net.foxdenstudio.sponge.foxcore.mod.cui.windows.components;

import com.flowpowered.math.vector.Vector4f;
import net.foxdenstudio.sponge.foxcore.mod.cui.RenderUtils;
import net.foxdenstudio.sponge.foxcore.mod.cui.windows.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11.GL_QUADS;

public class ButtonComponent extends Component {

    private final FontRenderer fontRenderer;
    private String buttonText;
    private double positionX;
    private double positionY;
    private double width;
    private double height;
    private Vector4f textColor;
    private boolean pressed;
    private Consumer<Integer> clickHandler;

    public ButtonComponent() {
        this("");
    }

    public ButtonComponent(@Nonnull final String buttonText) {
        this.textColor = new Vector4f(1, 1, 1, 1);
        this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
        this.height = this.fontRenderer.FONT_HEIGHT;
        this.setButtonText(buttonText);
    }

    @Override
    public void render(@Nonnull final Tessellator tessellator, @Nonnull final VertexBuffer vertexBuffer) {

        //181,082,057
        final float frameColorR = this.pressed ? 136.00f / 255.00f : 181.00f / 255.00f;
        final float frameColorG = this.pressed ? 57.00f / 255.00f : 82.00f / 255.00f;
        final float frameColorB = this.pressed ? 37.00f / 255.00f : 57.00f / 255.00f;
        final float frameColorA = 1;
        // Frame
        vertexBuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        vertexBuffer.pos(0, 0, 0).color(frameColorR, frameColorG, frameColorB, frameColorA).endVertex();
        vertexBuffer.pos(0, this.height, 0).color(frameColorR, frameColorG, frameColorB, frameColorA).endVertex();
        vertexBuffer.pos(this.width, this.height, 0).color(frameColorR, frameColorG, frameColorB, frameColorA).endVertex();
        vertexBuffer.pos(this.width, 0, 0).color(frameColorR, frameColorG, frameColorB, frameColorA).endVertex();
        tessellator.draw();

        RenderUtils.renderText(this.buttonText, 2, 0, this.textColor);
    }

    @Override
    public boolean mouseClicked(int x, int y, int mouseButton) {
        if ((x >= 0 && x <= this.width) && (y >= 0 && y <= this.height)) {
            this.pressed = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(int x, int y, int mouseButton) {
        if ((x >= 0 && x <= this.width) && (y >= 0 && y <= this.height)) {
            if (this.pressed) {
                if (this.clickHandler != null) {
                    this.clickHandler.accept(mouseButton);
                }
            }
            this.pressed = false;
            return true;
        }
        return false;
    }

    @Nullable
    public Consumer<Integer> getClickHandler() {
        return this.clickHandler;
    }

    @Nonnull
    public ButtonComponent setClickHandler(final Consumer<Integer> clickHandler) {
        this.clickHandler = clickHandler;
        return this;
    }

    @Nonnull
    public ButtonComponent setButtonText(@Nonnull final String buttonText) {
        this.buttonText = buttonText;
        this.width = this.fontRenderer.getStringWidth(buttonText);
        return this;
    }

    @Nonnull
    public ButtonComponent setTextColor(@Nonnull final Vector4f textColor) {
        this.textColor = textColor;
        return this;
    }

    @Override
    public double getPositionX() {
        return this.positionX;
    }

    @Nonnull
    public ButtonComponent setPositionX(double positionX) {
        this.positionX = positionX;
        return this;
    }

    @Override
    public double getPositionY() {
        return this.positionY;
    }

    @Nonnull
    public ButtonComponent setPositionY(double positionY) {
        this.positionY = positionY;
        return this;
    }

    @Override
    public double getWidth() {
        return this.width;
    }

    @Nonnull
    public ButtonComponent setWidth(double width) {
        this.width = width;
        return this;
    }

    @Override
    public double getHeight() {
        return this.height;
    }

    @Nonnull
    public ButtonComponent setHeight(double height) {
        this.height = height;
        return this;
    }

    @Override
    public String toString() {
        return "ButtonComponent{" +
                "buttonText='" + this.buttonText + '\'' +
                ", positionX=" + this.positionX +
                ", positionY=" + this.positionY +
                ", width=" + this.width +
                ", height=" + this.height +
                ", textColor=" + this.textColor +
                ", pressed=" + this.pressed +
                '}';
    }
}
