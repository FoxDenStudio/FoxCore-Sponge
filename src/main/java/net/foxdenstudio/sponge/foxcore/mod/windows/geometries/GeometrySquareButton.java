package net.foxdenstudio.sponge.foxcore.mod.windows.geometries;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector4f;
import net.foxdenstudio.sponge.foxcore.mod.rendernew.world.geometry.GeometrySquare;
import net.foxdenstudio.sponge.foxcore.mod.windows.TextRetriever;
import net.foxdenstudio.sponge.foxcore.mod.windows.parts.BasePart;

import javax.annotation.Nonnull;

public class GeometrySquareButton extends GeometrySquare {

    private final TextRetriever textRetriever;
    private final Vector4f textColor;

    public GeometrySquareButton(@Nonnull final Vector3d start, @Nonnull final Vector3d end, @Nonnull final Vector4f backgroundColor, @Nonnull final Vector4f textColor, @Nonnull final TextRetriever textRetriever) {
        super(start, end, backgroundColor);
        this.textColor = textColor;
        this.textRetriever = textRetriever;
    }

    @Override
    public void render() {
        super.render();
        final String text = this.textRetriever.accept();
        BasePart.renderText(text, ((float) (this.start.getX() + ((this.end.getX() - this.start.getX()) / 2 - (BasePart.textWidth(text) / 2) + 1))), (float) (this.start.getY() + ((this.end.getY() - this.start.getY()) / 2) - 4), this.textColor);
    }
}
