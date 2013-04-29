package buildtowin.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ItemStackList {
    
    private ItemStack[] contents;
    
    public ItemStackList(int size) {
        this.contents = new ItemStack[size];
    }
    
    public void readTagList(NBTTagList tagList) {
        this.clear();
        
        for (int i = 0; i < tagList.tagCount(); ++i) {
            NBTTagCompound itemStackNbt = (NBTTagCompound) tagList.tagAt(i);
            int j = itemStackNbt.getByte("Slot") & 255;
            
            if (j >= 0 && j < this.contents.length) {
                this.contents[j] = ItemStack.loadItemStackFromNBT(itemStackNbt);
            }
        }
    }
    
    public NBTTagList getTagList() {
        NBTTagList shopContentsNbt = new NBTTagList();
        
        for (int i = 0; i < this.contents.length; ++i) {
            if (this.contents[i] != null) {
                NBTTagCompound itemStackNbt = new NBTTagCompound();
                itemStackNbt.setByte("Slot", (byte) i);
                this.contents[i].writeToNBT(itemStackNbt);
                
                shopContentsNbt.appendTag(itemStackNbt);
            }
        }
        
        return shopContentsNbt;
    }
    
    public void clear() {
        for (int i = 0; i < this.contents.length; ++i) {
            this.contents[i] = null;
        }
    }
    
    public ItemStack[] getContents() {
        return this.contents;
    }
}
