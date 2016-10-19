package net.foxdenstudio.sponge.foxcore.plugin.wand.types;

import com.google.common.collect.ImmutableList;
import net.foxdenstudio.sponge.foxcore.plugin.command.util.ProcessResult;
import net.foxdenstudio.sponge.foxcore.plugin.wand.IWand;
import net.foxdenstudio.sponge.foxcore.plugin.wand.IWandFactory;
import net.foxdenstudio.sponge.foxcore.plugin.wand.WandMutableBase;
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
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Fox on 10/12/2016.
 */
public class CounterWand extends WandMutableBase {

    public static final String type = "counter";
    private static final DataQuery counterQuery = DataQuery.of("counter");

    private int counter;

    public CounterWand() {
    }

    public CounterWand(int counter) {
        this.counter = counter;
    }

    @Override
    public boolean leftClickAir(Player player) {
        counter--;
        return true;
    }

    @Override
    public boolean rightClickAir(Player player) {
        counter++;
        return true;
    }

    @Override
    public boolean leftClickBlock(Player player, BlockSnapshot block) {
        counter--;
        return true;
    }

    @Override
    public boolean rightClickBlock(Player player, BlockSnapshot block) {
        counter++;
        return true;
    }

    @Override
    public boolean leftClickEntity(Player player, Entity entity) {
        counter--;
        return true;
    }

    @Override
    public boolean rightClickEntity(Player player, Entity entity) {
        counter++;
        return true;
    }

    @Override
    public ProcessResult modify(CommandSource source, String arguments) throws CommandException {
        return ProcessResult.failure();
    }

    @Override
    public Text getItemName() {
        return Text.of("Counter Wand: " + counter);
    }

    @Override
    public List<Text> getLore() {
        return ImmutableList.of(getItemName());
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        //System.out.println(counter);
        return new MemoryDataContainer().set(counterQuery, counter);
    }

    @Override
    public String toString() {
        return "CounterWand{" +
                "counter=" + counter +
                '}';
    }

    public static class Factory implements IWandFactory {

        private static final String[] aliases = {"c", "count", "counter"};

        @Override
        public IWand create(String arguments, @Nullable CommandSource source, @Nullable Location<World> targetPosition) {
            return new CounterWand();
        }

        @Override
        public List<String> createSuggestions(String arguments, CommandSource source, @Nullable Location<World> targetPosition) {
            return ImmutableList.of();
        }

        @Override
        public IWand build(DataView dataView) {
            int count = (int) dataView.get(counterQuery).orElse(0);
            return new CounterWand(count);
        }

        @Override
        public String type() {
            return type;
        }

        @Override
        public String[] aliases() {
            return aliases;
        }
    }
}
