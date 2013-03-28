package buildtowin;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemConnector extends Item {
    
    public ItemConnector(int par1) {
        super(par1);
        
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setUnlocalizedName("Connector");
    }
    
    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
        if (player.worldObj.getBlockId(x, y, z) == BuildToWin.getBuildingController().blockID) {
            TileEntityBuildingController connectedBuildingController = BuildToWin.getBuildingControllerList(player.worldObj).getBuildingController(player);
            
            if (connectedBuildingController != null) {
                if (connectedBuildingController.getMode() == (byte) 2) {
                    BuildToWin.printChatMessage(player.worldObj, "Your building controller is already participating in a game.");
                    return true;
                }
                
                TileEntityBuildingController buildingControllerToConnect = (TileEntityBuildingController) player.worldObj.getBlockTileEntity(x, y, z);
                
                if (buildingControllerToConnect.getMode() != (byte) 0) {
                    BuildToWin.printChatMessage(player.worldObj, "This building controller is already participating in a game.");
                    return true;
                }
                
                if (connectedBuildingController != buildingControllerToConnect) {
                    connectedBuildingController.setMode((byte) 1);
                    connectedBuildingController.addBuildingController(buildingControllerToConnect);
                    
                    buildingControllerToConnect.setMode((byte) 2);
                    buildingControllerToConnect.addBuildingController(connectedBuildingController);
                    buildingControllerToConnect.setColor((byte) connectedBuildingController.getConnectedBuildingControllers().size());
                    buildingControllerToConnect.loadBlueprintRelative(connectedBuildingController.getBlockDataListRelative(), false);
                    
                    BuildToWin.printChatMessage(player.worldObj, "Connected the building controller to your game.");
                }
                
                return true;
            }
        }
        
        BuildToWin.printChatMessage(player.worldObj, "Please connect to the Building Controller.");
        return true;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void updateIcons(IconRegister par1IconRegister) {
        this.iconIndex = par1IconRegister.registerIcon("buildtowin:blueprinter");
    }
}
