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

public class ItemConnector extends Item {
    
    public ItemConnector(int par1) {
        super(par1);
        
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setUnlocalizedName("Connector");
    }
    
    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
        if (par3World.getBlockId(par4, par5, par6) == BuildToWin.getBuildingController().blockID) {
            TileEntityBuildingController connectedBuildingController = BuildToWin.getBuildingControllerList(par3World).getBuildingController(par2EntityPlayer);
            
            if (connectedBuildingController != null) {
                TileEntityBuildingController buildingControllerToConnect = (TileEntityBuildingController) par3World.getBlockTileEntity(par4, par5, par6);
                
                if (connectedBuildingController != buildingControllerToConnect) {
                    connectedBuildingController.addBuildingController(buildingControllerToConnect, true);
                    buildingControllerToConnect.addBuildingController(connectedBuildingController, true);
                    
                    if (par3World.isRemote) {
                        Minecraft mc = FMLClientHandler.instance().getClient();
                        mc.ingameGUI.getChatGUI().printChatMessage(
                                "<BuildToWin> Connected the building controller to your game.");
                    }
                    
                    return true;
                }
            }
        }
        
        if (par3World.isRemote) {
            Minecraft mc = FMLClientHandler.instance().getClient();
            mc.ingameGUI.getChatGUI().printChatMessage(
                    "<BuildToWin> Please connect to the Building Controller.");
        }
        
        return false;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void updateIcons(IconRegister par1IconRegister) {
        this.iconIndex = par1IconRegister.registerIcon("buildtowin:blueprinter");
    }
}
