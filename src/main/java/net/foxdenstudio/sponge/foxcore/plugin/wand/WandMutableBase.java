package net.foxdenstudio.sponge.foxcore.plugin.wand;

/**
 * Created by Fox on 10/7/2016.
 */
public abstract class WandMutableBase implements IWandMutable {

    boolean isDirty;

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    @Override
    public void markDirty(boolean isDirty) {
        this.isDirty = isDirty;
    }
}
