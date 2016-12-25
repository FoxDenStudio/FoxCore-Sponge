package net.foxdenstudio.sponge.foxcore.plugin.command.misc;

import com.google.common.collect.ImmutableList;
import net.foxdenstudio.sponge.foxcore.plugin.command.FCCommandBase;
import net.foxdenstudio.sponge.foxcore.plugin.command.util.AdvCmdParser;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.*;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by Fox on 12/22/2016.
 */
public class CommandWhat extends FCCommandBase {

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        AdvCmdParser.ParseResult parse = AdvCmdParser.builder()
                .arguments(arguments)
                .parse();
        CommandManager manager = Sponge.getCommandManager();

        if (parse.args.length == 0) {
            source.sendMessage(Text.of(TextColors.GREEN, "Usage: ", TextColors.RESET, "/foxcore misc what <command>"));
        } else {
            String commandName = parse.args[0];
            Set<? extends CommandMapping> mappings = manager.getAll(commandName);
            if (mappings.size() > 0) {
                Text.Builder builder = Text.builder();
                builder.append(Text.of(TextColors.GOLD, "\n-----------------------------------------------------\n"));
                if (mappings.size() == 1) {
                    CommandMapping mapping = mappings.iterator().next();

                    generateText(mapping, builder, source, manager);

                    source.sendMessage(builder.build());
                } else {
                    Optional<? extends CommandMapping> primaryMappingOpt = manager.get(commandName);
                    Set<? extends CommandMapping> secondaryMappings = new HashSet<>(mappings);
                    if (primaryMappingOpt.isPresent()) {
                        CommandMapping primaryMapping = primaryMappingOpt.get();
                        secondaryMappings.remove(primaryMapping);

                        builder.append(Text.of(TextColors.GREEN, "------- Primary -------\n"));

                        generateText(primaryMapping, builder, source, manager);

                        builder.append(Text.of(TextColors.GREEN, "\n------- Secondary -------\n"));

                        Iterator<? extends CommandMapping> mappingIterator = secondaryMappings.iterator();

                        while (mappingIterator.hasNext()) {
                            CommandMapping mapping = mappingIterator.next();
                            generateText(mapping, builder, source, manager);
                            if (mappingIterator.hasNext()) {
                                builder.append(Text.of("\n\n"));
                            }
                        }
                    } else {
                        source.sendMessage(Text.of(TextColors.RED, "Something very strange happened. What the heck did you do?"));
                    }
                }
            } else {
                source.sendMessage(Text.of(TextColors.RED, "No command with this name: ", TextColors.RESET, commandName));
            }
        }
        return CommandResult.empty();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException {
        AdvCmdParser.ParseResult parse = AdvCmdParser.builder()
                .arguments(arguments)
                .autoCloseQuotes(true)
                .excludeCurrent(true)
                .parse();
        CommandManager manager = Sponge.getCommandManager();

        if (parse.current.type == AdvCmdParser.CurrentElement.ElementType.ARGUMENT) {
            if (parse.current.index == 0) {
                return manager.getSuggestions(source, parse.current.token, targetPosition);
            }
        } else if (parse.current.type == AdvCmdParser.CurrentElement.ElementType.COMPLETE) {
            return ImmutableList.of(parse.current.prefix + " ");
        }
        return ImmutableList.of();
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return source.hasPermission("foxcore.command.misc.what");
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.of(Text.of("Tells you information about any command."));
    }

    @Override
    public Text getUsage(CommandSource source) {
        return Text.of("<command>");
    }

    private String colTS(Collection col) {
        String str = "";
        Iterator it = col.iterator();
        while (it.hasNext()) {
            str += it.next().toString();
            if (it.hasNext()) str += ", ";
        }
        return str;
    }

    private void generateText(CommandMapping mapping, Text.Builder builder, CommandSource source, CommandManager manager) {
        String primaryAlias = mapping.getPrimaryAlias();
        builder.append(Text.of(TextColors.GOLD, "Primary Alias: ", TextColors.RESET, primaryAlias, "\n"));

        Set<String> secondaryAliases = new HashSet<>(mapping.getAllAliases());
        secondaryAliases.remove(primaryAlias);

        if (secondaryAliases.size() > 1) {
            builder.append(Text.of(TextColors.GREEN, "Secondary Aliases: "));
        } else {
            builder.append(Text.of(TextColors.GREEN, "Secondary Alias: "));
        }
        builder.append(Text.of(TextColors.RESET, colTS(secondaryAliases), "\n"));

        CommandCallable callable = mapping.getCallable();

        builder.append(Text.of(TextColors.AQUA, "Usage: "));
        builder.append(callable.getUsage(source));
        builder.append(Text.NEW_LINE);

        Optional<Text> descriptionOpt = callable.getShortDescription(source);
        if (descriptionOpt.isPresent()) {
            Text description = descriptionOpt.get();
            builder.append(Text.of(TextColors.AQUA, "Description: "));
            builder.append(description);
            builder.append(Text.NEW_LINE);
        }

        Optional<PluginContainer> containerOpt = manager.getOwner(mapping);

        if (containerOpt.isPresent()) {
            PluginContainer container = containerOpt.get();
            String id = container.getId();
            builder.append(Text.of(
                    TextActions.showText(Text.of("Click to show plugin details")),
                    TextActions.runCommand("/foxcore misc who " + id),
                    TextColors.LIGHT_PURPLE, "Plugin: ", TextColors.RESET, id)
            );
        } else {
            builder.append(Text.of(TextColors.LIGHT_PURPLE, "Plugin: ", TextColors.GRAY, TextStyles.ITALIC, "unknown"));
        }
    }


}
