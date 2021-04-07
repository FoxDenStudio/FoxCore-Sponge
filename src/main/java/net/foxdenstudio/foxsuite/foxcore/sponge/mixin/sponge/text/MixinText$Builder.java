package net.foxdenstudio.foxsuite.foxcore.sponge.mixin.sponge.text;

import net.foxdenstudio.foxsuite.foxcore.platform.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(org.spongepowered.api.text.Text.Builder.class)
public abstract class MixinText$Builder implements Text.Builder {

    @Shadow(prefix = "shadow$")
    public abstract org.spongepowered.api.text.Text.Builder shadow$append(org.spongepowered.api.text.Text... children);

    @Override
    public Text.Builder append(Text... children) {
        org.spongepowered.api.text.Text[] array = new org.spongepowered.api.text.Text[children.length];
        for (int i = 0; i < children.length; i++) {
            array[i] = (org.spongepowered.api.text.Text) children[i];
        }
        return (Text.Builder) shadow$append(array);
    }

    @Shadow(prefix = "shadow$")
    public abstract org.spongepowered.api.text.Text shadow$build();

    @Override
    public Text build() {
        return (Text) shadow$build();
    }
}
