package net.foxdenstudio.sponge.foxcore.plugin.command.misc;

import com.google.common.collect.ImmutableList;
import net.foxdenstudio.sponge.foxcore.plugin.command.FCCommandBase;
import net.foxdenstudio.sponge.foxcore.plugin.command.util.AdvCmdParser;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.GuavaCollectors;
import org.spongepowered.api.util.StartsWithPredicate;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Created by Fox on 12/24/2016.
 */
public class CommandWho extends FCCommandBase {

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        AdvCmdParser.ParseResult parse = AdvCmdParser.builder()
                .arguments(arguments)
                .parse();

        PluginManager manager = Sponge.getPluginManager();

        if (parse.args.length == 0) {
            source.sendMessage(Text.of(TextColors.GREEN, "Usage: ", TextColors.RESET, "/foxcore misc who <plugin>"));
        } else {
            String pluginId = parse.args[0];

            Optional<PluginContainer> containerOpt = manager.getPlugin(pluginId);

            if (containerOpt.isPresent()) {
                PluginContainer container = containerOpt.get();
                Text.Builder builder = Text.builder();
                builder.append(Text.of(TextColors.GOLD, "\n-----------------------------------------------------\n"));
                builder.append(Text.of(TextColors.GREEN, "Id: ", TextColors.RESET, container.getId(), "\n"));
                builder.append(Text.of(TextColors.GREEN, "Name: ", TextColors.RESET, container.getName()));

                container.getVersion().ifPresent(s -> builder.append(Text.of(TextColors.GREEN, "\nVersion: ", TextColors.RESET, s)));

                List<String> authors = container.getAuthors();
                if (!authors.isEmpty()) {
                    if (authors.size() > 1) {
                        builder.append(Text.of(TextColors.AQUA, "\nAuthors: "));
                    } else {
                        builder.append(Text.of(TextColors.AQUA, "\nAuthor: "));
                    }
                    builder.append(Text.of(TextColors.RESET, colTS(authors)));
                }

                container.getDescription().ifPresent(s -> builder.append(Text.of(TextColors.AQUA, "\nDescription: ", TextColors.RESET, s)));
                container.getUrl().ifPresent(s -> builder.append(Text.of(TextColors.AQUA, "\nURL: ", TextColors.RESET, s)));
                container.getSource().ifPresent(p -> builder.append(Text.of(TextColors.LIGHT_PURPLE, "\nFile: ", TextColors.RESET, p.getFileName())));
                container.getInstance().ifPresent(i -> builder.append(Text.of(TextColors.LIGHT_PURPLE, "\nMain Class: ", TextColors.RESET, i.getClass().getName())));

                source.sendMessage(builder.build());
            } else {
                source.sendMessage(Text.of(TextColors.RED, "No plugin with this id: ", TextColors.RESET, pluginId));
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
        PluginManager manager = Sponge.getPluginManager();

        if (parse.current.type == AdvCmdParser.CurrentElement.ElementType.ARGUMENT) {
            if (parse.current.index == 0) {
                return manager.getPlugins().stream()
                        .map(PluginContainer::getId)
                        .filter(new StartsWithPredicate(parse.current.token))
                        .map(args -> parse.current.prefix + args)
                        .collect(GuavaCollectors.toImmutableList());
            }
        } else if (parse.current.type == AdvCmdParser.CurrentElement.ElementType.COMPLETE) {
            return ImmutableList.of(parse.current.prefix + " ");
        }
        return ImmutableList.of();
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return source.hasPermission("foxcore.command.misc.who");
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.of(Text.of("Tells you information about any plugin."));
    }

    @Override
    public Text getUsage(CommandSource source) {
        return Text.of("<plugin>");
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

}
