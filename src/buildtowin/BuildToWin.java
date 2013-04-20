package buildtowin;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import buildtowin.block.BlockBlueprint;
import buildtowin.block.BlockBuildingHub;
import buildtowin.block.BlockConnectionWire;
import buildtowin.block.BlockGameHub;
import buildtowin.block.BlockPenalizer;
import buildtowin.block.BlockProtector;
import buildtowin.block.BlockShop;
import buildtowin.block.BlockTeamChest;
import buildtowin.block.BlockTeamHub;
import buildtowin.client.gui.GuiHandlerShop;
import buildtowin.item.ItemPencil;
import buildtowin.network.PacketHandler;
import buildtowin.tileentity.TileEntityBlueprint;
import buildtowin.tileentity.TileEntityBuildingHub;
import buildtowin.tileentity.TileEntityConnectionWire;
import buildtowin.tileentity.TileEntityGameHub;
import buildtowin.tileentity.TileEntityPenalizer;
import buildtowin.tileentity.TileEntityProtector;
import buildtowin.tileentity.TileEntityShop;
import buildtowin.tileentity.TileEntityTeamChest;
import buildtowin.tileentity.TileEntityTeamHub;
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
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = "BuildToWin", name = "Build To Win!", version = "0.5.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = { "btw" }, packetHandler = PacketHandler.class)
public class BuildToWin {
    
    @Instance("BuildToWin")
    public static BuildToWin instance;
    
    @SidedProxy(clientSide = "buildtowin.ClientProxy", serverSide = "buildtowin.CommonProxy")
    public static CommonProxy proxy;
    
    public static int connectionWireRenderId;
    
    public static int blueprintRenderId;
    
    public final static CreativeTabBuildToWin tabBuildToWin = new CreativeTabBuildToWin("customTab");
    
    public final static BlockGameHub gameHub = new BlockGameHub(248);
    
    public final static BlockTeamHub teamHub = new BlockTeamHub(247);
    
    public final static BlockPenalizer penalizer = new BlockPenalizer(249);
    
    public final static BlockProtector protector = new BlockProtector(250);
    
    public final static BlockShop shop = new BlockShop(251);
    
    public final static BlockTeamChest teamChest = new BlockTeamChest(252);
    
    public final static BlockConnectionWire connectionWire = new BlockConnectionWire(244);
    
    public final static BlockBlueprint blueprint = new BlockBlueprint(245);
    
    public final static BlockBuildingHub buildingHub = new BlockBuildingHub(246);
    
    public final static ItemPencil pencil = (ItemPencil) (new ItemPencil(5000)).setUnlocalizedName("pencil");
    
    public final static ItemPencil rubber = (ItemPencil) (new ItemPencil(5001)).setUnlocalizedName("rubber");
    
    @PreInit
    public void preInit(FMLPreInitializationEvent event) {
    }
    
    @Init
    public void load(FMLInitializationEvent event) {
        proxy.init();
        
        TileEntity.addMapping(TileEntityGameHub.class, "gameHub");
        TileEntity.addMapping(TileEntityTeamHub.class, "teamHub");
        TileEntity.addMapping(TileEntityPenalizer.class, "penalizer");
        TileEntity.addMapping(TileEntityProtector.class, "protector");
        TileEntity.addMapping(TileEntityShop.class, "shop");
        TileEntity.addMapping(TileEntityTeamChest.class, "teamChest");
        TileEntity.addMapping(TileEntityConnectionWire.class, "connectionWire");
        TileEntity.addMapping(TileEntityBuildingHub.class, "buildingHub");
        TileEntity.addMapping(TileEntityBlueprint.class, "blueprint");
        
        GameRegistry.registerBlock(gameHub, "gameHub");
        GameRegistry.registerBlock(teamHub, "teamHub");
        GameRegistry.registerBlock(penalizer, "penalizer");
        GameRegistry.registerBlock(protector, "protector");
        GameRegistry.registerBlock(shop, "shop");
        GameRegistry.registerBlock(teamChest, "teamChest");
        GameRegistry.registerBlock(connectionWire, "connectionWire");
        GameRegistry.registerBlock(buildingHub, "buildingHub");
        GameRegistry.registerBlock(blueprint, "blueprint");
        
        LanguageRegistry.instance().addStringLocalization("itemGroup.customTab", "en_US", "Build To Win");
        LanguageRegistry.addName(gameHub, "Game Hub");
        LanguageRegistry.addName(teamHub, "Team Hub");
        LanguageRegistry.addName(penalizer, "Penalizer");
        LanguageRegistry.addName(protector, "Protector");
        LanguageRegistry.addName(shop, "Shop");
        LanguageRegistry.addName(teamChest, "Team Chest");
        LanguageRegistry.addName(connectionWire, "Connection Wire");
        LanguageRegistry.addName(buildingHub, "Building Hub");
        LanguageRegistry.addName(blueprint, "Blueprint");
        LanguageRegistry.addName(pencil, "Pencil");
        LanguageRegistry.addName(rubber, "Rubber");
        
        TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
        
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        
        NetworkRegistry.instance().registerConnectionHandler(new ConnectionHandler());
        NetworkRegistry.instance().registerGuiHandler(this, new GuiHandlerShop());
    }
    
    @PostInit
    public void postInit(FMLPostInitializationEvent event) {
    }
    
    public static void printChatMessage(EntityPlayer entityPlayer, String string) {
        if (entityPlayer.worldObj.isRemote) {
            Minecraft mc = FMLClientHandler.instance().getClient();
            mc.ingameGUI.getChatGUI().printChatMessage("<BuildToWin> " + string);
        }
    }
    
    public static void sendChatMessage(EntityPlayer entityPlayer, String string) {
        if (!entityPlayer.worldObj.isRemote) {
            PacketDispatcher.sendPacketToPlayer(new Packet3Chat("<BuildToWin> " + string), (Player) entityPlayer);
        }
    }
}
