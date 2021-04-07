/*
 * This file is part of FoxCore, licensed under the MIT License (MIT).
 *
 * Copyright (c) gravityfox - https://gravityfox.net/
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.foxdenstudio.foxsuite.foxcore.sponge;

import com.google.common.base.Stopwatch;
import com.google.inject.*;
import net.foxdenstudio.foxsuite.foxcore.FoxCore;
import net.foxdenstudio.foxsuite.foxcore.api.world.FoxWorldManager;
import net.foxdenstudio.foxsuite.foxcore.sponge.guice.module.FoxCoreSpongeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Plugin(id = "foxcore",
        name = "FoxCore",
        description = "Core plugin for Fox plugins. This plugin also contains some core functionality that other plugins may wish to use.",
        authors = {"gravityfox"},
        url = "https://github.com/FoxDenStudio/FoxCore"
)
public final class FoxCoreSpongePlugin {

    private static final UUID FOX_UUID = UUID.fromString("f275f223-1643-4fac-9fb8-44aaf5b4b371");
    private static final String FOX_APPENDER_NAME = "FoxFile";
    private static final String FOX_LOGGER_CONFIG_NAME = "fox";
    private static final String FOXCORE_LOGGER_NAME = "fox.core";
    private static FoxCoreSpongePlugin instance;

    private Logger logger = LoggerFactory.getLogger(FOXCORE_LOGGER_NAME);

    @Inject
    private Game game;

    @Inject
    private EventManager eventManager;

    @Inject
    @ConfigDir(sharedRoot = true)
    private Path configDirectory;

    private Path foxLogDirectory = Paths.get("logs", "fox");

    @Inject
    private PluginContainer container;

    @Inject
    private Injector pluginInjector;

    private Injector foxInjector;

    private FoxCore foxcore;

    public static FoxCoreSpongePlugin instance() {
        return instance;
    }

    @Listener
    public void construct(GameConstructionEvent event) {
        instance = this;
    }

    @Listener
    public void preInit(GamePreInitializationEvent event) {
        //logger.info("Injecting fox logger");
        //setupLogging();

        logger.info("Beginning FoxCore initialization");
        logger.info("Version: {}", container.getVersion().orElse("Unknown"));

        List<Module> modules = new ArrayList<>();

        modules.add(new FoxCoreSpongeModule());


        foxInjector = pluginInjector.createChildInjector(modules);

        printInjectorChain(foxInjector);

        logger.info("Injecting main FoxCore instance.");
        Stopwatch stopwatch = Stopwatch.createStarted();
        foxcore = foxInjector.getInstance(FoxCore.class);
        stopwatch.stop();
        logger.info("Injection took {} milliseconds.", stopwatch.elapsed(TimeUnit.MILLISECONDS));

        logger.info("Configuring commands");
        foxcore.configureCommands();

        logger.info("Setting up static content");
        foxcore.setupStaticContent();

        FoxWorldManager worldManager = foxcore.getWorldManager();
        logger.info("Loading FoxWorldManager world index data.");
        worldManager.load();

        logger.info("Registering world load listener");
        Sponge.getEventManager().registerListeners(this, worldManager);
    }

    @Listener
    public void init(GameInitializationEvent event) {
        logger.info("Registering commands");
        this.foxcore.registerCommands();

        logger.info("Loading persistent indices and their objects");
        this.foxcore.loadIndexObjects();
    }



    // This code doesn't work for Log4J 2.0-beta9
    // Will be used in MC 1.12.2 where Log4J is updated.
    /*private void setupLogging() {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        String foxLogFile = foxLogDirectory.resolve("fox-latest.log").toString();
        String foxLogPattern = foxLogDirectory.toString() + "/fox-%i.log";
        PatternLayout layout = PatternLayout.createLayout("[%d{HH:mm:ss}] [%t/%level] [%logger]: %msg%n", config, null, null, null);
        Appender appender = RollingRandomAccessFileAppender.createAppender(foxLogFile, foxLogPattern, null, FOX_APPENDER_NAME, null,
                OnStartupTriggeringPolicy.createPolicy(),
                DefaultRolloverStrategy.createStrategy("3", null, "max", "0", config),
                layout, null, "true", "false", null, config);
        appender.start();
        config.getAppenders().putIfAbsent(appender.getName(), appender);
        AppenderRef ref = AppenderRef.createAppenderRef(appender.getName(), null, null);
        AppenderRef[] refs = {ref};
        LoggerConfig loggerConfig = LoggerConfig.createLogger("true", "all", FOXCORE_LOGGER_NAME, null, refs, null, config, null);
        loggerConfig.addAppender(appender, null, null);
        config.getLoggers().putIfAbsent(loggerConfig.getName(), loggerConfig);
        ctx.updateLoggers(config);
    }*/

    /*@Listener
    public void setupNetworking(GameInitializationEvent event) {

    }*/

    @Listener
    public void registerListeners(GameInitializationEvent event) {
        //logger.info("Registering event listeners");
        EventManager manager = game.getEventManager();

    }

    @Listener
    public void onServerStopping(GameStoppingServerEvent event) {
        logger.info("Server is stopping! Saving states.");
        this.foxcore.getWorldManager().save();
    }

    /*@Listener
    public void registerData(GameInitializationEvent event) {
        //logger.info("Registering custom data manipulators");

    }*/

    /*@Listener
    public void configurePermissions(GamePostInitializationEvent event) {
        logger.info("Configuring permissions");
        game.getServiceManager().provide(PermissionService.class).get().getDefaults()
                .getTransientSubjectData().setPermission(SubjectData.GLOBAL_CONTEXT, "foxcore.command.info", Tristate.TRUE);
    }*/

    public Logger logger() {
        return logger;
    }

    public Game game() {
        return game;
    }

    @Listener
    public void playerJoin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();
        if (player.getUniqueId().equals(FOX_UUID)) {
            logger.info("A code fox has slipped into the server...");
        }
    }

    public Path getConfigDirectory() {
        return this.configDirectory;
    }

    public Path getLogDirectory() {
        return this.foxLogDirectory;
    }

    public PluginContainer getContainer() {
        return this.container;
    }

    public FoxCore getCommonInstance() {
        return this.foxcore;
    }

    //==================================================================================================================
    //====================================  PRIVATE METHODS  ===========================================================
    //==================================================================================================================

    private void printInjectorChain(Injector injector) {

        StringBuilder sb = new StringBuilder("\n======================= INJECTOR =======================\n");

        int count = 0;
        while (injector != null) {
            sb.append("COUNT: ").append(count++).append('\n');
            sb.append("BINDINGS:\n");
            Map<Key<?>, Binding<?>> bindings = injector.getBindings();
            for (Map.Entry<Key<?>, Binding<?>> entry : bindings.entrySet()) {
                sb.append(entry.getKey()).append('\n');
                sb.append("    ").append(entry.getValue()).append('\n');
                sb.append('\n');
            }
            sb.append('\n');
            injector = injector.getParent();
        }

        sb.append("========================================================");
        System.out.println(sb);
    }
}