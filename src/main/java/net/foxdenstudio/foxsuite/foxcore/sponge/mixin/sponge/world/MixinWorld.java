package net.foxdenstudio.foxsuite.foxcore.sponge.mixin.sponge.world;

import net.foxdenstudio.foxsuite.foxcore.platform.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(org.spongepowered.api.world.World.class)
public interface MixinWorld extends World {
}
