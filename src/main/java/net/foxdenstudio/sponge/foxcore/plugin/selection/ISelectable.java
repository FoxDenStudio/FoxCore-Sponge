package net.foxdenstudio.sponge.foxcore.plugin.selection;

/**
 * Created by Fox on 11/17/2016.
 */
public interface ISelectable<T extends ISelection> {

    T toSelection();

}
