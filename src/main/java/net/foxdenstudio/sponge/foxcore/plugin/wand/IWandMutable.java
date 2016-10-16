package net.foxdenstudio.sponge.foxcore.plugin.wand;

/**
 * Created by Fox on 10/7/2016.
 */
public interface IWandMutable extends IWand {

    boolean isDirty();

    void markDirty(boolean isDirty);
}
