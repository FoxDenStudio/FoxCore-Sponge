package net.foxdenstudio.foxcore.sponge.mixin.sponge;

import net.foxdenstudio.foxcore.platform.CatalogType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(org.spongepowered.api.CatalogType.class)
public interface MixinCatalogType extends CatalogType {
}
