package net.foxdenstudio.sponge.foxcore.mod.windows.parts;

import net.foxdenstudio.sponge.foxcore.mod.windows.Dependency;

import javax.annotation.Nullable;

public interface IBasePart {

    @Nullable
    Dependency getDependencies();

    void render();

    int getPositionX();

    int getPositionY();

    int getWidth();

    int getHeight();

    boolean isPinned();
}
