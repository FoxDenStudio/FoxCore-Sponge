package net.foxdenstudio.foxsuite.foxcore.sponge.impl;

import net.foxdenstudio.foxsuite.foxcore.platform.fox.text.TextFactory;
import net.foxdenstudio.foxsuite.foxcore.platform.text.Text;

public class SpongeTextFactory implements TextFactory {

    @SuppressWarnings("ConstantConditions")
    @Override
    public Text of(String text) {
        return (Text) (Object) org.spongepowered.api.text.Text.of(text);
    }

    @Override
    public Text of(Object... objects){
        return (Text) org.spongepowered.api.text.Text.of(objects);
    }

    @Override
    public Text.Builder builder() {
        return (Text.Builder) org.spongepowered.api.text.Text.builder();
    }

}
