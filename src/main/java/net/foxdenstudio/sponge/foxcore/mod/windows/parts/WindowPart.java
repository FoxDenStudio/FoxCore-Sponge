package net.foxdenstudio.sponge.foxcore.mod.windows.parts;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class WindowPart extends BasePart {

    protected final List<BasePart> partList;
    protected String title;

    protected WindowPart() {
        this.partList = new ArrayList<>();
    }

    public WindowPart addComponent(@Nonnull final ComponentPart iComponentPart) {
        this.partList.add(iComponentPart);
        return this;
    }

    public WindowPart addModule(@Nonnull final ModulePart modulePart) {
        this.partList.add(modulePart);
        return this;
    }

    @Override
    public void render() {
        this.partList.forEach(BasePart::render);
    }

    @Nonnull
    public String getTitle() {
        return this.title;
    }

    public WindowPart setTitle(@Nonnull final String title) {
        this.title = title;
        return this;
    }

    @Override
    public void mouseClicked(int x, int y, int button) {

    }

    @Override
    public void mouseReleased(int x, int y, int button) {

    }

    @Override
    public BasePart revalidate() {
        return this;
    }

    @Override
    public void mouseDrag(int x, int y, int button) {
    }
}
