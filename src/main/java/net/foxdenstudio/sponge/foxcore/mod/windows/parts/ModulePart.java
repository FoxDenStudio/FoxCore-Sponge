package net.foxdenstudio.sponge.foxcore.mod.windows.parts;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class ModulePart implements IBasePart {

    private final List<IComponentPart> componentList;

    public ModulePart() {
        this.componentList = new ArrayList<>();
    }

    public ModulePart addComponent(@Nonnull final IComponentPart iComponentPart) {
        this.componentList.add(iComponentPart);
        return this;
    }
}
