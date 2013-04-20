package buildtowin.tileentity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class TileEntityShop extends TileEntitySynchronized implements IInventory {
    
    private TileEntityTeamHub teamHub;
    
    public TileEntityShop() {
        
    }
    
    @Override
    public boolean writeDescriptionPacket(DataOutputStream dataOutputStream) throws IOException {
        if (this.teamHub != null) {
            dataOutputStream.writeInt(this.teamHub.xCoord);
            dataOutputStream.writeInt(this.teamHub.yCoord);
            dataOutputStream.writeInt(this.teamHub.zCoord);
            return true;
        }
        
        return false;
    }
    
    public void readDescriptionPacket(DataInputStream dataInputStream) throws IOException {
        TileEntity tileEntity = this.worldObj.getBlockTileEntity(
                dataInputStream.readInt(),
                dataInputStream.readInt(),
                dataInputStream.readInt());
        
        if (tileEntity instanceof TileEntityTeamHub) {
            this.teamHub = (TileEntityTeamHub) tileEntity;
        }
    }
    
    @Override
    public int getSizeInventory() {
        return 27;
    }
    
    @Override
    public ItemStack getStackInSlot(int slot) {
        if (this.teamHub != null) {
            return this.teamHub.getGameHub().getShop().getContents()[slot];
        } else {
            return null;
        }
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
            this.teamHub.getGameHub().getShop().getContents()[slot] = itemStack;
            
            if (itemStack != null && itemStack.stackSize > this.getInventoryStackLimit()) {
                itemStack.stackSize = this.getInventoryStackLimit();
            }
            
            this.onInventoryChanged();
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
        return teamHub;
    }
    
    public void setTeamHub(TileEntityTeamHub teamHub) {
        this.teamHub = teamHub;
    }
}
