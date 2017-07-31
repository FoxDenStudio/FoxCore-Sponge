package net.foxdenstudio.sponge.foxcore.mod.cui;

import com.flowpowered.math.vector.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import javax.annotation.Nonnull;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtils {
    private static final FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRenderer;

    public static void renderText(@Nonnull final String text, double x, double y, @Nonnull Vector4f color) {
        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glColor4f(color.getX(), color.getY(), color.getZ(), color.getW());
        final boolean old = fontRendererObj.getUnicodeFlag();
        fontRendererObj.setUnicodeFlag(true);
        fontRendererObj.drawStringWithShadow(text, ((float) x), ((float) y), ((((int) (255 * color.getX()))) << 24) + ((((int) (255 * color.getY()))) << 16) + ((((int) (255 * color.getZ()))) << 8) + (((int) (255 * color.getW()))));
        fontRendererObj.setUnicodeFlag(old);
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
    }

}
