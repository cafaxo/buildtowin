package buildtowin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import buildtowin.tileentity.TileEntityShop;
import buildtowin.tileentity.TileEntityTeamHub;
import buildtowin.util.PlayerList;
import buildtowin.util.PriceList;

public class ContainerShop extends Container {
    
    public ContainerShop(InventoryPlayer inventoryPlayer, TileEntityShop tileEntityShop) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(tileEntityShop, j + i * 9, 8 + j * 18, 18 + i * 18));
            }
        }
        
        this.bindPlayerInventory(inventoryPlayer);
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
        return null;
    }
    
    public ItemStack slotClick(int par1, int par2, int par3, EntityPlayer entityPlayer) {
        TileEntityTeamHub teamHub = (TileEntityTeamHub) PlayerList.getPlayerListProvider(entityPlayer, TileEntityTeamHub.class);
        
        if (teamHub == null) {
            return null;
        }
        
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(par1);
        
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            
            int price = PriceList.getInstance(par4EntityPlayer.worldObj).getPrice(itemstack1.getItem()) * itemstack1.stackSize;
            
            if (par1 < 3 * 9) {
                if (teamHub.getEnergy() >= price) {
                    teamHub.setEnergy(teamHub.getEnergy() - price);
                    
                    BuildToWin.printChatMessage(par4EntityPlayer, String.format("Bought %s * %d for %d energy points",
                            itemstack1.getItem().getItemDisplayName(itemstack1),
                            itemstack1.stackSize,
                            price));
                    
                    if (!this.mergeItemStack(itemstack1, 3 * 9, this.inventorySlots.size(), true)) {
                        return null;
                    }
                } else {
                    BuildToWin.printChatMessage(par4EntityPlayer, "You do not have enough energy points");
                }
            } else {
                teamHub.setEnergy(teamHub.getEnergy() + price);
                
                BuildToWin.printChatMessage(par4EntityPlayer, String.format("Sold %s * %d for %d energy points",
                        itemstack1.getItem().getItemDisplayName(itemstack1),
                        itemstack1.stackSize,
                        price));
                
                if (!this.mergeItemStack(itemstack1, 0, 3 * 9, false)) {
                    return null;
                }
            }
            
            if (itemstack1.stackSize == 0) {
                slot.putStack((ItemStack) null);
            } else {
                slot.onSlotChanged();
            }
        }
        
        return itemstack;
    }
    
    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 85 + i * 18));
            }
        }
        
        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 143));
        }
    }
}
