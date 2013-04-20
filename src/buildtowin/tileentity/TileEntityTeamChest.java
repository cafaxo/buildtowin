package buildtowin.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import buildtowin.BuildToWin;
import buildtowin.util.PlayerList;

public class TileEntityTeamChest extends TileEntityChest {
    
    private ItemStack[] chestContents;
    
    @Override
    public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
        if (this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) == this
                && entityPlayer.getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D) <= 64.0D) {
            TileEntityTeamHub teamHub = (TileEntityTeamHub) PlayerList.getPlayerListProvider(entityPlayer, TileEntityTeamHub.class);
            
            if (teamHub != null) {
                this.chestContents = teamHub.getTeamChestContents().getContents();
                return true;
            } else {
                BuildToWin.sendChatMessage(entityPlayer, "Please connect to a Team Hub.");
            }
        }
        
        return false;
    }
    
    @Override
    public ItemStack decrStackSize(int par1, int par2) {
        if (this.chestContents[par1] != null) {
            ItemStack itemstack;
            
            if (this.chestContents[par1].stackSize <= par2) {
                itemstack = this.chestContents[par1];
                this.chestContents[par1] = null;
                this.onInventoryChanged();
                return itemstack;
            } else {
                itemstack = this.chestContents[par1].splitStack(par2);
                
                if (this.chestContents[par1].stackSize == 0) {
                    this.chestContents[par1] = null;
                }
                
                this.onInventoryChanged();
                return itemstack;
            }
        } else {
            return null;
        }
    }
    
    @Override
    public ItemStack getStackInSlot(int par1) {
        if (this.chestContents != null) {
            return this.chestContents[par1];
        }
        
        return null;
    }
    
    @Override
    public ItemStack getStackInSlotOnClosing(int par1) {
        if (this.chestContents[par1] != null) {
            ItemStack itemstack = this.chestContents[par1];
            this.chestContents[par1] = null;
            return itemstack;
        } else {
            return null;
        }
    }
    
    @Override
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
        this.chestContents[par1] = par2ItemStack;
        
        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
        
        this.onInventoryChanged();
    }
    
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
    }
    
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
    }
}
