package net.foxdenstudio.sponge.foxcore.mod.windows.exceptions;

import net.foxdenstudio.sponge.foxcore.mod.windows.parts.IBasePart;

public class PartAlreadyRegisteredException extends Throwable {
    private final String fullyQualifiedName;
    private final Class<? extends IBasePart> iBasePart;

    public PartAlreadyRegisteredException(String fullyQualifiedName, final Class<? extends IBasePart> iBasePart) {
        this.fullyQualifiedName = fullyQualifiedName;
        this.iBasePart = iBasePart;
    }

    public Class<? extends IBasePart> getiBasePart() {
        return this.iBasePart;
    }

    public String getFullyQualifiedName() {
        return this.fullyQualifiedName;
    }
}
