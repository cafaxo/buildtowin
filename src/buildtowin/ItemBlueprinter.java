package buildtowin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlueprinter extends Item {
    public ItemBlueprinter(int id) {
        super(id);
        
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setUnlocalizedName("Blueprinter");
    }
    
    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
        int blockId = par3World.getBlockId(par4, par5, par6);
        
        if (blockId == BuildToWin.getBlueprint().blockID) {
            TileEntityBuildingController buildingController = BuildToWin.getBuildingControllerList(par3World).getBuildingController(par2EntityPlayer);
            
            if (buildingController != null) {
                BlockData blockData = buildingController.getBlockData(par4, par5, par6);
                
                if (blockData != null) {
                    buildingController.removeBlueprint(blockData, true);
                }
            } else if (par3World.isRemote) {
                Minecraft mc = FMLClientHandler.instance().getClient();
                mc.ingameGUI.getChatGUI().printChatMessage(
                        "<BuildToWin> Please connect to the Building Controller.");
            }
        } else if (blockId == BuildToWin.getBuildingController().blockID) {
            TileEntityBuildingController buildingController = (TileEntityBuildingController) par3World.getBlockTileEntity(par4, par5, par6);
            
            if (buildingController.isPlayerConnectedAndOnline(par2EntityPlayer)) {
                buildingController.disconnectPlayer(par2EntityPlayer);
            } else {
                buildingController.connectPlayer(par2EntityPlayer);
            }
        } else {
            TileEntityBuildingController buildingController = BuildToWin.getBuildingControllerList(par3World).getBuildingController(par2EntityPlayer);
            
            if (buildingController != null) {
                buildingController.placeBlueprint(new BlockData(par4, par5, par6, blockId, par3World.getBlockMetadata(par4, par5, par6)), true);
                
                return true;
            } else {
                if (par3World.isRemote) {
                    Minecraft mc = FMLClientHandler.instance().getClient();
                    mc.ingameGUI.getChatGUI().printChatMessage(
                            "<BuildToWin> Please connect to the Building Controller.");
                }
            }
        }
        
        return false;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void updateIcons(IconRegister par1IconRegister) {
        this.iconIndex = par1IconRegister.registerIcon("buildtowin:blueprinter");
    }
}
