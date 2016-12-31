package net.foxdenstudio.sponge.foxcore.mod.windows;

import net.foxdenstudio.sponge.foxcore.mod.windows.exceptions.PartAlreadyRegisteredException;
import net.foxdenstudio.sponge.foxcore.mod.windows.parts.BasePart;
import net.foxdenstudio.sponge.foxcore.mod.windows.parts.ComponentPart;
import net.foxdenstudio.sponge.foxcore.mod.windows.parts.ModulePart;
import net.foxdenstudio.sponge.foxcore.mod.windows.parts.WindowPart;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("SameParameterValue")
public class Registry {

    private static final Registry INSTANCE = new Registry();

    private final Map<String, Class<? extends ComponentPart>> componentPartsMap;
    private final Map<String, Class<? extends ModulePart>> modulePartsMap;
    private final Map<String, Class<? extends WindowPart>> windowPartsMap;

    private final UseOrderList<BasePart> partList;

    private Registry() {
        this.componentPartsMap = new HashMap<>();
        this.modulePartsMap = new HashMap<>();
        this.windowPartsMap = new HashMap<>();

        this.partList = new UseOrderList<>();
    }

    public static Registry getInstance() {
        return INSTANCE;
    }

    public Registry registerComponentPart(@Nonnull final String fullyQualifiedName, Class<? extends ComponentPart> iComponent) throws PartAlreadyRegisteredException {
        return registerComponentPart(fullyQualifiedName, iComponent, false);
    }

    public Registry registerComponentPart(@Nonnull final String fullyQualifiedName, Class<? extends ComponentPart> iComponent, final boolean force) throws PartAlreadyRegisteredException {
        if (!force && this.componentPartsMap.containsKey(fullyQualifiedName)) {
            throw new PartAlreadyRegisteredException(fullyQualifiedName, iComponent);
        }
        this.componentPartsMap.put(fullyQualifiedName, iComponent);
        return this;
    }

    public Registry registerModulePart(@Nonnull final String fullyQualifiedName, Class<? extends ModulePart> module) throws PartAlreadyRegisteredException {
        return registerModulePart(fullyQualifiedName, module, false);
    }

    public Registry registerModulePart(@Nonnull final String fullyQualifiedName, Class<? extends ModulePart> module, final boolean force) throws PartAlreadyRegisteredException {
        if (!force && this.modulePartsMap.containsKey(fullyQualifiedName)) {
            throw new PartAlreadyRegisteredException(fullyQualifiedName, module);
        }
        this.modulePartsMap.put(fullyQualifiedName, module);
        return this;
    }

    public Registry registerWindowPart(@Nonnull final String fullyQualifiedName, Class<? extends WindowPart> window) throws PartAlreadyRegisteredException {
        return registerWindowPart(fullyQualifiedName, window, false);
    }

    public Registry registerWindowPart(@Nonnull final String fullyQualifiedName, Class<? extends WindowPart> window, final boolean force) throws PartAlreadyRegisteredException {
        if (!force && this.windowPartsMap.containsKey(fullyQualifiedName)) {
            throw new PartAlreadyRegisteredException(fullyQualifiedName, window);
        }
        this.windowPartsMap.put(fullyQualifiedName, window);
        return this;
    }

    @Nonnull
    public UseOrderList<BasePart> getPartList() {
        return this.partList;
    }

    @Nonnull
    public Registry addParts(@Nonnull final BasePart... parts) {
        for (final BasePart part : parts) {
            this.partList.use(part);
        }
        return this;
    }

    @Nullable
    public BasePart getPartUnder(int x, int y) {
        for (final BasePart basePart : this.partList.itemList) {
            if (x >= basePart.getPositionX() && x <= basePart.getPositionX() + basePart.getWidth() && y >= basePart.getPositionY() && y <= basePart.getPositionY() + basePart.getHeight()) {
                return basePart;
            }
        }
        return null;
    }

    public void removePart(@Nonnull final BasePart basePart) {
        this.partList.remove(basePart);
    }
}
