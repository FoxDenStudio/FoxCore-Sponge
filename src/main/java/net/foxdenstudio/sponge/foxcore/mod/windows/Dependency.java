package net.foxdenstudio.sponge.foxcore.mod.windows;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Dependency {

    private final List<String[]> dependencies;

    public Dependency() {
        this.dependencies = new ArrayList<>();
    }

    public Dependency add(@Nonnull String fullyQualifiedName, @Nullable String version) {
        this.dependencies.add(new String[]{fullyQualifiedName, version});
        return this;
    }
}
