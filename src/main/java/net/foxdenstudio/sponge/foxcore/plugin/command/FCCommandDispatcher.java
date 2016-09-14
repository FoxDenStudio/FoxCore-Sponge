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

package net.foxdenstudio.sponge.foxcore.plugin.command;

import com.google.common.collect.*;
import net.foxdenstudio.sponge.foxcore.plugin.command.util.AdvCmdParser;
import org.spongepowered.api.command.*;
import org.spongepowered.api.command.dispatcher.Disambiguator;
import org.spongepowered.api.command.dispatcher.Dispatcher;
import org.spongepowered.api.command.dispatcher.SimpleDispatcher;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.GuavaCollectors;
import org.spongepowered.api.util.StartsWithPredicate;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.spongepowered.api.command.CommandMessageFormatting.SPACE_TEXT;

public class FCCommandDispatcher extends FCCommandBase implements Dispatcher {

    protected final Disambiguator disambiguator;
    protected final ListMultimap<String, CommandMapping> commands = ArrayListMultimap.create();
    protected String dispatcherPrefix;
    protected Text shortDescription;

    public FCCommandDispatcher(String dispatcherPrefix, String shortDescription, Disambiguator
            disambiguator) {
        this.dispatcherPrefix = dispatcherPrefix;
        if (shortDescription == null || shortDescription.isEmpty()) {
            this.shortDescription = null;
        } else {
            this.shortDescription = Text.of(shortDescription);
        }
        this.disambiguator = disambiguator;
    }

    public FCCommandDispatcher(String dispatcherPrefix, String shortDescription) {
        this(dispatcherPrefix, shortDescription, SimpleDispatcher.FIRST_DISAMBIGUATOR);
    }

    public FCCommandDispatcher(String dispatcherPrefix) {
        this(dispatcherPrefix, null, SimpleDispatcher.FIRST_DISAMBIGUATOR);
    }

    public Optional<CommandMapping> register(CommandCallable callable, String... alias) {
        checkNotNull(alias, "alias");
        return register(callable, Arrays.asList(alias));
    }

    public Optional<CommandMapping> register(CommandCallable callable, List<String> aliases) {
        checkNotNull(aliases, "aliases");
        checkNotNull(callable, "callable");

        if (!aliases.isEmpty()) {
            String primary = aliases.get(0);
            List<String> secondary = aliases.subList(1, aliases.size());
            CommandMapping mapping = new ImmutableCommandMapping(callable, primary, secondary);

            for (String alias : aliases) {
                this.commands.put(alias.toLowerCase(), mapping);
            }

            return Optional.of(mapping);
        } else {
            return Optional.empty();
        }
    }

    public Optional<CommandMapping> register(CommandCallable callable, String primaryAlias,
                                             List<String> secondaryAliases) {
        checkNotNull(primaryAlias, "aliases");
        checkNotNull(callable, "callable");

        if (!primaryAlias.isEmpty()) {
            if (secondaryAliases == null) secondaryAliases = new ArrayList<>();
            CommandMapping mapping = new ImmutableCommandMapping(callable, primaryAlias,
                    secondaryAliases);

            this.commands.put(primaryAlias.toLowerCase(), mapping);
            for (String alias : secondaryAliases) {
                this.commands.put(alias.toLowerCase(), mapping);
            }

            return Optional.of(mapping);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Set<? extends CommandMapping> getCommands() {
        return ImmutableSet.copyOf(this.commands.values());
    }

    @Override
    public Set<String> getPrimaryAliases() {
        Set<String> aliases = this.commands.values().stream().map
                (CommandMapping::getPrimaryAlias).collect(Collectors.toSet());
        return Collections.unmodifiableSet(aliases);
    }

    @Override
    public Set<String> getAliases() {
        Set<String> aliases = new HashSet<>();

        for (CommandMapping mapping : this.commands.values()) {
            aliases.addAll(mapping.getAllAliases());
        }

        return Collections.unmodifiableSet(aliases);
    }

    @Override
    public Optional<? extends CommandMapping> get(String alias) {
        return this.get(alias, null);
    }

    public Optional<CommandMapping> get(String alias, CommandSource source) {
        List<CommandMapping> results = this.commands.get(alias.toLowerCase());
        if (results.size() == 1) {
            return Optional.of(results.get(0));
        } else if (results.size() == 0 || source == null) {
            return Optional.empty();
        } else {
            return this.disambiguator.disambiguate(source, alias, results);
        }
    }

    @Override
    public Set<? extends CommandMapping> getAll(String alias) {
        return ImmutableSet.copyOf(this.commands.get(alias));
    }

    @Override
    public Multimap<String, CommandMapping> getAll() {
        return ImmutableMultimap.copyOf(this.commands);
    }

    @Override
    public boolean containsAlias(String alias) {
        return this.commands.containsKey(alias.toLowerCase());
    }

    @Override
    public boolean containsMapping(CommandMapping mapping) {
        checkNotNull(mapping, "mapping");
        for (CommandMapping test : this.commands.values()) {
            if (mapping.equals(test)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public CommandResult process(CommandSource source, String inputArguments) throws
            CommandException {
        if (!testPermission(source)) {
            source.sendMessage(Text.of(TextColors.RED, "You don't have permission to use this " +
                    "command!"));
            return CommandResult.empty();
        }
        if (!inputArguments.isEmpty()) {
            inputArguments = inputArguments.trim();
            String[] args = inputArguments.split(" +", 2);
            if (args[0].equalsIgnoreCase("help")) {
                //args = inputArguments.split(" +", 2);
                if (args.length > 1) {
                    final Optional<CommandMapping> optCommand = get(args[1], source);
                    if (!optCommand.isPresent()) {
                        source.sendMessage(Text.of("That command doesn't exist!"));
                        return CommandResult.empty();
                    }
                    CommandCallable command = optCommand.get().getCallable();
                    if (!command.testPermission(source)) {
                        source.sendMessage(Text.of(TextColors.RED, "You don't have permission to " +
                                "view help for this command!"));
                        return CommandResult.empty();
                    }
                    @SuppressWarnings("unchecked")
                    final Optional<Text> helpText = (Optional<Text>) command.getHelp(source);
                    Text.Builder builder = Text.builder();
                    if (helpText.isPresent())
                        builder.append(Text.of(TextColors.GREEN, "----------"),
                                Text.of(TextColors.GOLD, "Command \""),
                                Text.of(TextColors.GOLD, optCommand.get().getPrimaryAlias()),
                                Text.of(TextColors.GOLD, "\" Help"),
                                Text.of(TextColors.GREEN, "----------\n"));
                    source.sendMessage(builder.append(helpText.orElse(Text.of("Usage: " +
                            dispatcherPrefix + " ").toBuilder().append(command.getUsage(source))
                            .build())).build());
                    return CommandResult.empty();
                } else {
                    source.sendMessage(this.getHelp(source).get());
                    return CommandResult.empty();
                }
            } else {
                final Optional<CommandMapping> cmdOptional = get(args[0], source);
                if (!cmdOptional.isPresent())
                    throw new CommandNotFoundException(Text.of("Command not found!"), args[0]);

                final String arguments = args.length > 1 ? args[1] : "";
                final CommandCallable command = cmdOptional.get().getCallable();
                try {
                    return command.process(source, arguments);
                } catch (CommandNotFoundException e) {
                    throw new CommandException(Text.of("No such child command: %s" + e.getCommand
                            ()));
                } catch (CommandException e) {
                    Text text = e.getText();
                    if (text == null)
                        text = Text.of("There was an error processing command: " + args[0]);
                    source.sendMessage(text.toBuilder().color(TextColors.RED).build());
                    source.sendMessage(Text.of("Usage: " + dispatcherPrefix + " ").toBuilder()
                            .append(command.getUsage(source)).color(TextColors.RED).build());
                    return CommandResult.empty();
                }
            }
        } else {
            source.sendMessage(Text.builder()
                    .append(Text.of(TextColors.GREEN, "Usage: "))
                    .append(getUsage(source))
                    .build());
            return CommandResult.empty();
        }
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws
            CommandException {
        AdvCmdParser.ParseResult parse = AdvCmdParser.builder()
                .arguments(arguments)
                .limit(1)
                .leaveFinalAsIs(true)
                .autoCloseQuotes(true)
                .parseLastFlags(false)
                .parse();
        if (parse.current.type == AdvCmdParser.CurrentElement.ElementType.ARGUMENT) {
            if (parse.current.index == 0) {
                List<String> potentialCommands = filterCommandMappings(source).stream()
                        .map(CommandMapping::getPrimaryAlias)
                        .collect(Collectors.toList());
                potentialCommands.add("help");
                return potentialCommands.stream()
                        .filter(new StartsWithPredicate(parse.current.token))
                        .map(args -> parse.current.prefix + args)
                        .collect(GuavaCollectors.toImmutableList());
            } else return ImmutableList.of();
        } else if (parse.current.type == AdvCmdParser.CurrentElement.ElementType.FINAL) {
            String commandString = parse.args[0];
            if (commandString.equals("help")) {
                return filterCommandMappings(source).stream()
                        .map(CommandMapping::getPrimaryAlias)
                        .filter(new StartsWithPredicate(parse.current.token))
                        .map(args -> parse.current.prefix + args)
                        .collect(GuavaCollectors.toImmutableList());
            } else {
                Optional<CommandMapping> cmdOptional = get(parse.args[0], source);
                if (!cmdOptional.isPresent()) {
                    return ImmutableList.of();
                } else return cmdOptional.get().getCallable()
                        .getSuggestions(source, parse.args.length > 1 ? parse.args[1] : "", targetPosition)
                        .stream()
                        .map(args -> parse.current.prefix + args)
                        .collect(GuavaCollectors.toImmutableList());
            }
        } else if (parse.current.type == AdvCmdParser.CurrentElement.ElementType.COMPLETE) {
            return ImmutableList.of(parse.current.prefix + " ");
        } else return ImmutableList.of();
    }

    private Set<String> filterCommands(final CommandSource src) {
        return Multimaps.filterValues(this.commands, input -> input.getCallable().testPermission
                (src)).keys().elementSet();
    }

    private Set<CommandMapping> filterCommandMappings(final CommandSource src) {
        return new HashSet<>(
                Multimaps.filterValues(this.commands, input -> input.getCallable().testPermission
                        (src)).values());
    }

    @Override
    public boolean testPermission(CommandSource source) {
        for (CommandMapping mapping : this.commands.values()) {
            if (mapping.getCallable().testPermission(source)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.ofNullable(shortDescription);
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        if (this.commands.isEmpty()) {
            return Optional.empty();
        }
        Text.Builder build = Text.of(TextColors.GREEN, "Available commands:\n").toBuilder();
        for (Iterator<CommandMapping> it = filterCommandMappings(source).iterator(); it.hasNext()
                ; ) {

            CommandMapping mapping = it.next();
            @SuppressWarnings("unchecked")
            final Optional<Text> description = (Optional<Text>) mapping.getCallable()
                    .getShortDescription(source);
            build.append(Text.builder(mapping.getPrimaryAlias() + ":")
                            .color(TextColors.GOLD)
                            .onClick(TextActions.suggestCommand(this.dispatcherPrefix + " " +
                                    mapping.getPrimaryAlias())).build(),
                    SPACE_TEXT, description.orElse(mapping.getCallable().getUsage(source)));
            if (it.hasNext()) {
                build.append(Text.NEW_LINE);
            }
        }
        return Optional.of(build.build());
    }

    @Override
    public Text getUsage(CommandSource source) {
        final Text.Builder build = Text.builder();
        List<String> commands = filterCommands(source).stream()
                .filter(input -> {
                    if (input == null) {
                        return false;
                    }
                    final Optional<CommandMapping> ret = get(input, source);
                    return ret.isPresent() && ret.get().getPrimaryAlias().equals(input);
                })
                .collect(Collectors.toList());

        for (Iterator<String> it = commands.iterator(); it.hasNext(); ) {
            build.append(Text.of(it.next()));
            if (it.hasNext()) {
                build.append(Text.of(" | "));
            }
        }
        return build.build();
    }

    protected class CommandHelp extends FCCommandBase {

        @Override
        public CommandResult process(CommandSource source, String arguments) throws CommandException {
            return super.process(source, arguments);
        }

        @Override
        public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException {
            return super.getSuggestions(source, arguments, targetPosition);
        }

        @Override
        public boolean testPermission(CommandSource source) {
            return super.testPermission(source);
        }

        @Override
        public Optional<Text> getShortDescription(CommandSource source) {
            return super.getShortDescription(source);
        }

        @Override
        public Optional<Text> getHelp(CommandSource source) {
            return super.getHelp(source);
        }

        @Override
        public Text getUsage(CommandSource source) {
            return super.getUsage(source);
        }

    }

}
