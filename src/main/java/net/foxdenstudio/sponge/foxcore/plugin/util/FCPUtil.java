package net.foxdenstudio.sponge.foxcore.plugin.util;

import com.flowpowered.math.vector.Vector3i;
import net.foxdenstudio.sponge.foxcore.common.network.server.ServerPositionPacket;
import net.foxdenstudio.sponge.foxcore.common.util.FCCUtil;
import net.foxdenstudio.sponge.foxcore.plugin.network.FCServerNetworkManager;
import net.foxdenstudio.sponge.foxcore.plugin.state.FCStateManager;
import net.foxdenstudio.sponge.foxcore.plugin.state.PositionStateField;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tristate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Fox on 7/7/2016.
 */
public class FCPUtil {
    public static String readableTristate(Tristate state) {
        switch (state) {
            case UNDEFINED:
                return "Undefined";
            case TRUE:
                return "True";
            case FALSE:
                return "False";
            default:
                return "Wait wat?";
        }
    }

    public static Text readableTristateText(Tristate state) {
        switch (state) {
            case UNDEFINED:
                return Text.of(TextColors.YELLOW, "Undefined");
            case TRUE:
                return Text.of(TextColors.GREEN, "True");
            case FALSE:
                return Text.of(TextColors.RED, "False");
            default:
                return Text.of(TextColors.LIGHT_PURPLE, "Wait wat?");
        }
    }

    public static boolean isUserInCollection(Collection<User> collection, User user) {
        //System.out.println(user.getUniqueId());
        for (User u : collection) {
            //System.out.println(u.getUniqueId());
            if (u.getUniqueId().equals(user.getUniqueId())) return true;
        }
        return false;
    }

    public static boolean isUserInCollection(Collection<User> collection, UUID user) {
        //System.out.println(user.getUniqueId());
        for (User u : collection) {
            //System.out.println(u.getUniqueId());
            if (u.getUniqueId().equals(user)) return true;
        }
        return false;
    }

    public static boolean hasColor(Text text) {
        if (text.getColor() != TextColors.NONE && text.getColor() != TextColors.RESET) return true;
        for (Text child : text.getChildren()) {
            if (hasColor(child)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static List<Vector3i> getPositions(CommandSource source) {
        return ((PositionStateField) FCStateManager.instance().getStateMap().get(source).getOrCreate(PositionStateField.ID).get()).getList();
    }

    public static void updatePositions(Player player) {
        FCServerNetworkManager.instance().sendPacket(player, new ServerPositionPacket(getPositions(player)));
    }

    public static Optional<TextColor> textColorFromName(String name) {
        int code = FCCUtil.colorCodeFromName(name);
        if (code >= 0) return Optional.of(FCCUtil.colors[code]);
        else return Optional.empty();
    }

    public static Optional<TextColor> textColorFromHex(String hex) {
        if (!hex.matches("[0-9a-f]")) return Optional.empty();
        int code = Integer.parseInt(hex, 16);
        return Optional.of(FCCUtil.colors[code]);
    }

    public static CommentedConfigurationNode getHOCONConfiguration(Path file, ConfigurationLoader<CommentedConfigurationNode> loader){
        CommentedConfigurationNode root;
        if (Files.exists(file)) {
            try {
                root = loader.load();
            } catch (IOException e) {
                root = loader.createEmptyNode(ConfigurationOptions.defaults());
            }
        } else {
            root = loader.createEmptyNode(ConfigurationOptions.defaults());
        }
        return root;
    }

    public static Text pageFooter(int currentPage, int maxPage, String command, String finalString) {
        Pattern pattern = Pattern.compile("--[\\w]+[:=]([\"'])(?:\\\\.|[^\\\\])*?\\1|--[\\w]+[:=][\\w]*");
        Matcher matcher = pattern.matcher(command);
        String start = null, end = null;
        while (matcher.find()) {
            String match = matcher.group();
            String key = match.split("[:=]")[0].substring(2);
            if (Aliases.isIn(Aliases.PAGE_ALIASES, key)) {
                start = command.substring(0, matcher.start());
                if (matcher.end() == command.length()) end = "";
                else end = command.substring(matcher.end(), command.length() - 1);
                break;
            }
        }
        if (start == null) {
            start = command;
        }
        if (end == null) {
            if (finalString != null) end = finalString;
            else end = "";
        }
        Text space = Text.of(TextColors.RESET, " ");
        Text.Builder builder = Text.builder();
        builder.append(Text.of(TextColors.GREEN, "-------"));
        builder.append(space);
        if (currentPage != 1) {
            builder.append(Text.of(
                    TextColors.LIGHT_PURPLE,
                    TextActions.showText(Text.of("First Page")),
                    TextActions.runCommand(start + " --p=1 " + end),
                    "<<"));
            builder.append(space);
            builder.append(Text.of(
                    TextColors.AQUA,
                    TextActions.showText(Text.of("Previous Page")),
                    TextActions.runCommand(start + " --p=" + (currentPage - 1) + " " + end),
                    "<"));
        } else {
            builder.append(Text.of(TextColors.GRAY, "<< <"));
        }
        builder.append(space);
        int digits = Integer.toString(maxPage).length();
        builder.append(Text.of(TextColors.YELLOW, String.format("%0" + digits + "d", currentPage)));
        builder.append(Text.of(TextColors.GREEN, " / "));
        builder.append(Text.of(TextColors.GOLD, maxPage));
        builder.append(space);
        if (currentPage != maxPage) {
            builder.append(Text.of(
                    TextColors.AQUA,
                    TextActions.showText(Text.of("Next Page")),
                    TextActions.runCommand(start + " --p=" + (currentPage + 1) + " " + end),
                    ">"));
            builder.append(space);
            builder.append(Text.of(
                    TextColors.LIGHT_PURPLE,
                    TextActions.showText(Text.of("Last Page")),
                    TextActions.runCommand(start + " --p=" + maxPage + " " + end),
                    ">>"));
        } else {
            builder.append(Text.of(TextColors.GRAY, "> >>"));
        }
        builder.append(space);
        builder.append(Text.of(TextColors.GREEN, "-------"));
        return builder.build();
    }
}
