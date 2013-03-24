package buildtowin;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class TileEntityBuildingController extends TileEntity {
    private ArrayList<String> connectedPlayers = new ArrayList<String>();
    
    private ArrayList<EntityPlayer> connectedAndOnlinePlayers = new ArrayList<EntityPlayer>();
    
    private ArrayList<BlockData> blockDataList = new ArrayList<BlockData>();
    
    private long plannedTimespan = 0;
    
    private long deadline = 0;
    
    private int finishedBlocks = 0;
    
    public TileEntityBuildingController() {
    }
    
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        
        NBTTagList connectedPlayersNbt = new NBTTagList();
        
        for (String player : this.connectedPlayers) {
            connectedPlayersNbt.appendTag(new NBTTagString("", player));
        }
        
        par1NBTTagCompound.setTag("players", connectedPlayersNbt);
        
        int rawBlockDataList[] = new int[blockDataList.size() * 5];
        
        for (int i = 0; i < blockDataList.size(); ++i) {
            rawBlockDataList[i * 5] = blockDataList.get(i).x;
            rawBlockDataList[i * 5 + 1] = blockDataList.get(i).y;
            rawBlockDataList[i * 5 + 2] = blockDataList.get(i).z;
            rawBlockDataList[i * 5 + 3] = blockDataList.get(i).id;
            rawBlockDataList[i * 5 + 4] = blockDataList.get(i).metadata;
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
        
        for (int i = 0; i < rawBlockDataList.length / 5; ++i) {
            BlockData blockData = new BlockData(
                    rawBlockDataList[i * 5],
                    rawBlockDataList[i * 5 + 1],
                    rawBlockDataList[i * 5 + 2],
                    rawBlockDataList[i * 5 + 3],
                    rawBlockDataList[i * 5 + 4]);
            
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
    
    public Packet getDescriptionPacketOptimized() {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        
        try {
            dataoutputstream.writeInt(this.xCoord);
            dataoutputstream.writeInt(this.yCoord);
            dataoutputstream.writeInt(this.zCoord);
            
            this.refreshConnectedAndOnlinePlayers();
            dataoutputstream.writeInt(this.connectedAndOnlinePlayers.size());
            
            for (EntityPlayer player : this.connectedAndOnlinePlayers) {
                dataoutputstream.writeInt(player.entityId);
            }
            
            dataoutputstream.writeInt(this.blockDataList.size());
            
            for (BlockData blockData : this.blockDataList) {
                dataoutputstream.writeInt(blockData.x);
                dataoutputstream.writeInt(blockData.y);
                dataoutputstream.writeInt(blockData.z);
                dataoutputstream.writeInt(blockData.id);
                dataoutputstream.writeInt(blockData.metadata);
            }
            
            dataoutputstream.writeLong(this.plannedTimespan);
            dataoutputstream.writeLong(this.deadline);
            dataoutputstream.writeInt(this.finishedBlocks);
            
            return new Packet250CustomPayload("btwbcupdt", bytearrayoutputstream.toByteArray());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        
        return null;
    }
    
    public void onDataPacketOptimized(DataInputStream inputStream) throws IOException {
        this.connectedAndOnlinePlayers.clear();
        int connectedAndOnlinePlayersCount = inputStream.readInt();
        
        for (int i = 0; i < connectedAndOnlinePlayersCount; ++i) {
            Entity entity = this.worldObj.getEntityByID(inputStream.readInt());
            
            if (entity != null && entity instanceof EntityPlayer) {
                this.connectedAndOnlinePlayers.add((EntityPlayer) entity);
            }
        }
        
        this.blockDataList.clear();
        int blockDataCount = inputStream.readInt();
        
        for (int i = 0; i < blockDataCount; ++i) {
            BlockData blockData = new BlockData(inputStream.readInt(), inputStream.readInt(), inputStream.readInt(), inputStream.readInt(), inputStream.readInt());
            this.blockDataList.add(blockData);
        }
        
        this.plannedTimespan = inputStream.readLong();
        this.deadline = inputStream.readLong();
        this.finishedBlocks = inputStream.readInt();
        
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
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
        if (this.getDeadline() != 0) {
            if (this.worldObj.isRemote) {
                Minecraft mc = FMLClientHandler.instance().getClient();
                mc.ingameGUI.getChatGUI().printChatMessage(
                        "<BuildToWin> Could not connect, because the game is running.");
            }
        } else {
            if (!this.worldObj.isRemote) {
                if (!isPlayerConnected(entityPlayer)) {
                    TileEntityBuildingController buildingController = BuildToWin.getBuildingController().getTileEntity(entityPlayer);
                    
                    if (buildingController != null) {
                        buildingController.disconnectPlayer(entityPlayer);
                    }
                    
                    this.connectedPlayers.add(entityPlayer.username);
                }
            } else {
                Minecraft mc = FMLClientHandler.instance().getClient();
                mc.ingameGUI.getChatGUI().printChatMessage(
                        "<BuildToWin> Connected to the Building Controller.");
            }
        }
    }
    
    public void disconnectPlayer(EntityPlayer entityPlayer) {
        if (this.getDeadline() != 0) {
            if (this.worldObj.isRemote) {
                Minecraft mc = FMLClientHandler.instance().getClient();
                mc.ingameGUI.getChatGUI().printChatMessage(
                        "<BuildToWin> Could not disconnect, because the game is running.");
            }
        } else {
            if (!this.worldObj.isRemote) {
                this.connectedPlayers.remove(entityPlayer.username);
            } else {
                Minecraft mc = FMLClientHandler.instance().getClient();
                mc.ingameGUI.getChatGUI().printChatMessage(
                        "<BuildToWin> Disconnected from the Building Controller.");
            }
        }
    }
    
    public void placeBlueprint(int x, int y, int z, int id, int metadata) {
        if (this.getDeadline() == 0) {
            this.worldObj.setBlock(x, y, z, BuildToWin.getBlueprint().blockID);
            
            TileEntityBlueprint blueprint = (TileEntityBlueprint) this.worldObj.getBlockTileEntity(x, y, z);
            
            if (blueprint != null) {
                this.addBlock(new BlockData(x, y, z, id, metadata));
                blueprint.setBlockId(id);
            } else {
                throw new RuntimeException();
            }
        } else {
            if (this.worldObj.isRemote) {
                Minecraft mc = FMLClientHandler.instance().getClient();
                mc.ingameGUI.getChatGUI().printChatMessage(
                        "<BuildToWin> Could not place the blueprint, because the game is running.");
            }
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
    
    public boolean isPlayerConnected(EntityPlayer entityPlayer) {
        return this.connectedPlayers.contains(entityPlayer.username);
    }
    
    public boolean isPlayerConnectedAndOnline(EntityPlayer entityPlayer) {
        return this.connectedAndOnlinePlayers.contains(entityPlayer);
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
    
    public void startGame(EntityPlayer entityPlayer) {
        this.refreshConnectedAndOnlinePlayers();
        
        if (this.getConnectedAndOnlinePlayers().isEmpty()) {
            PacketDispatcher.sendPacketToPlayer(new Packet3Chat("<BuildToWin> Could not start the game, because no players are connected."), (Player) entityPlayer);
        } else if (this.getBlockDataList().size() == 0) {
            PacketDispatcher.sendPacketToPlayer(new Packet3Chat("<BuildToWin> Could not start the game, because no blueprints exist."), (Player) entityPlayer);
        } else {
            this.resetAllBlocks();
            this.setDeadline(this.worldObj.getTotalWorldTime() + this.getPlannedTimespan());
            
            PacketDispatcher.sendPacketToPlayer(new Packet3Chat("<BuildToWin> Started the game successfully."), (Player) entityPlayer);
            this.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> The game has started."));
        }
    }
    
    public void stopGame(EntityPlayer entityPlayer) {
        this.setDeadline(0);
        
        PacketDispatcher.sendPacketToPlayer(new Packet3Chat("<BuildToWin> Stopped the game successfully."), (Player) entityPlayer);
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
    
    public ArrayList<String> getConnectedPlayers() {
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
        world.setBlock(blockData.x, blockData.y, blockData.z, blockData.id, blockData.metadata, 3);
        this.blockDataList.remove(blockData);
    }
    
    public void refreshTimespan(long newTimespan) {
        if (this.deadline != 0) {
            this.deadline = this.worldObj.getTotalWorldTime() + newTimespan;
        }
        
        this.plannedTimespan = newTimespan;
    }
}
