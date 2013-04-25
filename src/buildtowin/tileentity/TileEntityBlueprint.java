package buildtowin.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import buildtowin.blueprint.BlockData;
import buildtowin.util.Color;

public class TileEntityBlueprint extends TileEntity {
    
    private BlockData blockData;
    
    private Color color;
    
    public TileEntityBlueprint() {
        this.blockData = new BlockData(0, 0);
        this.color = new Color(0.F, 0.F, 0.F);
        this.color.setFromId(0);
    }
    
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setInteger("blockid", this.blockData.savedId);
        par1NBTTagCompound.setInteger("metadata", this.blockData.savedMetadata);
        par1NBTTagCompound.setInteger("color", this.color.id);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        
        this.blockData.savedId = par1NBTTagCompound.getInteger("blockid");
        this.blockData.savedMetadata = par1NBTTagCompound.getInteger("metadata");
        this.color.setFromId(par1NBTTagCompound.getInteger("color"));
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
    
    public Color getColor() {
        return this.color;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
}
