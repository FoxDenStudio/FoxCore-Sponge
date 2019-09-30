package net.foxdenstudio.foxcore.sponge.impl;

import net.foxdenstudio.foxcore.platform.fox.text.TextFactory;
import net.foxdenstudio.foxcore.platform.text.Text;
import org.jline.terminal.TerminalBuilder;

public class SpongeTextFactory implements TextFactory {

    @SuppressWarnings("ConstantConditions")
    @Override
    public Text getText(String text) {
        return (Text) (Object) org.spongepowered.api.text.Text.of(text);
    }

    @Override
    public Text getText(Object... objects){
        return (Text) org.spongepowered.api.text.Text.of(objects);
    }

}
