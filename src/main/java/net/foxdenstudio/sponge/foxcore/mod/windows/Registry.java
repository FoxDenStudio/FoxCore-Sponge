package net.foxdenstudio.sponge.foxcore.mod.windows;

import net.foxdenstudio.sponge.foxcore.mod.windows.exceptions.PartAlreadyRegisteredException;
import net.foxdenstudio.sponge.foxcore.mod.windows.parts.IComponentPart;
import net.foxdenstudio.sponge.foxcore.mod.windows.parts.ModulePart;
import net.foxdenstudio.sponge.foxcore.mod.windows.parts.WindowPart;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("SameParameterValue")
public class Registry {

    private static final Registry INSTANCE = new Registry();

    private final Map<String, Class<? extends IComponentPart>> componentPartsMap;
    private final Map<String, Class<? extends ModulePart>> modulePartsMap;
    private final Map<String, Class<? extends WindowPart>> windowPartsMap;

    private final Map<UUID, IComponentPart> components;
    private final Map<UUID, ModulePart> modules;
    private final UseOrderList<WindowPart> windows;

    private Registry() {
        this.componentPartsMap = new HashMap<>();
        this.modulePartsMap = new HashMap<>();
        this.windowPartsMap = new HashMap<>();

        this.components = new HashMap<>();
        this.modules = new HashMap<>();
        this.windows = new UseOrderList<>();
    }

    public static Registry getInstance() {
        return INSTANCE;
    }

    public Registry registerComponentPart(@Nonnull final String fullyQualifiedName, Class<? extends IComponentPart> iComponent) throws PartAlreadyRegisteredException {
        return registerComponentPart(fullyQualifiedName, iComponent, false);
    }

    public Registry registerComponentPart(@Nonnull final String fullyQualifiedName, Class<? extends IComponentPart> iComponent, final boolean force) throws PartAlreadyRegisteredException {
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
    public Map<UUID, IComponentPart> getComponents() {
        return this.components;
    }

    @Nonnull
    public Map<UUID, ModulePart> getModules() {
        return this.modules;
    }

    @Nonnull
    public UseOrderList<WindowPart> getWindows() {
        return this.windows;
    }

    public Registry addComponent(@Nonnull final IComponentPart iComponentPart) {
        return addComponent(UUID.randomUUID(), iComponentPart);
    }

    public Registry addComponent(@Nonnull final UUID uuid, @Nonnull final IComponentPart windowPart) {
        this.components.put(uuid, windowPart);
        return this;
    }

    public Registry addModule(@Nonnull final ModulePart modulePart) {
        return addModule(UUID.randomUUID(), modulePart);
    }

    public Registry addModule(@Nonnull final UUID uuid, @Nonnull final ModulePart modulePart) {
        this.modules.put(uuid, modulePart);
        return this;
    }

    public Registry addWindow(@Nonnull final WindowPart windowPart) {
        return addWindow(UUID.randomUUID(), windowPart);
    }

    public Registry addWindow(@Nonnull final UUID uuid, @Nonnull final WindowPart windowPart) {
        this.windows.use(windowPart);
        return this;
    }

    @Nullable
    public WindowPart getWindowUnder(int x, int y) {
        for (final WindowPart windowPart : this.windows.itemList) {
            if (x >= windowPart.getPositionX() && x <= windowPart.getPositionX() + windowPart.getWidth() && y >= windowPart.getPositionY() && y <= windowPart.getPositionY() + windowPart.getHeight()) {
                return windowPart;
            }
        }
        return null;
    }

    public void removeWindow(@Nonnull final WindowPart windowPart) {
        this.windows.remove(windowPart);
    }
}
