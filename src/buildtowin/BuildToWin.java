package buildtowin;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
import buildtowin.item.ItemBlueprintDetector;
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
import buildtowin.util.PriceList;
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

@Mod(modid = "BuildToWin", name = "Build To Win!", version = "1.0.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = { "btw" }, packetHandler = PacketHandler.class)
public class BuildToWin {
    
    @Instance("BuildToWin")
    public static BuildToWin instance;
    
    @SidedProxy(clientSide = "buildtowin.ClientProxy", serverSide = "buildtowin.CommonProxy")
    public static CommonProxy proxy;
    
    public static int connectionWireRenderId;
    
    public static int blueprintRenderId;
    
    public static int coloredBlockRenderId;
    
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
    
    public final static ItemPencil pencil = (ItemPencil) new ItemPencil(5000).setUnlocalizedName("pencil");
    
    public final static ItemPencil rubber = (ItemPencil) new ItemPencil(5001).setUnlocalizedName("rubber");
    
    public final static ItemBlueprintDetector blueprintDetector = new ItemBlueprintDetector(5002);
    
    @PreInit
    public void preInit(FMLPreInitializationEvent event) {
        PriceList.serverInstance.init(event.getModConfigurationDirectory());
    }
    
    @Init
    public void load(FMLInitializationEvent event) {
        BuildToWin.proxy.init();
        
        TileEntity.addMapping(TileEntityGameHub.class, "gameHub");
        TileEntity.addMapping(TileEntityTeamHub.class, "teamHub");
        TileEntity.addMapping(TileEntityPenalizer.class, "penalizer");
        TileEntity.addMapping(TileEntityProtector.class, "protector");
        TileEntity.addMapping(TileEntityShop.class, "shop");
        TileEntity.addMapping(TileEntityTeamChest.class, "teamChest");
        TileEntity.addMapping(TileEntityConnectionWire.class, "connectionWire");
        TileEntity.addMapping(TileEntityBuildingHub.class, "buildingHub");
        TileEntity.addMapping(TileEntityBlueprint.class, "blueprint");
        
        GameRegistry.registerBlock(BuildToWin.gameHub, "gameHub");
        GameRegistry.registerBlock(BuildToWin.teamHub, "teamHub");
        GameRegistry.registerBlock(BuildToWin.penalizer, "penalizer");
        GameRegistry.registerBlock(BuildToWin.protector, "protector");
        GameRegistry.registerBlock(BuildToWin.shop, "shop");
        GameRegistry.registerBlock(BuildToWin.teamChest, "teamChest");
        GameRegistry.registerBlock(BuildToWin.connectionWire, "connectionWire");
        GameRegistry.registerBlock(BuildToWin.buildingHub, "buildingHub");
        GameRegistry.registerBlock(BuildToWin.blueprint, "blueprint");
        
        GameRegistry.addRecipe(new ItemStack(BuildToWin.teamChest), "xxx", "xyx", "xxx",
                'x', Item.redstone, 'y', Block.chest);
        
        GameRegistry.addRecipe(new ItemStack(BuildToWin.penalizer), "xxx", "xyx", "xxx",
                'x', Block.stone, 'y', Item.gunpowder);
        
        GameRegistry.addRecipe(new ItemStack(BuildToWin.protector), "xxx", "xyx", "xxx",
                'x', Block.stone, 'y', Item.swordStone);
        
        GameRegistry.addRecipe(new ItemStack(BuildToWin.shop), "xxx", "yzy", "xxx",
                'x', Block.planks, 'y', Item.redstone, 'z', Item.ingotIron);
        
        MinecraftForge.setBlockHarvestLevel(BuildToWin.teamChest, "pickaxe", 2);
        MinecraftForge.setBlockHarvestLevel(BuildToWin.penalizer, "pickaxe", 2);
        MinecraftForge.setBlockHarvestLevel(BuildToWin.protector, "pickaxe", 2);
        MinecraftForge.setBlockHarvestLevel(BuildToWin.shop, "pickaxe", 2);
        
        LanguageRegistry.instance().addStringLocalization("itemGroup.customTab", "en_US", "Build To Win");
        LanguageRegistry.addName(BuildToWin.gameHub, "Game Hub");
        LanguageRegistry.addName(BuildToWin.teamHub, "Team Hub");
        LanguageRegistry.addName(BuildToWin.penalizer, "Penalizer");
        LanguageRegistry.addName(BuildToWin.protector, "Protector");
        LanguageRegistry.addName(BuildToWin.shop, "Shop");
        LanguageRegistry.addName(BuildToWin.teamChest, "Team Chest");
        LanguageRegistry.addName(BuildToWin.connectionWire, "Connection Wire");
        LanguageRegistry.addName(BuildToWin.buildingHub, "Building Hub");
        LanguageRegistry.addName(BuildToWin.blueprint, "Blueprint");
        LanguageRegistry.addName(BuildToWin.pencil, "Pencil");
        LanguageRegistry.addName(BuildToWin.rubber, "Rubber");
        LanguageRegistry.addName(BuildToWin.blueprintDetector, "Blueprint Detector");
        
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
