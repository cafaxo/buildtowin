package buildtowin;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
    public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
        int blockId = player.worldObj.getBlockId(x, y, z);
        
        if (blockId == BuildToWin.getBlueprint().blockID) {
            TileEntityBuildingController buildingController = BuildToWin.getBuildingControllerList(player.worldObj).getBuildingController(player);
            
            if (buildingController != null) {
                BlockData blockData = buildingController.getBlockData(x, y, z);
                
                if (blockData != null) {
                    buildingController.removeBlueprint(blockData, true);
                }
            } else {
                BuildToWin.printChatMessage(player.worldObj, "Please connect to the Building Controller.");
            }
        } else if (blockId == BuildToWin.getBuildingController().blockID) {
            TileEntityBuildingController buildingController = (TileEntityBuildingController) player.worldObj.getBlockTileEntity(x, y, z);
            
            if (buildingController.isPlayerConnectedAndOnline(player)) {
                buildingController.disconnectPlayer(player);
            } else {
                TileEntityBuildingController connectedBuildingController = BuildToWin.getBuildingControllerList(player.worldObj).getBuildingController(player);
                
                if (connectedBuildingController == null) {
                    buildingController.connectPlayer(player);
                } else if (!connectedBuildingController.getConnectedBuildingControllers().contains(buildingController)) {
                    connectedBuildingController.connectBuildingController(buildingController, player);
                } else {
                    connectedBuildingController.disconnectBuildingController(buildingController, player);
                }
            }
        } else if (blockId != BuildToWin.getBlueprint().blockID && blockId != BuildToWin.getBuildingController().blockID) {
            TileEntityBuildingController buildingController = BuildToWin.getBuildingControllerList(player.worldObj).getBuildingController(player);
            
            if (buildingController != null) {
                buildingController.placeBlueprint(new BlockData(x, y, z, blockId, player.worldObj.getBlockMetadata(x, y, z)), true);
            } else {
                BuildToWin.printChatMessage(player.worldObj, "Please connect to the Building Controller.");
            }
        }
        
        return true;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void updateIcons(IconRegister par1IconRegister) {
        this.iconIndex = par1IconRegister.registerIcon("buildtowin:blueprinter");
    }
}
