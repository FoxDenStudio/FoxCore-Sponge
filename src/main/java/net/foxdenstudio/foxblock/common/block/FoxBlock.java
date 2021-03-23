package net.foxdenstudio.foxblock.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class FoxBlock extends Block {

    public FoxBlock(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
    }

    public FoxBlock(Material materialIn) {
        super(materialIn);
    }
}
