package net.foxdenstudio.foxcore.sponge.mixin.sponge.text.format;

import net.foxdenstudio.foxcore.platform.text.format.TextColor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(org.spongepowered.api.text.format.TextColor.class)
public interface MixinTextColor extends TextColor {

}
