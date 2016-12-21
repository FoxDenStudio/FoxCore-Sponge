package net.foxdenstudio.sponge.foxcore.plugin.util;

import com.flowpowered.math.vector.*;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.Random;

/**
 * Created by Fox on 10/13/2016.
 */
public class Position extends Vector3i {

    private Color color;

    public Position() {
        this(Color.WHITE);
    }

    public Position(Vector2i v) {
        this(v, Color.WHITE);
    }

    public Position(Vector2i v, double z) {
        this(v, z, Color.WHITE);
    }

    public Position(Vector2i v, int z) {
        this(v, z, Color.WHITE);
    }

    public Position(Vector3i v) {
        this(v, Color.WHITE);
    }

    public Position(Vector4i v) {
        this(v, Color.WHITE);
    }

    public Position(VectorNi v) {
        this(v, Color.WHITE);
    }

    public Position(double x, double y, double z) {
        this(x, y, z, Color.WHITE);
    }

    public Position(int x, int y, int z) {
        this(x, y, z, Color.WHITE);
    }

    public Position(Color color) {
        this.color = color;
    }

    public Position(Vector2i v, Color color) {
        super(v);
        this.color = color;
    }

    public Position(Vector2i v, double z, Color color) {
        super(v, z);
        this.color = color;
    }

    public Position(Vector2i v, int z, Color color) {
        super(v, z);
        this.color = color;
    }

    public Position(Vector3i v, Color color) {
        super(v);
        this.color = color;
    }

    public Position(Vector4i v, Color color) {
        super(v);
        this.color = color;
    }

    public Position(VectorNi v, Color color) {
        super(v);
        this.color = color;
    }

    public Position(double x, double y, double z, Color color) {
        super(x, y, z);
        this.color = color;
    }

    public Position(int x, int y, int z, Color color) {
        super(x, y, z);
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public TextColor getTextColor() {
        return color.getTextColor();
    }

    public Vector3f getRgbColor() {
        return color.getRgbColor();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Position position = (Position) o;

        return color == position.color;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + color.hashCode();
        return result;
    }

    public enum Color {
        WHITE(TextColors.WHITE, new Vector3f(1, 1, 1)),
        RED(TextColors.RED, new Vector3f(1, 0, 0)),
        ORANGE(TextColors.GOLD, new Vector3f(1, 0.5, 0)),
        YELLOW(TextColors.YELLOW, new Vector3f(1, 1, 0)),
        GREEN(TextColors.GREEN, new Vector3f(0, 1, 0)),
        AQUA(TextColors.AQUA, new Vector3f(0, 1, 1)),
        BLUE(TextColors.BLUE, new Vector3f(0, 0, 1)),
        MAGENTA(TextColors.LIGHT_PURPLE, new Vector3f(1, 0, 1)),
        BLACK(TextColors.DARK_GRAY, new Vector3f(0, 0, 0));

        private static final Random random = new Random();

        private final TextColor textColor;
        private final Vector3f rgbColor;

        Color(TextColor textColor, Vector3f rgbColor) {
            this.textColor = textColor;
            this.rgbColor = rgbColor;
        }

        public static Color randomColor() {
            Color[] values = Color.values();
            return values[random.nextInt(values.length)];
        }

        public static Color from(int index) {
            Color[] values = Color.values();
            if (index < 0 || index >= values.length) return null;
            return values[index];
        }

        public static Color from(String name) {
            for (Color color : values()) {
                if (color.name().equalsIgnoreCase(name)) return color;
            }
            return null;
        }

        public TextColor getTextColor() {
            return textColor;
        }

        public Vector3f getRgbColor() {
            return rgbColor;
        }

    }
}
