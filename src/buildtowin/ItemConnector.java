package buildtowin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
            if (par1ItemStack.getTagCompound() != null) {
                int coords[] = par1ItemStack.getTagCompound().getIntArray("first");
                
                if (coords != null && coords.length == 3 && par3World.getBlockId(coords[0], coords[1], coords[2]) == BuildToWin.getBuildingController().blockID) {
                    TileEntityBuildingController buildingControllerFirst = (TileEntityBuildingController) par3World.getBlockTileEntity(coords[0], coords[1], coords[2]);
                    TileEntityBuildingController buildingControllerSecond = (TileEntityBuildingController) par3World.getBlockTileEntity(par4, par5, par6);
                    
                    if (buildingControllerFirst != buildingControllerSecond) {
                        buildingControllerFirst.addBuildingController(buildingControllerSecond);
                        buildingControllerSecond.addBuildingController(buildingControllerFirst);
                        
                        if (par3World.isRemote) {
                            Minecraft mc = FMLClientHandler.instance().getClient();
                            mc.ingameGUI.getChatGUI().printChatMessage(
                                    "<BuildToWin> Connected the building controller to a versus game.");
                        }
                        
                        return true;
                    }
                }
            }
            
            par1ItemStack.stackTagCompound = new NBTTagCompound();
            par1ItemStack.stackTagCompound.setIntArray("first", new int[] { par4, par5, par6 });
            
            if (par3World.isRemote) {
                Minecraft mc = FMLClientHandler.instance().getClient();
                mc.ingameGUI.getChatGUI().printChatMessage(
                        "<BuildToWin> Created a versus game.");
            }
            
            return true;
        }
        return false;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void updateIcons(IconRegister par1IconRegister) {
        this.iconIndex = par1IconRegister.registerIcon("buildtowin:blueprinter");
    }
}
