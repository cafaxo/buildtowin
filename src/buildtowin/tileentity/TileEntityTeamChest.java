package buildtowin.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;

public class TileEntityTeamChest extends TileEntityChest implements ITeamHubExtension {
    
    private TileEntityTeamHub teamHub;
    
    @Override
    public ItemStack decrStackSize(int par1, int par2) {
        if (this.teamHub != null) {
            ItemStack chestContents[] = this.teamHub.getTeamChestContents().getContents();
            
            if (chestContents[par1] != null) {
                ItemStack itemstack;
                
                if (chestContents[par1].stackSize <= par2) {
                    itemstack = chestContents[par1];
                    chestContents[par1] = null;
                    this.onInventoryChanged();
                    return itemstack;
                } else {
                    itemstack = chestContents[par1].splitStack(par2);
                    
                    if (chestContents[par1].stackSize == 0) {
                        chestContents[par1] = null;
                    }
                    
                    this.onInventoryChanged();
                    return itemstack;
                }
            }
        }
        
        return null;
    }
    
    @Override
    public ItemStack getStackInSlot(int par1) {
        if (this.teamHub != null) {
            ItemStack chestContents[] = this.teamHub.getTeamChestContents().getContents();
            
            if (chestContents != null) {
                return chestContents[par1];
            }
        }
        
        return null;
    }
    
    @Override
    public ItemStack getStackInSlotOnClosing(int par1) {
        if (this.teamHub != null) {
            ItemStack chestContents[] = this.teamHub.getTeamChestContents().getContents();
            
            if (chestContents[par1] != null) {
                ItemStack itemstack = chestContents[par1];
                chestContents[par1] = null;
                return itemstack;
            }
        }
        
        return null;
        
    }
    
    @Override
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
        if (this.teamHub != null) {
            ItemStack chestContents[] = this.teamHub.getTeamChestContents().getContents();
            
            chestContents[par1] = par2ItemStack;
            
            if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
                par2ItemStack.stackSize = this.getInventoryStackLimit();
            }
            
            this.onInventoryChanged();
        }
    }
    
    @Override
    public void setTeamHub(TileEntityTeamHub teamHub) {
        this.teamHub = teamHub;
    }
    
    @Override
    public TileEntityTeamHub getTeamHub() {
        if (this.teamHub != null && this.teamHub.isInvalid()) {
            this.teamHub = null;
        }
        
        return this.teamHub;
    }
}
