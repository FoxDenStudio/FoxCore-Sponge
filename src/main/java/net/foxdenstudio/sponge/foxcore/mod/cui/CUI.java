package net.foxdenstudio.sponge.foxcore.mod.cui;

import net.foxdenstudio.sponge.foxcore.mod.FoxCoreClientMain;
import net.foxdenstudio.sponge.foxcore.mod.cui.windows.Window;
import net.foxdenstudio.sponge.foxcore.mod.cui.windows.components.LabelComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.Point;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class CUI {

    private final WindowSystem windowSystem;
    private final WorldSystem worldSystem;
    private final VertexBuffer vertexBuffer;
    private final Tessellator tessellator;
    private final FoxCoreClientMain foxCoreClientMain;
    private final List<KeyBinding> registeredKeyBindings;
    private final Window window;
    private final LabelComponent labelComponent;
    private Minecraft minecraft;

    public CUI(@Nonnull final FoxCoreClientMain foxCoreClientMain) {
        this.foxCoreClientMain = foxCoreClientMain;

        this.registeredKeyBindings = new ArrayList<>();

        this.tessellator = Tessellator.getInstance();
        this.vertexBuffer = this.tessellator.getBuffer();
        this.minecraft = Minecraft.getMinecraft();

        this.worldSystem = new WorldSystem(this, this.tessellator, this.vertexBuffer, this.minecraft);
        this.windowSystem = new WindowSystem(this, this.tessellator, this.vertexBuffer, this.minecraft);

        this.window = new Window("World Start!").setPositionX(10).setPositionY(10).setWidth(160).setHeight(80).setPinned(true).setVisible(true);
        this.labelComponent = new LabelComponent("Player Position: ").setPositionX(10).setPositionY(10);
        this.window.addComponent(this.labelComponent);
        this.windowSystem.registerWindow(this.window);
    }

    public void registerWindow(@Nonnull final Window window) {
        this.windowSystem.registerWindow(window);
    }


    @SubscribeEvent
    public void renderInWorld(@Nonnull final RenderWorldLastEvent event) {
        glPushAttrib(GL_ENABLE_BIT | GL_COLOR_BUFFER_BIT | GL_CURRENT_BIT | GL_DEPTH_BUFFER_BIT);
        glPushMatrix();

        glDisable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_ALPHA_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDepthMask(false);

        this.worldSystem.render();

        glPopMatrix();
        glPopAttrib();
    }

    @SubscribeEvent
    public void render(@Nonnull final RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        glPushAttrib(GL_CURRENT_BIT | GL_ENABLE_BIT | GL_COLOR_BUFFER_BIT);
        glPushMatrix();

        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        this.windowSystem.render(event.getPartialTicks());

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);

        glPopMatrix();
        glPopAttrib();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onKeyPress(@Nonnull final InputEvent.KeyInputEvent event) {
        this.windowSystem.onKeyPress(event);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTick(@Nonnull final TickEvent event) {
        this.windowSystem.onTick(event);
        if (this.minecraft.player != null) {
            final double nPosX = this.minecraft.player.posX;
            final double nPosZ = this.minecraft.player.posZ;
            if (nPosX >= 0 && nPosX <= 100 && nPosZ >= 0 && nPosZ <= 100) {
                this.labelComponent.setText(String.format("Player Position: %.2f/%.2f/%.2f", nPosX, this.minecraft.player.posY, nPosZ));
                this.window.setVisible(true);
            } else {
                this.window.setVisible(false);
            }
        }
    }

    public void registerKeyBinding(final KeyBinding... keyBindings) {
        this.registeredKeyBindings.addAll(Arrays.asList(keyBindings));
    }

    public List<KeyBinding> getRegisteredKeyBindings() {
        return this.registeredKeyBindings;
    }

    @Nonnull
    public Point calculateMouseLocation() {
        int guiScale = this.minecraft.gameSettings.guiScale;
        if (guiScale == 0) {
            guiScale = 1000;
        }

        int scaleFactor = 0;
        while (scaleFactor < guiScale && (this.minecraft.displayWidth / (scaleFactor + 1)) >= 320 && (this.minecraft.displayHeight / (scaleFactor + 1) >= 240)) {
            scaleFactor++;
        }

        return new Point(Mouse.getX() / scaleFactor, (this.minecraft.displayHeight / scaleFactor) - (Mouse.getY() / scaleFactor));
    }

    public Tessellator getTessellator() {
        return this.tessellator;
    }

    public VertexBuffer getVertexBuffer() {
        return this.vertexBuffer;
    }
}
