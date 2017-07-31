package net.foxdenstudio.sponge.foxcore.mod.cui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class WorldSystem {
    private final CUI cui;
    private final Tessellator tessellator;
    private final VertexBuffer vertexBuffer;

    public WorldSystem(@Nonnull final CUI cui, @Nonnull final Tessellator tessellator, @Nonnull final VertexBuffer vertexBuffer, Minecraft minecraft) {
        this.cui = cui;
        this.tessellator = tessellator;
        this.vertexBuffer = vertexBuffer;
    }

    public void render() {

    }
}
