package net.foxdenstudio.sponge.foxcore.mod.cui.windows.components;

import com.flowpowered.math.vector.Vector4f;
import net.foxdenstudio.sponge.foxcore.mod.cui.RenderUtils;
import net.foxdenstudio.sponge.foxcore.mod.cui.windows.Component;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;

import javax.annotation.Nonnull;

public class LabelComponent extends Component {

    private String text;
    private double positionX;
    private double positionY;
    private double width;
    private double height;
    private Vector4f textColor;

    public LabelComponent() {
        this("");
    }

    public LabelComponent(final String text) {
        this.text = text;
        this.textColor = new Vector4f(1, 1, 1, 1);
    }

    @Override
    public void render(@Nonnull final Tessellator tessellator, @Nonnull final VertexBuffer vertexBuffer) {
        RenderUtils.renderText(this.text, 0, 0, this.textColor);
    }

    @Nonnull
    public String getText() {
        return this.text;
    }

    @Nonnull
    public LabelComponent setText(@Nonnull final String text) {
        this.text = text;
        return this;
    }

    @Override
    public double getPositionX() {
        return this.positionX;
    }

    @Nonnull
    public LabelComponent setPositionX(double positionX) {
        this.positionX = positionX;
        return this;
    }

    @Override
    public double getPositionY() {
        return this.positionY;
    }

    @Nonnull
    public LabelComponent setPositionY(double positionY) {
        this.positionY = positionY;
        return this;
    }

    @Override
    public double getWidth() {
        return this.width;
    }

    @Nonnull
    public LabelComponent setWidth(double width) {
        this.width = width;
        return this;
    }

    @Override
    public double getHeight() {
        return this.height;
    }

    @Nonnull
    public LabelComponent setHeight(double height) {
        this.height = height;
        return this;
    }

    public Vector4f getTextColor() {
        return this.textColor;
    }

    @Nonnull
    public LabelComponent setTextColor(@Nonnull final Vector4f textColor) {
        this.textColor = textColor;
        return this;
    }

    @Override
    public String toString() {
        return "LabelComponent{" +
                "text='" + this.text + '\'' +
                ", positionX=" + this.positionX +
                ", positionY=" + this.positionY +
                ", width=" + this.width +
                ", height=" + this.height +
                ", textColor=" + this.textColor +
                '}';
    }
}
