package buildtowin.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import buildtowin.blueprint.BlockData;

public class TileEntityBlueprint extends TileEntity {
    
    
    public static final float colors[] = new float[] {
            0.3F, 0.3F, 1.0F, 0.7F,
            1.0F, 0.3F, 0.3F, 0.7F,
            0.3F, 1.0F, 0.3F, 0.7F,
    };
    
    private BlockData blockData;
    
    private int color = 0;
    
    public TileEntityBlueprint() {
        this.blockData = new BlockData(0, 0);
    }
    
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setInteger("blockid", this.blockData.savedId);
        par1NBTTagCompound.setInteger("metadata", this.blockData.savedMetadata);
        par1NBTTagCompound.setByte("color", (byte) this.color);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        
        this.blockData.savedId = par1NBTTagCompound.getInteger("blockid");
        this.blockData.savedMetadata = par1NBTTagCompound.getInteger("metadata");
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
    
    public BlockData getBlockData() {
        return blockData;
    }
    
    public void setBlockData(BlockData blockData) {
        this.blockData = blockData;
    }
    
    public int getColor() {
        return this.color;
    }
    
    public void setColor(int color) {
        this.color = color;
    }
}
