package net.foxdenstudio.foxblock;

import net.foxdenstudio.foxblock.common.CommonProxy;
import net.foxdenstudio.foxblock.common.block.FoxBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;

//@Mod(modid = FoxBlockMod.MOD_ID, name = "FoxBlock!", version = "0.0.1")
public class FoxBlockMod {

    public static final String MOD_ID = "foxblock";

    @SidedProxy(modId = "foxblock", serverSide = "net.foxdenstudio.foxblock.common.CommonProxy", clientSide = "net.foxdenstudio.foxblock.client.ClientProxy")
    private static CommonProxy proxy;

    public final FoxBlock foxbox = (FoxBlock) new FoxBlock(Material.WOOD)
            .setRegistryName(MOD_ID, "foxbox")
            .setCreativeTab(CreativeTabs.DECORATIONS)
            .setTranslationKey("foxbox");

    public final ItemBlock foxboxItem = (ItemBlock) new ItemBlock(foxbox)
            .setRegistryName(Objects.requireNonNull(foxbox.getRegistryName()))
            .setCreativeTab(CreativeTabs.DECORATIONS)
            .setTranslationKey("foxbox");

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        System.out.println("\n-----\n-----\nFOX BLOCK PREINIT!\n-----\n-----");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        System.out.println("\n-----\n-----\nFOX BLOCK INIT!\n-----\n-----");
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> blockRegistryEvent) {
        System.out.println("\n-----\n-----\nFOX BLOCK REGISTER EVENT!\n-----\n-----");
        IForgeRegistry<Block> reg = blockRegistryEvent.getRegistry();
        reg.register(foxbox);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> itemRegistryEvent) {
        System.out.println("\n-----\n-----\nFOX ITEM REGISTER EVENT!\n-----\n-----");
        IForgeRegistry<Item> reg = itemRegistryEvent.getRegistry();
        reg.register(foxboxItem);
    }
}
