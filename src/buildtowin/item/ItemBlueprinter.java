package buildtowin.item;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import buildtowin.BuildToWin;
import buildtowin.tileentity.TileEntityBuildingHub;
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
        
        if (blockId == BuildToWin.buildingHub.blockID) {
            TileEntityBuildingHub buildingHub = (TileEntityBuildingHub) player.worldObj.getBlockTileEntity(x, y, z);
            
            if (buildingHub.getPlayerList().isPlayerConnected(player)) {
                buildingHub.disconnectPlayer(player);
            } else {
                buildingHub.connectPlayer(player);
            }
        } else {
            TileEntityBuildingHub buildingHub = TileEntityBuildingHub.getBuildingHub(player);
            
            if (buildingHub != null) {
                if (itemstack.stackTagCompound == null) {
                    itemstack.stackTagCompound = new NBTTagCompound();
                }
                
                if (!itemstack.stackTagCompound.hasKey("selection")) {
                    itemstack.stackTagCompound.setIntArray("selection", new int[] { x, y, z });
                    BuildToWin.printChatMessage(player, "Started the selection at " + x + ", " + y + ", " + z);
                } else {
                    int selection[] = itemstack.stackTagCompound.getIntArray("selection");
                    itemstack.stackTagCompound.removeTag("selection");
                    BuildToWin.printChatMessage(player, "Finished the selection at " + x + ", " + y + ", " + z);
                    
                    if (!player.worldObj.isRemote) {
                        buildingHub.getBlueprint().select(selection[0], selection[1], selection[2], x, y, z);
                    }
                }
            } else {
                BuildToWin.printChatMessage(player, "Please connect to a Building Hub.");
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
