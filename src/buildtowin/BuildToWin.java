package buildtowin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "BuildToWin", name = "Build To Win!", version = "0.1.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = true)
public class BuildToWin {
    private final static BlockContainer blueprint = new BlockBlueprint(243);
    private final static Item blueprinter = new ItemBlueprinter(5000);
    
    @Instance("BuildToWin")
    public static BuildToWin instance;
    
    @SidedProxy(clientSide = "buildtowin.ClientProxy", serverSide = "buildtowin.CommonProxy")
    public static CommonProxy proxy;
    
    @PreInit
    public void preInit(FMLPreInitializationEvent event) {
    }
    
    @Init
    public void load(FMLInitializationEvent event) {
        proxy.registerRenderers();
        
        TileEntity.addMapping(TileEntityBlockData.class, "BlockData");
        GameRegistry.registerBlock(blueprint, "blueprint");
        LanguageRegistry.addName(blueprint, "Blueprint");
        LanguageRegistry.addName(blueprinter, "Blueprinter");
    }
    
    @PostInit
    public void postInit(FMLPostInitializationEvent event) {
    }
}
