package buildtowin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.client.FMLClientHandler;
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
                TileEntityBuildingController buildingControllerToConnect = (TileEntityBuildingController) player.worldObj.getBlockTileEntity(x, y, z);
                
                if (connectedBuildingController != buildingControllerToConnect) {
                    connectedBuildingController.addBuildingController(buildingControllerToConnect, true);
                    
                    buildingControllerToConnect.addBuildingController(connectedBuildingController, true);
                    buildingControllerToConnect.refreshColor(connectedBuildingController.getConnectedBuildingControllers().size());
                    buildingControllerToConnect.loadBlueprintRelative(connectedBuildingController.getBlockDataListRelative(), false);
                    
                    if (player.worldObj.isRemote) {
                        Minecraft mc = FMLClientHandler.instance().getClient();
                        mc.ingameGUI.getChatGUI().printChatMessage(
                                "<BuildToWin> Connected the building controller to your game.");
                    }
                    
                    return true;
                }
            }
        }
        
        if (player.worldObj.isRemote) {
            Minecraft mc = FMLClientHandler.instance().getClient();
            mc.ingameGUI.getChatGUI().printChatMessage(
                    "<BuildToWin> Please connect to the Building Controller.");
        }
        
        return true;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void updateIcons(IconRegister par1IconRegister) {
        this.iconIndex = par1IconRegister.registerIcon("buildtowin:blueprinter");
    }
}
