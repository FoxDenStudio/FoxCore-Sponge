package net.foxdenstudio.sponge.foxcore.mod.rendernew.windows;

import net.foxdenstudio.sponge.foxcore.mod.FoxCoreClientMain;
import net.foxdenstudio.sponge.foxcore.mod.rendernew.windows.components.Window;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class WindowRenderer {

    private final List<Window> windows;

    public WindowRenderer() {
        this.windows = new ArrayList<>();
    }

    public boolean add(@Nonnull final Window window) {
        return this.windows.add(window);
    }

    public boolean remove(@Nonnull final Window window) {
        return this.windows.remove(window);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        glPushAttrib(GL_CURRENT_BIT | GL_ENABLE_BIT | GL_COLOR_BUFFER_BIT);
        glPushMatrix();

        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        if (FoxCoreClientMain.instance.getIsWindowControlActive()) {
            this.windows.iterator().forEachRemaining(Window::render);
        } else {
            this.windows.iterator().forEachRemaining(window -> {
                if (window.isPinned()) {
                    window.render();
                }
            });
        }

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glPopMatrix();
        glPopAttrib();
    }

    @Nullable
    public Window getWindowUnder(int x, int y) {
        for (Window window : this.windows) {
            if ((x >= window.getPosX()) && (x <= (window.getPosX() + window.getWidth())) && (y >= window.getPosY()) && (y <= (window.getPosY() + window.getHeight()))) {
                return window;
            }
        }
        return null;
    }
}