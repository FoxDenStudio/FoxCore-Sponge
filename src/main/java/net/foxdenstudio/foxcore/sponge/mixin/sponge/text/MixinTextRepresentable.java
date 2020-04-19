package net.foxdenstudio.foxcore.sponge.mixin.sponge.text;

import net.foxdenstudio.foxcore.platform.text.Text;
import net.foxdenstudio.foxcore.platform.text.TextRepresentable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(org.spongepowered.api.text.TextRepresentable.class)
public interface MixinTextRepresentable extends TextRepresentable {

    @Shadow(prefix = "shadow$")
    org.spongepowered.api.text.Text shadow$toText();

    @Override
    default Text toText() {
        return (Text) shadow$toText();
    }

    @Shadow
    void applyTo(org.spongepowered.api.text.Text.Builder builder);

    @Override
    default void applyTo(Text.Builder builder) {
        applyTo((org.spongepowered.api.text.Text.Builder) builder);
    }
}
