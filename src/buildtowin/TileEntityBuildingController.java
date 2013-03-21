package buildtowin;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileEntityBuildingController extends TileEntity {
    private ArrayList<BlockData> blockDataList = new ArrayList<BlockData>();
    
    private int deadline = 0;
    
    private int finishedBlocks = 0;
    
    public TileEntityBuildingController() {
    }
    
    public void updateBlocks(World world) {
        Iterator<BlockData> iter = this.blockDataList.iterator();
        this.finishedBlocks = 0;
        
        while (iter.hasNext()) {
            BlockData blockData = iter.next();
            int realBlockId = world.getBlockId(blockData.x, blockData.y, blockData.z);
            
            if (realBlockId == blockData.id) {
                ++this.finishedBlocks;
            }
            
            if (realBlockId != BuildToWin.getBlueprint().blockID && realBlockId != blockData.id) {
                world.setBlock(blockData.x, blockData.y, blockData.z, BuildToWin.getBlueprint().blockID);
                
                TileEntityBlueprint te = (TileEntityBlueprint) world.getBlockTileEntity(blockData.x, blockData.y, blockData.z);
                
                if (te != null) {
                    te.setBlockId(blockData.id);
                }
            }
        }
    }
    
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        
        int encodedArray[] = new int[blockDataList.size() * 4];
        
        for (int i = 0; i < blockDataList.size(); ++i) {
            encodedArray[i * 4] = blockDataList.get(i).x;
            encodedArray[i * 4 + 1] = blockDataList.get(i).y;
            encodedArray[i * 4 + 2] = blockDataList.get(i).z;
            encodedArray[i * 4 + 3] = blockDataList.get(i).id;
        }
        
        par1NBTTagCompound.setIntArray("blockdatalist", encodedArray);
        par1NBTTagCompound.setInteger("finishedblocks", this.finishedBlocks);
        par1NBTTagCompound.setInteger("deadline", this.deadline);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        
        this.blockDataList.clear();
        int encodedArray[] = par1NBTTagCompound.getIntArray("blockdatalist");
        
        for (int i = 0; i < encodedArray.length / 4; ++i) {
            BlockData blockData = new BlockData(
                    encodedArray[i * 4],
                    encodedArray[i * 4 + 1],
                    encodedArray[i * 4 + 2],
                    encodedArray[i * 4 + 3]);
            
            this.blockDataList.add(blockData);
        }
        
        this.finishedBlocks = par1NBTTagCompound.getInteger("finishedblocks");
        this.deadline = par1NBTTagCompound.getInteger("deadline");
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
    
    public int getFinishedBlocks() {
        return finishedBlocks;
    }
    
    public ArrayList<BlockData> getBlockDataList() {
        return blockDataList;
    }
    
    public int getDeadline() {
        return deadline;
    }
    
    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }
    
    public void addBlock(BlockData blockData) {
        this.blockDataList.add(blockData);
    }
    
    public void removeBlock(BlockData blockData) {
        this.blockDataList.remove(blockData);
    }
}
