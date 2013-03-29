package buildtowin;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
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
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = "BuildToWin", name = "Build To Win!", version = "0.2.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = { "btwbcupdt", "btwtimsupdt", "btwbpupdt", "btwbpsav", "btwbpload", "btwstart", "btwstop" }, packetHandler = PacketHandler.class)
public class BuildToWin {
    
    @Instance("BuildToWin")
    public static BuildToWin instance;
    
    @SidedProxy(clientSide = "buildtowin.ClientProxy", serverSide = "buildtowin.CommonProxy")
    public static CommonProxy proxy;
    
    private final static BlockBuildingController buildingController = new BlockBuildingController(244);
    
    private final static BlockBlueprint blueprint = new BlockBlueprint(243);
    
    private final static ItemBlueprinter blueprinter = new ItemBlueprinter(5000);
    
    public static int blueprintRenderId;
    
    public static BlueprintList blueprintListServer;
    
    public static BlueprintList blueprintListClient;
    
    public static BuildingControllerList buildingControllerListServer;
    
    public static BuildingControllerList buildingControllerListClient;
    
    @PreInit
    public void preInit(FMLPreInitializationEvent event) {
        TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
        
        BuildToWin.buildingControllerListServer = new BuildingControllerList();
        BuildToWin.buildingControllerListClient = new BuildingControllerList();
    }
    
    @Init
    public void load(FMLInitializationEvent event) {
        proxy.init();
        
        TileEntity.addMapping(TileEntityBuildingController.class, "BuildingController");
        TileEntity.addMapping(TileEntityBlueprint.class, "BlockData");
        
        GameRegistry.registerBlock(buildingController, "buildingController");
        GameRegistry.registerBlock(blueprint, "blueprint");
        
        LanguageRegistry.addName(buildingController, "Building Controller");
        LanguageRegistry.addName(blueprint, "Blueprint");
        LanguageRegistry.addName(blueprinter, "Blueprinter");
    }
    
    @PostInit
    public void postInit(FMLPostInitializationEvent event) {
    }
    
    public static BuildingControllerList getBuildingControllerList(World world) {
        if (world.isRemote) {
            return buildingControllerListClient;
        } else {
            return buildingControllerListServer;
        }
    }
    
    public static void printChatMessage(World world, String string) {
        if (world.isRemote) {
            Minecraft mc = FMLClientHandler.instance().getClient();
            mc.ingameGUI.getChatGUI().printChatMessage("<BuildToWin> " + string);
        }
    }
    
    public static BlockBuildingController getBuildingController() {
        return buildingController;
    }
    
    public static BlockBlueprint getBlueprint() {
        return blueprint;
    }
    
    public static ItemBlueprinter getBlueprinter() {
        return blueprinter;
    }
}
