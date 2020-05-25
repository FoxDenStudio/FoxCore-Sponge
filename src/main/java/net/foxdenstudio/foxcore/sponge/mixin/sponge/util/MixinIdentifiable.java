package net.foxdenstudio.foxcore.sponge.mixin.sponge.util;

import net.foxdenstudio.foxcore.platform.util.Identifiable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(org.spongepowered.api.util.Identifiable.class)
public interface MixinIdentifiable extends Identifiable {
}
