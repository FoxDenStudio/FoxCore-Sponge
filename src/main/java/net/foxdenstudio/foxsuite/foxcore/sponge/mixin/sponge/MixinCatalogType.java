package net.foxdenstudio.foxsuite.foxcore.sponge.mixin.sponge;

import net.foxdenstudio.foxsuite.foxcore.platform.CatalogType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(org.spongepowered.api.CatalogType.class)
public interface MixinCatalogType extends CatalogType {
}
