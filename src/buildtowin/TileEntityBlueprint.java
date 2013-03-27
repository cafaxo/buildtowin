package buildtowin;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;

public class TileEntityBlueprint extends TileEntity {
    public static final float colors[] = new float[] {
            0.3F, 0.3F, 1.0F, 0.7F,
            1.0F, 0.3F, 0.3F, 0.7F,
            0.3F, 1.0F, 0.3F, 0.7F,
    };
    
    private int blockId = 0;
    
    private int metadata = 0;
    
    private byte color = 0;
    
    public TileEntityBlueprint() {
    }
    
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setInteger("blockid", this.blockId);
        par1NBTTagCompound.setInteger("metadata", this.metadata);
        par1NBTTagCompound.setByte("color", this.color);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        
        this.blockId = par1NBTTagCompound.getInteger("blockid");
        this.metadata = par1NBTTagCompound.getInteger("metadata");
        this.color = par1NBTTagCompound.getByte("color");
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
    
    public int getBlockId() {
        return blockId;
    }
    
    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }
    
    public int getMetadata() {
        return metadata;
    }
    
    public void setMetadata(int metadata) {
        this.metadata = metadata;
    }
    
    public byte getColor() {
        return color;
    }
    
    public void setColor(byte color) {
        this.color = color;
    }
}
