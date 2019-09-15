package net.foxdenstudio.foxcore.sponge.impl;

import net.foxdenstudio.foxcore.platform.fox.text.TextFactory;
import net.foxdenstudio.foxcore.platform.text.Text;

public class SpongeTextFactory implements TextFactory {
    @Override
    public Text getText(String text) {
        return (Text) (Object) org.spongepowered.api.text.Text.of(text);
    }
}
