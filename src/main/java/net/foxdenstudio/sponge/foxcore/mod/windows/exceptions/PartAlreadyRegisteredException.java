package net.foxdenstudio.sponge.foxcore.mod.windows.exceptions;

import net.foxdenstudio.sponge.foxcore.mod.windows.parts.BasePart;

public class PartAlreadyRegisteredException extends Throwable {
    private final String fullyQualifiedName;
    private final Class<? extends BasePart> iBasePart;

    public PartAlreadyRegisteredException(String fullyQualifiedName, final Class<? extends BasePart> iBasePart) {
        this.fullyQualifiedName = fullyQualifiedName;
        this.iBasePart = iBasePart;
    }

    public Class<? extends BasePart> getiBasePart() {
        return this.iBasePart;
    }

    public String getFullyQualifiedName() {
        return this.fullyQualifiedName;
    }
}
