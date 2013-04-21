package buildtowin.item;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
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
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        return true;
    }
    
    public boolean onBlockRightClicked(ItemStack itemStack, int x, int y, int z, int facedX, int facedY, int facedZ, EntityPlayer entityPlayer) {
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
                    itemStack.stackTagCompound.setIntArray("selection", new int[] { facedX, facedY, facedZ });
                    BuildToWin.printChatMessage(entityPlayer, "Started the selection at " + facedX + ", " + facedY + ", " + facedZ);
                } else {
                    int selection[] = itemStack.stackTagCompound.getIntArray("selection");
                    itemStack.stackTagCompound.removeTag("selection");
                    BuildToWin.printChatMessage(entityPlayer, "Finished the selection at " + facedX + ", " + facedY + ", " + facedZ);
                    
                    if (!entityPlayer.worldObj.isRemote) {
                        buildingHub.getBlueprint().select(selection[0], selection[1], selection[2], facedX, facedY, facedZ, this == BuildToWin.pencil);
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
    public void updateIcons(IconRegister par1IconRegister) {
        if (this == BuildToWin.pencil) {
            this.iconIndex = par1IconRegister.registerIcon("buildtowin:blueprinter");
        } else {
            this.iconIndex = par1IconRegister.registerIcon("buildtowin:blueprinter");
        }
    }
}
