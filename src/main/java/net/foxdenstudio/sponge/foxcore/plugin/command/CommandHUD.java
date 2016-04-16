package net.foxdenstudio.sponge.foxcore.plugin.command;

import com.google.common.collect.ImmutableList;
import net.foxdenstudio.sponge.foxcore.plugin.FCConfigManager;
import net.foxdenstudio.sponge.foxcore.plugin.command.util.AdvCmdParser;
import net.foxdenstudio.sponge.foxcore.plugin.util.CacheMap;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Fox on 4/14/2016.
 */
public class CommandHUD implements CommandCallable {

    private static CommandHUD instance;

    private Map<Player, Boolean> isHUDEnabled = new CacheMap<>((k, m) -> FCConfigManager.getInstance().isDefaultHUDOn());

    public CommandHUD() {
        if (instance == null) instance = this;
    }

    public static CommandHUD instance() {
        return instance;
    }

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        if (!testPermission(source)) {
            source.sendMessage(Text.of(TextColors.RED, "You don't have permission to use this command!"));
            return CommandResult.empty();
        }
        AdvCmdParser.ParseResult parse = AdvCmdParser.builder().arguments(arguments).parse();
        if (source instanceof Player) {
            if (parse.args.length == 0) {
                Text.Builder builder = Text.builder();
                builder.append(Text.of("Your HUD is currently "));
                if (isHUDEnabled.get(source)) builder.append(Text.of(TextColors.GREEN, "on"));
                else builder.append(Text.of(TextColors.RED, "off"));
                builder.append(Text.of(TextColors.RESET, "!"));
            } else {
                if (parse.args[0].equalsIgnoreCase("on")) {
                    isHUDEnabled.put((Player) source, true);
                } else if (parse.args[0].equalsIgnoreCase("off")) {
                    isHUDEnabled.put((Player) source, false);
                    if (Sponge.getServer().getServerScoreboard().isPresent()) {
                        ((Player) source).setScoreboard(Sponge.getServer().getServerScoreboard().get());
                    } else {
                        ((Player) source).setScoreboard(Scoreboard.builder().build());
                    }
                } else if (parse.args[0].equalsIgnoreCase("reset")) {
                    if (Sponge.getServer().getServerScoreboard().isPresent()) {
                        ((Player) source).setScoreboard(Sponge.getServer().getServerScoreboard().get());
                    } else {
                        ((Player) source).setScoreboard(Scoreboard.builder().build());
                    }
                }
            }
        } else source.sendMessage(Text.of("HUD controls are only available for players!"));
        return CommandResult.empty();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
        AdvCmdParser.ParseResult parse = AdvCmdParser.builder()
                .arguments(arguments)
                .autoCloseQuotes(true)
                .parse();
        if (parse.current.type == AdvCmdParser.CurrentElement.ElementType.ARGUMENT &&
                parse.current.index == 0)
            return ImmutableList.of("on", "off", "reset");
        else return ImmutableList.of();
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return source.hasPermission("foxcore.hud");
    }

    @Override
    public Optional<? extends Text> getShortDescription(CommandSource source) {
        return Optional.of(Text.of("Command for enabling and disabling the scoreboard HUD."));
    }

    @Override
    public Optional<? extends Text> getHelp(CommandSource source) {
        return Optional.empty();
    }

    @Override
    public Text getUsage(CommandSource source) {
        return Text.of("hud [on|off|reset]");
    }

    public Map<Player, Boolean> getIsHUDEnabled() {
        return isHUDEnabled;
    }
}
