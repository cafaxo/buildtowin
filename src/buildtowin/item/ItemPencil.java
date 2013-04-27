package buildtowin.item;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import buildtowin.BuildToWin;
import buildtowin.tileentity.TileEntityBuildingHub;
import buildtowin.util.PlayerList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPencil extends Item {
    
    public ItemPencil(int id) {
        super(id);
        
        this.setMaxStackSize(1);
        this.setCreativeTab(BuildToWin.tabBuildToWin);
    }
    
    @Override
    public boolean onBlockStartBreak(ItemStack itemStack, int x, int y, int z, EntityPlayer entityPlayer) {
        int blockId = entityPlayer.worldObj.getBlockId(x, y, z);
        
        if (blockId == BuildToWin.buildingHub.blockID) {
            TileEntityBuildingHub buildingHub = (TileEntityBuildingHub) entityPlayer.worldObj.getBlockTileEntity(x, y, z);
            
            if (buildingHub.getPlayerList().isPlayerConnected(entityPlayer)) {
                buildingHub.getPlayerList().disconnectPlayer(entityPlayer);
            } else {
                buildingHub.getPlayerList().connectPlayer(entityPlayer);
            }
        } else {
            TileEntityBuildingHub buildingHub = (TileEntityBuildingHub) PlayerList.getPlayerListProvider(entityPlayer, TileEntityBuildingHub.class);
            
            if (buildingHub != null) {
                if (itemStack.stackTagCompound == null) {
                    itemStack.stackTagCompound = new NBTTagCompound();
                }
                
                if (!itemStack.stackTagCompound.hasKey("selection")) {
                    itemStack.stackTagCompound.setIntArray("selection", new int[] { x, y, z });
                    BuildToWin.printChatMessage(entityPlayer, "Started the selection at " + x + ", " + y + ", " + z);
                } else {
                    int selection[] = itemStack.stackTagCompound.getIntArray("selection");
                    itemStack.stackTagCompound.removeTag("selection");
                    BuildToWin.printChatMessage(entityPlayer, "Finished the selection at " + x + ", " + y + ", " + z);
                    
                    if (!entityPlayer.worldObj.isRemote) {
                        buildingHub.getBlueprint().select(selection[0], selection[1], selection[2], x, y, z, this == BuildToWin.pencil);
                    }
                }
            } else {
                BuildToWin.printChatMessage(entityPlayer, "Please connect to a Building Hub.");
            }
        }
        
        return true;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
        if (this == BuildToWin.pencil) {
            this.itemIcon = par1IconRegister.registerIcon("buildtowin:pencil");
        } else {
            this.itemIcon = par1IconRegister.registerIcon("buildtowin:rubber");
        }
    }
}
