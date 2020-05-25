package net.foxdenstudio.foxcore.sponge.impl.world;

import net.foxdenstudio.foxcore.api.object.index.FoxMainIndex;
import net.foxdenstudio.foxcore.api.storage.FoxStorageManager;
import net.foxdenstudio.foxcore.api.world.FoxWorld;
import net.foxdenstudio.foxcore.impl.world.FoxWorldImpl;
import net.foxdenstudio.foxcore.impl.world.FoxWorldManagerImplBase;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.world.World;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

public class SpongeFoxWorldManager extends FoxWorldManagerImplBase {

    @Inject
    private SpongeFoxWorldManager(FoxStorageManager storageManager, FoxMainIndex mainIndex, FoxWorldImpl.RepObjectFactory foxWorldImplObjectFactory) {
        super(storageManager, mainIndex, foxWorldImplObjectFactory);
    }

    @Listener
    public void onWorldLoad(LoadWorldEvent event) {
        World world = event.getTargetWorld();
        String name = world.getName();
        UUID uuid = world.getUniqueId();
        Path directory = world.getDirectory();

        FoxWorldImpl foxWorld = this.worldMapCopy.get(name);
        FoxWorldImpl.FoxObject object;
        if (foxWorld == null) {
            foxWorld = new FoxWorldImpl(name, uuid, directory);
            object = this.configureAndLoadWorldRep(foxWorld);
            this.worldMap.put(name, foxWorld);
        } else {
            Optional<? extends FoxWorld.Object> objOpt = foxWorld.getRepresentation();
            if (objOpt.isPresent()) {
                object = (FoxWorldImpl.FoxObject) objOpt.get();
            } else {
                object = this.configureAndLoadWorldRep(foxWorld);
            }

        }
    }
}
