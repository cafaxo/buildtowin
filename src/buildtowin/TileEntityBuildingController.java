package buildtowin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class TileEntityBuildingController extends TileEntity {
    private Set<String> connectedPlayers;
    
    ArrayList<EntityPlayer> connectedAndOnlinePlayers;
    
    private ArrayList<BlockData> blockDataList;
    
    private long plannedTimespan;
    
    private long deadline;
    
    private int finishedBlocks;
    
    public TileEntityBuildingController() {
        this.connectedPlayers = new HashSet<String>();
        this.connectedAndOnlinePlayers = new ArrayList<EntityPlayer>();
        this.blockDataList = new ArrayList<BlockData>();
        this.plannedTimespan = 0;
        this.deadline = 0;
        this.finishedBlocks = 0;
    }
    
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        
        NBTTagList connectedPlayersNbt = new NBTTagList();
        
        for (String player : this.connectedPlayers) {
            connectedPlayersNbt.appendTag(new NBTTagString("", player));
        }
        
        par1NBTTagCompound.setTag("players", connectedPlayersNbt);
        
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
        
        this.connectedPlayers.clear();
        NBTTagList connectedPlayersNbt = (NBTTagList) par1NBTTagCompound.getTag("players");
        
        for (int i = 0; i < connectedPlayersNbt.tagCount(); ++i) {
            this.connectedPlayers.add(((NBTTagString) connectedPlayersNbt.tagAt(i)).data);
        }
        
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
    
    public void updateBlocks() {
        Iterator<BlockData> iter = this.blockDataList.iterator();
        this.finishedBlocks = 0;
        
        while (iter.hasNext()) {
            BlockData blockData = iter.next();
            int realBlockId = this.worldObj.getBlockId(blockData.x, blockData.y, blockData.z);
            
            if (realBlockId == blockData.id) {
                ++this.finishedBlocks;
            }
            
            if (realBlockId != BuildToWin.getBlueprint().blockID && realBlockId != blockData.id) {
                this.worldObj.setBlock(blockData.x, blockData.y, blockData.z, BuildToWin.getBlueprint().blockID);
                
                TileEntityBlueprint te = (TileEntityBlueprint) this.worldObj.getBlockTileEntity(blockData.x, blockData.y, blockData.z);
                
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
            
            this.connectedPlayers.add(entityPlayer.username);
        }
    }
    
    public void disconnectPlayer(EntityPlayer entityPlayer) {
        this.connectedPlayers.remove(entityPlayer.username);
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
    
    public boolean isPlayerConnected(EntityPlayer entityPlayer) {
        return this.connectedPlayers.contains(entityPlayer.username);
    }
    
    public void refreshConnectedAndOnlinePlayers() {
        this.connectedAndOnlinePlayers.clear();
        
        for (String connectedPlayer : this.connectedPlayers) {
            EntityPlayer player = this.worldObj.getPlayerEntityByName(connectedPlayer);
            
            if (player != null) {
                this.connectedAndOnlinePlayers.add(player);
            }
        }
    }
    
    public void sendPacketToConnectedPlayers(Packet packet) {
        for (EntityPlayer player : this.connectedAndOnlinePlayers) {
            PacketDispatcher.sendPacketToPlayer(packet, (Player) player);
        }
    }
    
    public void resetAllBlocks() {
        Iterator<BlockData> iter = this.blockDataList.iterator();
        this.finishedBlocks = 0;
        
        while (iter.hasNext()) {
            BlockData blockData = iter.next();
            
            this.worldObj.setBlock(blockData.x, blockData.y, blockData.z, BuildToWin.getBlueprint().blockID);
            
            TileEntityBlueprint te = (TileEntityBlueprint) this.worldObj.getBlockTileEntity(blockData.x, blockData.y, blockData.z);
            
            if (te != null) {
                te.setBlockId(blockData.id);
            }
        }
    }
    
    public void startGame() {
        this.refreshConnectedAndOnlinePlayers();
        
        if (this.getConnectedAndOnlinePlayers().isEmpty()) {
            this.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> Could not start the game, because no players are connected."));
        } else if (this.getBlockDataList().size() == 0) {
            this.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> Could not start the game, because no blueprints exist."));
        } else {
            this.resetAllBlocks();
            this.setDeadline(this.worldObj.getTotalWorldTime() + this.getPlannedTimespan());
            
            this.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> The game has started."));
        }
    }
    
    public void stopGame() {
        this.setDeadline(0);
        PacketDispatcher.sendPacketToAllPlayers(new Packet3Chat("<BuildToWin> The game has been stopped."));
    }
    
    public void removeAllBlocks() {
        Iterator<BlockData> iter = this.blockDataList.iterator();
        this.finishedBlocks = 0;
        
        while (iter.hasNext()) {
            BlockData blockData = iter.next();
            this.worldObj.setBlock(blockData.x, blockData.y, blockData.z, blockData.id);
            iter.remove();
        }
    }
    
    public int getFinishedBlocks() {
        return finishedBlocks;
    }
    
    public ArrayList<BlockData> getBlockDataList() {
        return blockDataList;
    }
    
    public Set<String> getConnectedPlayers() {
        return connectedPlayers;
    }
    
    public ArrayList<EntityPlayer> getConnectedAndOnlinePlayers() {
        return connectedAndOnlinePlayers;
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
    
    public void removeBlock(BlockData blockData, World world) {
        world.setBlock(blockData.x, blockData.y, blockData.z, blockData.id);
        this.blockDataList.remove(blockData);
    }
    
    public void refreshTimespan(long newTimespan) {
        if (this.deadline != 0) {
            this.deadline = this.worldObj.getTotalWorldTime() + newTimespan;
        }
        
        this.plannedTimespan = newTimespan;
    }
}
