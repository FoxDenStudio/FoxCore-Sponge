package net.foxdenstudio.sponge.foxcore.plugin.util;

import net.foxdenstudio.sponge.foxcore.common.network.server.packet.ServerPositionPacket;
import net.foxdenstudio.sponge.foxcore.common.util.FCCUtil;
import net.foxdenstudio.sponge.foxcore.plugin.FoxCoreMain;
import net.foxdenstudio.sponge.foxcore.plugin.state.FCStateManager;
import net.foxdenstudio.sponge.foxcore.plugin.state.PositionStateField;
import net.foxdenstudio.sponge.foxcore.plugin.wand.IWand;
import net.foxdenstudio.sponge.foxcore.plugin.wand.data.WandData;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.LoreData;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;
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

import static org.spongepowered.api.text.format.TextColors.*;
import static org.spongepowered.api.text.format.TextStyles.*;

/**
 * Created by Fox on 7/7/2016.
 */
public class FCPUtil {

    public static final TextColor[] COLORS = {BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY,
            DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE};

    public static Text readableBooleanText(boolean bool) {
        return bool ? Text.of(TextColors.GREEN, "True") : Text.of(TextColors.RED, "False");
    }

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
    public static List<Position> getPositions(CommandSource source) {
        return ((PositionStateField) FCStateManager.instance().getStateMap().get(source).getOrCreate(PositionStateField.ID).get()).getList();
    }

    public static void updatePositions(Player player) {
        FoxCoreMain.instance().getFoxcoreNetworkChannel().sendPacket(player, new ServerPositionPacket(getPositions(player)));
    }

    public static Optional<TextColor> textColorFromName(String name) {
        int code = FCCUtil.colorCodeFromName(name);
        if (code >= 0) return Optional.of(COLORS[code]);
        else return Optional.empty();
    }

    public static Optional<TextColor> textColorFromHex(String hex) {
        if (!hex.matches("[0-9a-f]")) return Optional.empty();
        int code = Integer.parseInt(hex, 16);
        return Optional.of(COLORS[code]);
    }

    public static Optional<TextStyle> textStyleFromCode(String code) {
        switch (code) {
            case "k":
                return Optional.of(OBFUSCATED);
            case "l":
                return Optional.of(BOLD);
            case "m":
                return Optional.of(STRIKETHROUGH);
            case "n":
                return Optional.of(UNDERLINE);
            case "o":
                return Optional.of(ITALIC);
            case "r":
                return Optional.of(TextStyles.RESET);
            default:
                return Optional.empty();
        }
    }

    public static String getColorName(TextColor color, boolean upperCase) {
        switch (color.getId()) {
            case "RED":
                return upperCase ? "Red" : "red";
            case "YELLOW":
                return upperCase ? "Yellow" : "yellow";
            case "GREEN":
                return upperCase ? "Green" : "green";
            case "AQUA":
                return upperCase ? "Aqua" : "aqua";
            case "BLUE":
                return upperCase ? "Blue" : "blue";
            case "LIGHT_PURPLE":
                return upperCase ? "Light purple" : "light purple";
            case "DARK_RED":
                return upperCase ? "Dark red" : "dark red";
            case "GOLD":
                return upperCase ? "Gold" : "gold";
            case "DARK_GREEN":
                return upperCase ? "Dark green" : "dark green";
            case "DARK_AQUA":
                return upperCase ? "Dark aqua" : "dark aqua";
            case "DARK_BLUE":
                return upperCase ? "Dark blue" : "dark blue";
            case "DARK_PURPLE":
                return upperCase ? "Dark purple" : "dark puple";
            case "WHITE":
                return upperCase ? "White" : "white";
            case "GRAY":
                return upperCase ? "Gray" : "gray";
            case "DARK_GRAY":
                return upperCase ? "Dark gray" : "dark gray";
            case "BLACK":
                return upperCase ? "Black" : "black";
            case "RESET":
                return upperCase ? "Reset" : "reset";
            default:
                return "What?";
        }
    }

    public static CommentedConfigurationNode getHOCONConfiguration(Path file, ConfigurationLoader<CommentedConfigurationNode> loader) {
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
                end = command.substring(matcher.end());
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

    public static void updateWandLore(ItemStack itemStack) {
        Optional<WandData> wandDataOptional = itemStack.get(WandData.class);
        if (wandDataOptional.isPresent()) {
            WandData wandData = wandDataOptional.get();
            updateWandLore(itemStack, wandData.getWand().get());
        }
    }

    public static void updateWandLore(ItemStack itemStack, IWand wand) {
        List<Text> wandLore = wand.getLore();
        LoreData loreData = Sponge.getDataManager().getManipulatorBuilder(LoreData.class).get().create();
        final ListValue<Text> lore = loreData.lore();
        if (wandLore != null) lore.addAll(wand.getLore());
        loreData.set(lore);
        itemStack.offer(loreData);
        itemStack.offer(Keys.DISPLAY_NAME, wand.getItemName());
    }

    /*private static final String[] STYLE_TO_FLATTEN = {"bold", "italic", "underlined", "strikethrough", "obfuscated", "color", "insertion", "clickEvent", "colorEvent"};

    private static Text smartTrim(Text text) {
        TextSerializer serializer = TextSerializers.JSON;

        String jsonString = serializer.serialize(text);

        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();


        return serializer.deserialize(jsonObject.toString());
    }

    private static void smartTrim(JsonObject json) {
        JsonArray extra = json.getAsJsonArray("extra");
        if (extra != null) {
            for (JsonElement child : extra) {
                if (child instanceof JsonObject) smartTrim((JsonObject) child);
            }
            for (String styleProp : STYLE_TO_FLATTEN) {
                int keyLength = styleProp.length() + 3;
                Multimap<JsonElement, JsonObject> map = HashMultimap.create();
                for (JsonElement child : extra) {
                    if (child instanceof JsonObject) {
                        JsonObject childObject = ((JsonObject) child);
                        JsonElement propValue = childObject.get(styleProp);
                        map.put(propValue, childObject);
                    }
                }
                JsonElement basePropValue = json.get(styleProp);
                JsonElement targetValue = basePropValue;
                int delta = 0;
                if (basePropValue != null) {
                    int baseEntryLength = keyLength + basePropValue.toString().length();
                    if()

                } else {
                    for (Map.Entry<JsonElement, Collection<JsonObject>> entry : map.asMap().entrySet()) {
                        Collection<JsonObject> objects = entry.getValue();
                        int size = objects.size();
                        if (size > 1) {
                            int newdelta = 0;
                            JsonElement element = entry.getKey();
                            int entryLength = keyLength + element.toString().length();
                            newdelta -= entryLength * (size - 1);
                            if (newdelta < delta){
                                newdelta = delta;
                                targetValue = element;
                            }
                        }
                    }
                }


            }

        }

    }*/
}
