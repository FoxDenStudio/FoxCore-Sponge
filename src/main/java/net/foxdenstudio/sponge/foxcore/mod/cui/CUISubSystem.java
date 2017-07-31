package net.foxdenstudio.sponge.foxcore.mod.cui;

import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;

public abstract class CUISubSystem {

    public void onTick(@Nonnull final TickEvent tickEvent) {

    }

    public void onKeyPress(@Nonnull final InputEvent.KeyInputEvent keyInputEvent) {

    }
}
