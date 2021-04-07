package net.foxdenstudio.foxsuite.foxcore.sponge.mixin.sponge.util;

import net.foxdenstudio.foxsuite.foxcore.platform.util.Identifiable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(org.spongepowered.api.util.Identifiable.class)
public interface MixinIdentifiable extends Identifiable {
}
