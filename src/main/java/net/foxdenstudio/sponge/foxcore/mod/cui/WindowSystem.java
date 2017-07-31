package net.foxdenstudio.sponge.foxcore.mod.cui;

import net.foxdenstudio.sponge.foxcore.mod.cui.windows.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.Point;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;

import static org.lwjgl.input.Keyboard.*;
import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
class WindowSystem extends CUISubSystem {

    public static final int DRAG_TIME_DEADZONE = 100;
    private final LinkedHashMap<UUID, Window> registeredWindows;
    private final UseOrderList<Window> windowUseOrder;
    private final CUI cui;
    private final Tessellator tessellator;
    private final VertexBuffer vertexBuffer;
    private final Minecraft minecraft;
    private final KeyBinding windowControlBinding;
    private final HashMap<Integer, List<Consumer<Integer>>> internalKeyBindings;
    private final HashMap<Window, Double> lastVisibleState;
    private boolean windowControlActive;
    private Window lastTouchedWindow;
    private long lastMouseEventTime;
    private int lastMouseButton;

    WindowSystem(@Nonnull final CUI cui, @Nonnull final Tessellator tessellator, @Nonnull final VertexBuffer vertexBuffer, @Nonnull final Minecraft minecraft) {
        this.cui = cui;
        this.tessellator = tessellator;
        this.vertexBuffer = vertexBuffer;
        this.minecraft = minecraft;

        this.registeredWindows = new LinkedHashMap<>();
        this.lastVisibleState = new HashMap<>();
        this.windowUseOrder = new UseOrderList<>();
        this.windowControlBinding = new KeyBinding("Access FoxCore Windows", KEY_GRAVE, "FoxCore");
        this.cui.registerKeyBinding(this.windowControlBinding);
        this.internalKeyBindings = new HashMap<>();

        final ArrayList<Consumer<Integer>> list = new ArrayList<>();
        list.add(ignored -> {
            this.registerWindow(new Window()
                    .setTitle("Test Window")
                    .setPositionX(10)
                    .setPositionY(20)
                    .setWidth(200)
                    .setHeight(100)
                    .setVisible(true));
        });
        this.internalKeyBindings.put(KEY_BACKSLASH, list);

    }

    public void registerWindow(@Nonnull final Window window) {
        final UUID windowUUID = UUID.randomUUID();
        this.registeredWindows.put(windowUUID, window);
        this.windowUseOrder.use(window);
        this.lastVisibleState.put(window, 0.00D);
    }

    public void render(float partialTicks) {
        if (this.windowControlActive) {
            glColor4f(0, 0, 0, .8F); // Should be a black mostly opaque overlay

            this.vertexBuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
            this.vertexBuffer.pos(0, 0, 0).endVertex();
            this.vertexBuffer.pos(0, this.minecraft.displayHeight, 0).endVertex();
            this.vertexBuffer.pos(this.minecraft.displayWidth, this.minecraft.displayHeight, 0).endVertex();
            this.vertexBuffer.pos(this.minecraft.displayWidth, 0, 0).endVertex();
            this.tessellator.draw();

            this.windowUseOrder.reverseStream()
                    .filter(Window::isVisible)
                    .forEachOrdered(window -> {
                        glPushMatrix();
                        glTranslated(window.getPositionX(), window.getPositionY(), 0);
                        window.render(this.cui);
                        final Double winInEff = this.lastVisibleState.get(window);
                        glColor4f(0, 0, 0, 1.00f - winInEff.floatValue());
                        this.vertexBuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
                        this.vertexBuffer.pos(0, 0, 0).endVertex();
                        this.vertexBuffer.pos(0, window.getHeight(), 0).endVertex();
                        this.vertexBuffer.pos(window.getWidth(), window.getHeight(), 0).endVertex();
                        this.vertexBuffer.pos(window.getWidth(), 0, 0).endVertex();
                        this.tessellator.draw();
                        if (winInEff < 1) {
                            this.lastVisibleState.put(window, winInEff + 0.05d);
                        }
                        glPopMatrix();
                    });

        } else {
            this.windowUseOrder.reverseStream()
                    .filter(Window::isPinned)
                    .filter(Window::isVisible)
                    .forEachOrdered(window -> {
                        glPushMatrix();
                        glTranslated(window.getPositionX(), window.getPositionY(), 0);
                        window.render(this.cui);
                        final Double winInEff = this.lastVisibleState.get(window);
                        glColor4f(0, 0, 0, 1.00f - winInEff.floatValue());
                        this.vertexBuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
                        this.vertexBuffer.pos(0, 0, 0).endVertex();
                        this.vertexBuffer.pos(0, window.getHeight(), 0).endVertex();
                        this.vertexBuffer.pos(window.getWidth(), window.getHeight(), 0).endVertex();
                        this.vertexBuffer.pos(window.getWidth(), 0, 0).endVertex();
                        this.tessellator.draw();
                        if (winInEff < 1) {
                            this.lastVisibleState.put(window, winInEff + 0.05d);
                        }
                        glPopMatrix();
                    });
        }
    }

    @Override
    public void onTick(@Nonnull final TickEvent event) {
        if (this.windowControlActive) {
            while (Mouse.next()) {
                final Point mousePosRel = this.cui.calculateMouseLocation();
                final Optional<Window> windowUnderMouse = this.getWindowUnderMouse(mousePosRel);

                windowUnderMouse.ifPresent(window -> this.lastTouchedWindow = window);

                if (this.lastTouchedWindow != null) {
                    this.lastTouchedWindow.mouseMoved(mousePosRel.getX() - this.lastTouchedWindow.getPositionX(),
                            mousePosRel.getY() - this.lastTouchedWindow.getPositionY());
                }

                final int curMouseButton = Mouse.getEventButton();

                if (Mouse.getEventButtonState()) {
                    this.lastMouseButton = curMouseButton;
                    this.lastMouseEventTime = Minecraft.getSystemTime();
                    System.out.println("Mouse Clicked: " + mousePosRel.getX() + " | " + mousePosRel.getY() + " | " + curMouseButton);

                    if (this.lastTouchedWindow != null) {
                        System.out.println("\t1");

                        final int wX = mousePosRel.getX() - this.lastTouchedWindow.getPositionX();
                        final int wY = mousePosRel.getY() - this.lastTouchedWindow.getPositionY();
                        this.windowUseOrder.use(this.lastTouchedWindow);
                        this.lastTouchedWindow.mouseClicked(wX, wY, curMouseButton);
                    }
                } else if (curMouseButton != -1) {
                    this.lastMouseButton = -1;
                    System.out.println("Mouse Released: " + mousePosRel.getX() + " | " + mousePosRel.getY() + " | " + curMouseButton);

                    if (this.lastTouchedWindow != null) {
                        System.out.println("\t2");

                        final int wX = mousePosRel.getX() - this.lastTouchedWindow.getPositionX();
                        final int wY = mousePosRel.getY() - this.lastTouchedWindow.getPositionY();
                        this.windowUseOrder.use(this.lastTouchedWindow);
                        this.lastTouchedWindow.mouseReleased(wX, wY, curMouseButton);
                    }
                    this.lastTouchedWindow = null;
                } else if (this.lastMouseButton != -1 && this.lastMouseEventTime > 0) {
                    System.out.println("Mouse Dragged: " + mousePosRel.getX() + " | " + mousePosRel.getY() + " | " + curMouseButton);

                    if ((Minecraft.getSystemTime() - this.lastMouseEventTime) > DRAG_TIME_DEADZONE && this.lastTouchedWindow != null) {
                        System.out.println("\t3");

                        final int wX = mousePosRel.getX() - this.lastTouchedWindow.getPositionX();
                        final int wY = mousePosRel.getY() - this.lastTouchedWindow.getPositionY();
                        this.windowUseOrder.use(this.lastTouchedWindow);
                        this.lastTouchedWindow.mouseDrag(wX, wY, this.lastMouseButton);
                    }
                }
            }
            while (Keyboard.next()) {
                final int eventKey = Keyboard.getEventKey();
                if (Keyboard.isKeyDown(eventKey)) {
                    if (eventKey == KEY_ESCAPE || eventKey == this.windowControlBinding.getKeyCode()) {
                        this.windowControlActive = false;
                        this.minecraft.setIngameFocus();
                        KeyBinding.unPressAllKeys();
                    } else {
                        if (this.internalKeyBindings.containsKey(eventKey)) {
                            this.internalKeyBindings.get(eventKey).forEach(c -> c.accept(eventKey));
                        }
                    }
                }
            }
        }
    }

    private Optional<Window> getWindowUnderMouse(@Nonnull final Point mousePosRel) {
        return this.windowUseOrder.stream()
                .filter(window -> mousePosRel.getX() >= window.getPositionX())
                .filter(window -> mousePosRel.getX() <= window.getPositionX() + window.getWidth())
                .filter(window -> mousePosRel.getY() >= window.getPositionY())
                .filter(window -> mousePosRel.getY() <= window.getPositionY() + window.getHeight())
                .findFirst();
    }

    @Override
    public void onKeyPress(@Nonnull final InputEvent.KeyInputEvent event) {
        if (this.windowControlBinding.isPressed()) {
            if (!this.windowControlActive) {
                this.windowControlActive = true;
                Minecraft.getMinecraft().setIngameNotInFocus();
                KeyBinding.unPressAllKeys();
            }
        }
    }
}
