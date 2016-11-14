package net.foxdenstudio.sponge.foxcore.plugin.wand.types;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;
import net.foxdenstudio.sponge.foxcore.common.util.FCCUtil;
import net.foxdenstudio.sponge.foxcore.plugin.command.util.AdvCmdParser;
import net.foxdenstudio.sponge.foxcore.plugin.command.util.ProcessResult;
import net.foxdenstudio.sponge.foxcore.plugin.state.FCStateManager;
import net.foxdenstudio.sponge.foxcore.plugin.util.FCPUtil;
import net.foxdenstudio.sponge.foxcore.plugin.util.Position;
import net.foxdenstudio.sponge.foxcore.plugin.wand.IWand;
import net.foxdenstudio.sponge.foxcore.plugin.wand.IWandFactory;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.GuavaCollectors;
import org.spongepowered.api.util.StartsWithPredicate;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.spongepowered.api.text.format.TextColors.*;

/**
 * Created by Fox on 4/30/2016.
 */
public class PositionWand implements IWand {

    public static final String TYPE = "position";

    public static final DataQuery COLOR_QUERY = DataQuery.of("color");
    public static final DataQuery RAINBOW_QUERY = DataQuery.of("rainbow");

    private Position.Color color;
    private boolean rainbow;

    public PositionWand() {
        color = Position.Color.WHITE;
        rainbow = false;
    }

    public PositionWand(Position.Color color, boolean rainbow) {
        this.color = color == null ? Position.Color.WHITE : color;
        this.rainbow = rainbow;
    }


    @Override
    public boolean leftClickAir(Player player) {
        return false;
    }

    @Override
    public boolean rightClickAir(Player player) {
        return false;
    }

    @Override
    public boolean leftClickBlock(Player player, BlockSnapshot block) {
        Vector3i vec = block.getPosition();
        List<Position> positions = FCPUtil.getPositions(player);
        Vector3i pos;
        if (rainbow) pos = vec;
        else pos = new Position(vec, color);
        if (positions.contains(pos)) {
            positions.remove(positions.lastIndexOf(pos));
            player.sendMessage(Text.of(TextColors.GREEN, "Successfully removed position (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")!"));
            FCPUtil.updatePositions(player);
            FCStateManager.instance().getStateMap().get(player).updateScoreboard();
        }
        return true;
    }

    @Override
    public boolean rightClickBlock(Player player, BlockSnapshot block) {
        Vector3i pos = block.getPosition();
        List<Position> positions = FCPUtil.getPositions(player);
        positions.add(new Position(pos, rainbow ? Position.Color.randomColor() : color));
        player.sendMessage(Text.of(TextColors.GREEN, "Successfully added position (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")!"));
        FCPUtil.updatePositions(player);
        FCStateManager.instance().getStateMap().get(player).updateScoreboard();
        return true;
    }

    @Override
    public boolean leftClickEntity(Player player, Entity entity) {
        return false;
    }

    @Override
    public boolean rightClickEntity(Player player, Entity entity) {
        return false;
    }

    @Override
    public ProcessResult modify(CommandSource source, String arguments) throws CommandException {
        return ProcessResult.failure();
    }

    @Override
    public Text getItemName() {
        return Text.of("Position Wand");
    }

    @Override
    public List<Text> getLore() {
        return ImmutableList.of(getItemName(), getColorText());
    }

    private Text getColorText(){
        if(rainbow){
            return Text.of("Color: ",
                    RED, "R",
                    GOLD, "a",
                    YELLOW, "i",
                    GREEN, "n",
                    AQUA, "b",
                    BLUE, "o",
                    LIGHT_PURPLE, "w");
        } else return Text.of("Color: ", color.getTextColor(), FCCUtil.toCapitalCase(color.name()));
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return new MemoryDataContainer()
                .set(COLOR_QUERY, color.name())
                .set(RAINBOW_QUERY, rainbow);
    }

    public static class Factory implements IWandFactory {

        private static final String[] aliases = {"p", "pos", "position"};

        @Override
        public IWand create(String arguments, @Nullable CommandSource source, @Nullable Location<World> targetPosition) throws CommandException {
            AdvCmdParser.ParseResult parse = AdvCmdParser.builder()
                    .arguments(arguments)
                    .parse();
            Position.Color color = Position.Color.WHITE;
            boolean rainbow = false;
            if (parse.args.length > 0) {
                String colorName = parse.args[0];
                if (colorName.equalsIgnoreCase("rainbow")) {
                    rainbow = true;
                } else {
                    Position.Color color1 = Position.Color.from(colorName);
                    if (color1 != null) color = color1;
                }
            }
            return new PositionWand(color, rainbow);
        }

        @Override
        public List<String> createSuggestions(String arguments, CommandSource source, @Nullable Location<World> targetPosition) throws CommandException {
            AdvCmdParser.ParseResult parse = AdvCmdParser.builder()
                    .arguments(arguments)
                    .excludeCurrent(true)
                    .autoCloseQuotes(true)
                    .parse();
            if (parse.current.type == AdvCmdParser.CurrentElement.ElementType.ARGUMENT) {
                if (parse.current.index == 0) {
                    List<String> list = Arrays.stream(Position.Color.values())
                            .map(Enum::name)
                            .map(String::toLowerCase)
                            .collect(Collectors.toList());
                    list.add("rainbow");
                    return list.stream().filter(new StartsWithPredicate(parse.current.token))
                            .map(args -> parse.current.prefix + args)
                            .collect(GuavaCollectors.toImmutableList());
                }
            } else if (parse.current.type.equals(AdvCmdParser.CurrentElement.ElementType.COMPLETE))
                return ImmutableList.of(parse.current.prefix + " ");
            return ImmutableList.of();
        }

        @Override
        public IWand build(DataView dataView) {
            if (dataView.contains(COLOR_QUERY) && dataView.contains(RAINBOW_QUERY))
                return new PositionWand(Position.Color.from(dataView.getString(COLOR_QUERY).orElse(null)), dataView.getBoolean(RAINBOW_QUERY).orElse(false));
            else return new PositionWand();
        }

        @Override
        public String type() {
            return TYPE;
        }

        @Override
        public String[] aliases() {
            return aliases;
        }
    }
}
