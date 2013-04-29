package buildtowin.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class TileEntityShop extends TileEntity implements IInventory {
    
    private TileEntityTeamHub teamHub;
    
    @Override
    public int getSizeInventory() {
        return 27;
    }
    
    @Override
    public ItemStack getStackInSlot(int slot) {
        if (this.teamHub != null) {
            if (this.teamHub.getGameHub() != null) {
                if (this.teamHub.getGameHub().getShop() != null) {
                    return this.teamHub.getGameHub().getShop().getContents()[slot];
                }
            }
        }
        
        return null;
    }
    
    @Override
    public ItemStack decrStackSize(int i, int j) {
        return null;
    }
    
    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        return null;
    }
    
    @Override
    public void setInventorySlotContents(int slot, ItemStack itemStack) {
        if (this.teamHub != null) {
            if (this.teamHub.getGameHub() != null) {
                if (this.teamHub.getGameHub().getShop() != null) {
                    this.teamHub.getGameHub().getShop().getContents()[slot] = itemStack;
                    
                    if (itemStack != null && itemStack.stackSize > this.getInventoryStackLimit()) {
                        itemStack.stackSize = this.getInventoryStackLimit();
                    }
                    
                    this.onInventoryChanged();
                }
            }
        }
    }
    
    @Override
    public String getInvName() {
        return "Shop";
    }
    
    @Override
    public boolean isInvNameLocalized() {
        return false;
    }
    
    @Override
    public int getInventoryStackLimit() {
        return 64;
    }
    
    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return true;
    }
    
    @Override
    public void openChest() {
    }
    
    @Override
    public void closeChest() {
    }
    
    @Override
    public boolean isStackValidForSlot(int i, ItemStack itemstack) {
        return false;
    }
    
    public TileEntityTeamHub getTeamHub() {
        return this.teamHub;
    }
    
    public void setTeamHub(TileEntityTeamHub teamHub) {
        this.teamHub = teamHub;
    }
}
