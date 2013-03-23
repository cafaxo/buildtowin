package buildtowin;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileEntityBuildingController extends TileEntity {
    private NBTTagList connectedPlayers = new NBTTagList();
    
    private ArrayList<BlockData> blockDataList = new ArrayList<BlockData>();
    
    private long plannedTimespan = 0;
    
    private long deadline = 0;
    
    private int finishedBlocks = 0;
    
    public TileEntityBuildingController() {
    }
    
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setTag("players", this.connectedPlayers);
        
        int rawBlockDataList[] = new int[blockDataList.size() * 4];
        
        for (int i = 0; i < blockDataList.size(); ++i) {
            rawBlockDataList[i * 4] = blockDataList.get(i).x;
            rawBlockDataList[i * 4 + 1] = blockDataList.get(i).y;
            rawBlockDataList[i * 4 + 2] = blockDataList.get(i).z;
            rawBlockDataList[i * 4 + 3] = blockDataList.get(i).id;
        }
        
        par1NBTTagCompound.setIntArray("blockdatalist", rawBlockDataList);
        
        par1NBTTagCompound.setLong("plantmspn", this.plannedTimespan);
        
        par1NBTTagCompound.setLong("deadline", this.deadline);
        
        par1NBTTagCompound.setInteger("finishedblocks", this.finishedBlocks);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        
        this.connectedPlayers = (NBTTagList) par1NBTTagCompound.getTag("players");
        
        this.blockDataList.clear();
        int rawBlockDataList[] = par1NBTTagCompound.getIntArray("blockdatalist");
        
        for (int i = 0; i < rawBlockDataList.length / 4; ++i) {
            BlockData blockData = new BlockData(
                    rawBlockDataList[i * 4],
                    rawBlockDataList[i * 4 + 1],
                    rawBlockDataList[i * 4 + 2],
                    rawBlockDataList[i * 4 + 3]);
            
            this.blockDataList.add(blockData);
        }
        
        this.plannedTimespan = par1NBTTagCompound.getLong("plantmspn");
        
        this.deadline = par1NBTTagCompound.getLong("deadline");
        
        this.finishedBlocks = par1NBTTagCompound.getInteger("finishedblocks");
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
    
    public void connectPlayer(EntityPlayer entityPlayer) {
        if (!isPlayerConnected(entityPlayer)) {
            TileEntityBuildingController buildingController = BuildToWin.getBuildingController().getTileEntity(entityPlayer);
            
            if (buildingController != null) {
                buildingController.disconnectPlayer(entityPlayer);
            }
            
            this.connectedPlayers.appendTag(new NBTTagString("", entityPlayer.username));
        }
    }
    
    public void disconnectPlayer(EntityPlayer entityPlayer) {
        int playerId = getPlayerId(entityPlayer);
        
        if (playerId != -1) {
            this.connectedPlayers.removeTag(playerId);
        }
    }
    
    public BlockData getBlockData(int x, int y, int z) {
        for (int i = 0; i < this.blockDataList.size(); ++i) {
            BlockData blockData = this.blockDataList.get(i);
            
            if (blockData.x == x && blockData.y == y && blockData.z == z) {
                return blockData;
            }
        }
        
        return null;
    }
    
    public int getPlayerId(EntityPlayer entityPlayer) {
        for (int i = 0; i < this.connectedPlayers.tagCount(); ++i) {
            if (entityPlayer.username.equals(((NBTTagString) this.connectedPlayers.tagAt(i)).data)) {
                return i;
            }
        }
        
        return -1;
    }
    
    public boolean isPlayerConnected(EntityPlayer entityPlayer) {
        if (this.getPlayerId(entityPlayer) != -1) {
            return true;
        } else {
            return false;
        }
    }
    
    public int getFinishedBlocks() {
        return finishedBlocks;
    }
    
    public ArrayList<BlockData> getBlockDataList() {
        return blockDataList;
    }
    
    public NBTTagList getConnectedPlayers() {
        return connectedPlayers;
    }
    
    public long getPlannedTimespan() {
        return plannedTimespan;
    }
    
    public void setPlannedTimespan(long plannedTimespan) {
        this.plannedTimespan = plannedTimespan;
    }
    
    public long getDeadline() {
        return deadline;
    }
    
    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }
    
    public void addBlock(BlockData blockData) {
        this.blockDataList.add(blockData);
    }
    
    public void removeBlock(BlockData blockData) {
        this.blockDataList.remove(blockData);
    }
}
