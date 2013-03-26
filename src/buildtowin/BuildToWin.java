package buildtowin;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
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

@Mod(modid = "BuildToWin", name = "Build To Win!", version = "0.1.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = { "btwbcupdt", "btwtimsupdt", "btwbpupdt", "btwbpsav", "btwbpload", "btwstart", "btwstop" }, packetHandler = PacketHandler.class)
public class BuildToWin {
    private final static BlockBuildingController buildingController = new BlockBuildingController(244);
    private final static BlockBlueprint blueprint = new BlockBlueprint(243);
    private final static ItemBlueprinter blueprinter = new ItemBlueprinter(5000);
    private final static ItemConnector connector = new ItemConnector(5001);
    
    @Instance("BuildToWin")
    public static BuildToWin instance;
    
    @SidedProxy(clientSide = "buildtowin.ClientProxy", serverSide = "buildtowin.CommonProxy")
    public static CommonProxy proxy;
    
    public static int blueprintRenderingId;
    
    public static BlueprintList serverBlueprintList;
    
    public static BlueprintList clientBlueprintList;
    
    public static BuildingControllerList buildingControllerListServer;
    
    public static BuildingControllerList buildingControllerListClient;
    
    public static BuildingControllerList getBuildingControllerList(World world) {
        if (world.isRemote) {
            return buildingControllerListClient;
        } else {
            return buildingControllerListServer;
        }
    }
    
    @PreInit
    public void preInit(FMLPreInitializationEvent event) {
        TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
        
        buildingControllerListServer = new BuildingControllerList();
        buildingControllerListClient = new BuildingControllerList();
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
        LanguageRegistry.addName(connector, "Connector");
    }
    
    @PostInit
    public void postInit(FMLPostInitializationEvent event) {
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
    
    public static ItemConnector getConnector() {
        return connector;
    }
}
