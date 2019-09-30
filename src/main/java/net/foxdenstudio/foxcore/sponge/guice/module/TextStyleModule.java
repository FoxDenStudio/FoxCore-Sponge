package net.foxdenstudio.foxcore.sponge.guice.module;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import net.foxdenstudio.foxcore.platform.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

public class TextStyleModule extends AbstractModule {
    protected void configure() {
        //add configuration logic here

        bind(TextColor.class).annotatedWith(Names.named("black")).toInstance((TextColor) TextColors.BLACK);
        bind(TextColor.class).annotatedWith(Names.named("dark_blue")).toInstance((TextColor) TextColors.DARK_BLUE);
        bind(TextColor.class).annotatedWith(Names.named("dark_green")).toInstance((TextColor) TextColors.DARK_GREEN);
        bind(TextColor.class).annotatedWith(Names.named("dark_aqua")).toInstance((TextColor) TextColors.DARK_AQUA);
        bind(TextColor.class).annotatedWith(Names.named("dark_red")).toInstance((TextColor) TextColors.DARK_RED);
        bind(TextColor.class).annotatedWith(Names.named("dark_purple")).toInstance((TextColor) TextColors.DARK_PURPLE);
        bind(TextColor.class).annotatedWith(Names.named("gold")).toInstance((TextColor) TextColors.GOLD);
        bind(TextColor.class).annotatedWith(Names.named("gray")).toInstance((TextColor) TextColors.GRAY);
        bind(TextColor.class).annotatedWith(Names.named("dark_gray")).toInstance((TextColor) TextColors.DARK_GRAY);
        bind(TextColor.class).annotatedWith(Names.named("blue")).toInstance((TextColor) TextColors.BLUE);
        bind(TextColor.class).annotatedWith(Names.named("green")).toInstance((TextColor) TextColors.GREEN);
        bind(TextColor.class).annotatedWith(Names.named("aqua")).toInstance((TextColor) TextColors.AQUA);
        bind(TextColor.class).annotatedWith(Names.named("red")).toInstance((TextColor) TextColors.RED);
        bind(TextColor.class).annotatedWith(Names.named("light_purple")).toInstance((TextColor) TextColors.LIGHT_PURPLE);
        bind(TextColor.class).annotatedWith(Names.named("yellow")).toInstance((TextColor) TextColors.YELLOW);
        bind(TextColor.class).annotatedWith(Names.named("white")).toInstance((TextColor) TextColors.WHITE);

        bind(TextColor.class).annotatedWith(Names.named("reset")).toInstance((TextColor) TextColors.RESET);
        bind(TextColor.class).annotatedWith(Names.named("none")).toInstance((TextColor) TextColors.NONE);
    }
}
