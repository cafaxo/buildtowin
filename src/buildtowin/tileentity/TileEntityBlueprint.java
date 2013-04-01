package buildtowin.tileentity;

import java.util.Arrays;
import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import buildtowin.blueprint.BlockData;

public class TileEntityBlueprint extends TileEntity {
    
    public static HashMap<Integer, BlockData> instancesClient = new HashMap<Integer, BlockData>();
    
    public static final float colors[] = new float[] {
            0.3F, 0.3F, 1.0F, 0.7F,
            1.0F, 0.3F, 0.3F, 0.7F,
            0.3F, 1.0F, 0.3F, 0.7F,
    };
    
    private BlockData blockData;
    
    private byte color = 0;
    
    public TileEntityBlueprint() {
        this.blockData = new BlockData(0, 0);
    }
    
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setInteger("blockid", this.blockData.id);
        par1NBTTagCompound.setInteger("metadata", this.blockData.metadata);
        par1NBTTagCompound.setByte("color", this.color);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        
        this.blockData.id = par1NBTTagCompound.getInteger("blockid");
        this.blockData.metadata = par1NBTTagCompound.getInteger("metadata");
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
    
    public byte getColor() {
        return this.color;
    }
    
    public void setColor(byte color) {
        this.color = color;
    }
    
    public static BlockData getBlockData(int x, int y, int z) {
        return TileEntityBlueprint.instancesClient.get(Arrays.hashCode(new int[] { x, y, z }));
    }
    
    @Override
    public void validate() {
        super.validate();
        
        if (this.worldObj.isRemote) {
            TileEntityBlueprint.instancesClient.put(Arrays.hashCode(new int[] { this.xCoord, this.yCoord, this.zCoord }), this.blockData);
        }
    }
    
    @Override
    public void onChunkUnload() {
        if (this.worldObj.isRemote) {
            TileEntityBlueprint.instancesClient.remove(Arrays.hashCode(new int[] { this.xCoord, this.yCoord, this.zCoord }));
        }
    }
    
    @Override
    public void invalidate() {
        super.invalidate();
        
        if (this.worldObj.isRemote) {
            TileEntityBlueprint.instancesClient.remove(Arrays.hashCode(new int[] { this.xCoord, this.yCoord, this.zCoord }));
        }
    }
}
