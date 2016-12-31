package net.foxdenstudio.sponge.foxcore.mod.windows.components;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector4f;
import net.foxdenstudio.sponge.foxcore.mod.windows.geometries.GeometrySquareButton;
import net.foxdenstudio.sponge.foxcore.mod.windows.parts.BasePart;
import net.foxdenstudio.sponge.foxcore.mod.windows.parts.ComponentPart;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class Button extends ComponentPart {

    private final Map<Integer, List<BiConsumer<Vector2i, Integer>>> mouseClickCallbacks;
    private GeometrySquareButton geometry;
    private String text;
    private Vector4f backgroundColorVector;
    private Vector4f textColorVector;

    public Button() {
        this("");
    }

    public Button(@Nonnull final String text) {
        this.text = text;
        this.mouseClickCallbacks = new HashMap<>();
        this.width = fontRendererObj.getStringWidth(text) + 2;
        this.height = 8 + 2;
        this.backgroundColorVector = new Vector4f(1, 0, 1, 1);
        this.textColorVector = new Vector4f(1, 1, 1, 1);
        revalidate();
    }

    public String getText() {
        return this.text;
    }

    public Button setText(String text) {
        this.text = text;
        return this;
    }

    public Vector4f getBackgroundColorVector() {
        return this.backgroundColorVector;
    }

    /**
     * All values are between 0 and 1
     *
     * @param backgroundColorVector
     * @return
     */
    @Nonnull
    public Button setBackgroundColorVector(@Nonnull final Vector4f backgroundColorVector) {
        this.backgroundColorVector = backgroundColorVector;
        return this;
    }

    public Vector4f getTextColorVector() {
        return this.textColorVector;
    }

    /**
     * All values are between 0 and 1
     *
     * @param textColorVector
     * @return
     */
    @Nonnull
    public Button setTextColorVector(@Nonnull final Vector4f textColorVector) {
        this.textColorVector = textColorVector;
        return this;
    }

    @Override
    public void render() {
        if (this.geometry != null) {
            this.geometry.render();
        }
    }

    // TODO: Tristate... Accepted/True, Failed/False, Ignored
    public boolean tryClick(int x, int y, int buttonCode) {
        if (this.geometry.pointLiesWithin(x, y, 0)) {
            if (this.mouseClickCallbacks.containsKey(buttonCode)) {
                final List<BiConsumer<Vector2i, Integer>> biConsumers = this.mouseClickCallbacks.get(buttonCode);
                biConsumers.forEach(callback -> callback.accept(new Vector2i(x, y), buttonCode));
                if (biConsumers.size() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Nonnull
    public Button clearClickHandlers(int buttonCode) {
        if (buttonCode == -1) {
            this.mouseClickCallbacks.clear();
        } else {
            if (this.mouseClickCallbacks.containsKey(buttonCode)) {
                this.mouseClickCallbacks.get(buttonCode).clear();
            }
        }
        return this;
    }

    @Nonnull
    public Button addClickHandler(int buttonCode, @Nonnull final BiConsumer<Vector2i, Integer> callback) {
        if (!this.mouseClickCallbacks.containsKey(buttonCode)) {
            this.mouseClickCallbacks.put(buttonCode, new ArrayList<>());
        }
        this.mouseClickCallbacks.get(buttonCode).add(callback);
        return this;
    }

    @Override
    public BasePart revalidate() {
        this.geometry = new GeometrySquareButton(
                new Vector3d(this.positionX, this.positionY, 0),
                new Vector3d(this.positionX + this.width, this.positionY + this.height, 0),
                this.backgroundColorVector,
                this.textColorVector,
                () -> this.text
        );
        return this;
    }

    @Override
    public String toString() {
        return "Button{" +
                "mouseClickCallbacks=" + this.mouseClickCallbacks +
                ", geometry=" + this.geometry +
                ", text='" + this.text + '\'' +
                ", backgroundColorVector=" + this.backgroundColorVector +
                ", textColorVector=" + this.textColorVector +
                '}';
    }
}
