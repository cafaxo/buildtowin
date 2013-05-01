package buildtowin.tileentity;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import buildtowin.blueprint.IBlueprintProvider;

public class TileEntityBlueprint extends TileEntity {
    
    private int data[] = new int[5];
    
    private TileEntity cachedBlueprintProvider;
    
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setIntArray("data", this.data);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        
        this.data = par1NBTTagCompound.getIntArray("data");
    }
    
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, tag);
    }
    
    @Override
    public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
        NBTTagCompound tag = pkt.customParam1;
        this.readFromNBT(tag);
    }
    
    public int getSavedId() {
        return this.data[0];
    }
    
    public void setSavedId(int savedId) {
        this.data[0] = savedId;
    }
    
    public int getSavedMetadata() {
        return this.data[1];
    }
    
    public void setSavedMetadata(int savedMetadata) {
        this.data[1] = savedMetadata;
    }
    
    public Block getSavedBlock() {
        if (this.data[0] > 0 && this.data[0] < Block.blocksList.length) {
            if (Block.blocksList[this.data[0]] != null) {
                return Block.blocksList[this.data[0]];
            }
        }
        
        return null;
    }
    
    public TileEntity getBlueprintProvider() {
        if (this.cachedBlueprintProvider == null || this.cachedBlueprintProvider.isInvalid()) {
            TileEntity tileEntity = this.worldObj.getBlockTileEntity(this.data[2], this.data[3], this.data[4]);
            
            if (tileEntity instanceof IBlueprintProvider && !tileEntity.isInvalid()) {
                this.cachedBlueprintProvider = tileEntity;
            } else {
                this.cachedBlueprintProvider = null;
                this.worldObj.setBlockToAir(this.xCoord, this.yCoord, this.zCoord);
            }
        }
        
        return this.cachedBlueprintProvider;
    }
    
    public void setBlueprintProvider(TileEntity blueprintProvider) {
        this.data[2] = blueprintProvider.xCoord;
        this.data[3] = blueprintProvider.yCoord;
        this.data[4] = blueprintProvider.zCoord;
        
        this.cachedBlueprintProvider = null;
    }
}
