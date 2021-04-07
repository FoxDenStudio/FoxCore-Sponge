package net.foxdenstudio.foxsuite.foxcore.sponge.mixin.sponge.text.format;

import net.foxdenstudio.foxsuite.foxcore.platform.text.format.TextColor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(org.spongepowered.api.text.format.TextColor.class)
public interface MixinTextColor extends TextColor {

}
