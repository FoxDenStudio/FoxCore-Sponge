package net.foxdenstudio.sponge.foxcore.mod.windows.parts;

import net.foxdenstudio.sponge.foxcore.mod.windows.Dependency;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class WindowPart implements IBasePart {

    protected final Tessellator tessellator = Tessellator.getInstance();
    protected final VertexBuffer vertexBuffer = this.tessellator.getBuffer();
    protected final List<IBasePart> partList;
    protected int height;
    protected int positionX;
    protected int positionY;
    protected int width;
    protected boolean pinned;
    protected String title;

    protected WindowPart() {
        this.partList = new ArrayList<>();
    }

    public WindowPart addComponent(@Nonnull final IComponentPart iComponentPart) {
        this.partList.add(iComponentPart);
        return this;
    }

    public WindowPart addModule(@Nonnull final ModulePart modulePart) {
        this.partList.add(modulePart);
        return this;
    }

    @Nullable
    @Override
    public Dependency getDependencies() {
        return new Dependency().add("net.foxdenstudio.windowapi:label", null);
    }

    @Override
    public void render() {
        this.partList.forEach(IBasePart::render);
    }

    @Override
    public int getPositionX() {
        return this.positionX;
    }

    public WindowPart setPositionX(int positionX) {
        this.positionX = positionX;
        return this;
    }

    @Override
    public int getPositionY() {
        return this.positionY;
    }

    public WindowPart setPositionY(int positionY) {
        this.positionY = positionY;
        return this;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    public WindowPart setWidth(int width) {
        this.width = width;
        return this;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    public WindowPart setHeight(int height) {
        this.height = height;
        return this;
    }

    @Override
    public boolean isPinned() {
        return this.pinned;
    }

    public WindowPart setPinned(boolean pinned) {
        this.pinned = pinned;
        return this;
    }

    @Nonnull
    public String getTitle() {
        return this.title;
    }

    public WindowPart setTitle(@Nonnull final String title) {
        this.title = title;
        return this;
    }
}
