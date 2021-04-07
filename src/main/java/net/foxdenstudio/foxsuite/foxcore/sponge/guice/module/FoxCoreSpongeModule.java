package net.foxdenstudio.foxsuite.foxcore.sponge.guice.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import net.foxdenstudio.foxsuite.foxcore.api.annotation.FoxCorePluginInstance;
import net.foxdenstudio.foxsuite.foxcore.api.world.FoxWorldManager;
import net.foxdenstudio.foxsuite.foxcore.guice.module.FoxCoreModule;
import net.foxdenstudio.foxsuite.foxcore.platform.command.PlatformCommandManager;
import net.foxdenstudio.foxsuite.foxcore.platform.command.source.ConsoleSource;
import net.foxdenstudio.foxsuite.foxcore.platform.fox.text.TextFactory;
import net.foxdenstudio.foxsuite.foxcore.sponge.impl.SpongeTextFactory;
import net.foxdenstudio.foxsuite.foxcore.sponge.impl.world.SpongeFoxWorldManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;

import javax.annotation.Nullable;

public class FoxCoreSpongeModule extends AbstractModule {
    protected void configure() {
        install(new FoxCoreModule());
        install(new TextStyleModule());
        bind(TextFactory.class).to(SpongeTextFactory.class);
        bind(FoxWorldManager.class).to(SpongeFoxWorldManager.class);

    }

    @Provides
    PlatformCommandManager provideFoxCommandManager() {
        return (PlatformCommandManager) Sponge.getCommandManager();
    }

    @Provides
    ConsoleSource getConsoleSource() {
        try {
            return (ConsoleSource) Sponge.getServer().getConsole();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    @Provides
    @FoxCorePluginInstance
    Object getPluginInstance(PluginContainer container) {
        return container.getInstance().orElse(null);
    }
}
