package buildtowin;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import net.minecraft.block.Block;
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
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class TileEntityBuildingController extends TileEntity {
    
    private byte mode; // 0 = Time; 1 = versus controller; 2 = versus member
    
    private int rawConnectedBuildingControllers[];
    
    private ArrayList<TileEntityBuildingController> connectedBuildingControllers = new ArrayList<TileEntityBuildingController>();
    
    private ArrayList<String> connectedPlayers = new ArrayList<String>();
    
    private ArrayList<EntityPlayer> connectedAndOnlinePlayers = new ArrayList<EntityPlayer>();
    
    private ArrayList<BlockData> blockDataList = new ArrayList<BlockData>();
    
    private long plannedTimespan = 0;
    
    private long deadline = 0;
    
    private long sleptTime = 0;
    
    private int finishedBlocks = 0;
    
    private byte color = 0;
    
    private int timer = 0;
    
    public TileEntityBuildingController() {
    }
    
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setByte("mode", this.mode);
        
        NBTTagList connectedPlayersNbt = new NBTTagList();
        
        for (String player : this.connectedPlayers) {
            connectedPlayersNbt.appendTag(new NBTTagString("", player));
        }
        
        par1NBTTagCompound.setTag("players", connectedPlayersNbt);
        
        int rawConnectedBuildingControllers[] = new int[this.connectedBuildingControllers.size() * 3];
        
        for (int i = 0; i < this.connectedBuildingControllers.size(); ++i) {
            rawConnectedBuildingControllers[i * 3] = this.connectedBuildingControllers.get(i).xCoord;
            rawConnectedBuildingControllers[i * 3 + 1] = this.connectedBuildingControllers.get(i).yCoord;
            rawConnectedBuildingControllers[i * 3 + 2] = this.connectedBuildingControllers.get(i).zCoord;
        }
        
        par1NBTTagCompound.setIntArray("buildingcontrollers", rawConnectedBuildingControllers);
        
        int rawBlockDataList[] = new int[this.blockDataList.size() * 5];
        
        for (int i = 0; i < this.blockDataList.size(); ++i) {
            rawBlockDataList[i * 5] = this.blockDataList.get(i).x;
            rawBlockDataList[i * 5 + 1] = this.blockDataList.get(i).y;
            rawBlockDataList[i * 5 + 2] = this.blockDataList.get(i).z;
            rawBlockDataList[i * 5 + 3] = this.blockDataList.get(i).id;
            rawBlockDataList[i * 5 + 4] = this.blockDataList.get(i).metadata;
        }
        
        par1NBTTagCompound.setIntArray("blockdatalist", rawBlockDataList);
        
        par1NBTTagCompound.setLong("plantmspn", this.plannedTimespan);
        
        par1NBTTagCompound.setLong("deadline", this.deadline);
        
        par1NBTTagCompound.setLong("slepttime", this.sleptTime);
        
        par1NBTTagCompound.setInteger("finishedblocks", this.finishedBlocks);
        
        par1NBTTagCompound.setByte("color", this.color);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        
        this.mode = par1NBTTagCompound.getByte("mode");
        
        this.connectedPlayers.clear();
        NBTTagList connectedPlayersNbt = (NBTTagList) par1NBTTagCompound.getTag("players");
        
        for (int i = 0; i < connectedPlayersNbt.tagCount(); ++i) {
            this.connectedPlayers.add(((NBTTagString) connectedPlayersNbt.tagAt(i)).data);
        }
        
        this.connectedBuildingControllers.clear();
        this.rawConnectedBuildingControllers = par1NBTTagCompound.getIntArray("buildingcontrollers");
        
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
        
        this.sleptTime = par1NBTTagCompound.getLong("slepttime");
        
        this.finishedBlocks = par1NBTTagCompound.getInteger("finishedblocks");
        
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
    
    public Packet getDescriptionPacketOptimized() {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        
        try {
            dataoutputstream.writeInt(this.xCoord);
            dataoutputstream.writeInt(this.yCoord);
            dataoutputstream.writeInt(this.zCoord);
            
            dataoutputstream.writeByte(this.mode);
            
            this.refreshConnectedAndOnlinePlayers();
            dataoutputstream.writeInt(this.connectedAndOnlinePlayers.size());
            
            for (EntityPlayer player : this.connectedAndOnlinePlayers) {
                dataoutputstream.writeInt(player.entityId);
            }
            
            dataoutputstream.writeInt(this.connectedBuildingControllers.size());
            
            for (TileEntityBuildingController buildingController : this.connectedBuildingControllers) {
                dataoutputstream.writeInt(buildingController.xCoord);
                dataoutputstream.writeInt(buildingController.yCoord);
                dataoutputstream.writeInt(buildingController.zCoord);
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
            dataoutputstream.writeLong(this.sleptTime);
            dataoutputstream.writeInt(this.finishedBlocks);
            dataoutputstream.writeByte(this.color);
            
            return new Packet250CustomPayload("btwbcupdt", bytearrayoutputstream.toByteArray());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        
        return null;
    }
    
    public void onDataPacketOptimized(DataInputStream inputStream) throws IOException {
        this.mode = inputStream.readByte();
        
        this.connectedAndOnlinePlayers.clear();
        int connectedAndOnlinePlayersCount = inputStream.readInt();
        
        for (int i = 0; i < connectedAndOnlinePlayersCount; ++i) {
            Entity entity = this.worldObj.getEntityByID(inputStream.readInt());
            
            if (entity != null && entity instanceof EntityPlayer) {
                this.connectedAndOnlinePlayers.add((EntityPlayer) entity);
            }
        }
        
        this.connectedBuildingControllers.clear();
        int connectedBuildingControllersCount = inputStream.readInt();
        
        for (int i = 0; i < connectedBuildingControllersCount; ++i) {
            TileEntityBuildingController buildingController = (TileEntityBuildingController) this.worldObj.getBlockTileEntity(
                    inputStream.readInt(), inputStream.readInt(), inputStream.readInt());
            
            if (buildingController != null) {
                this.connectedBuildingControllers.add(buildingController);
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
        this.sleptTime = inputStream.readLong();
        this.finishedBlocks = inputStream.readInt();
        this.color = inputStream.readByte();
    }
    
    @Override
    public void updateEntity() {
        if (this.mode != 0) {
            this.refreshBuildingControllers();
        }
        
        this.updateBlockData();
        
        if (!this.worldObj.isRemote) {
            this.checkGameStatus();
            
            if (this.timer == 100) {
                PacketDispatcher.sendPacketToAllPlayers(this.getDescriptionPacketOptimized());
                this.timer = 0;
            }
            
            ++this.timer;
        }
    }
    
    private void checkGameStatus() {
        if (this.getDeadline() != 0) {
            if (this.finishedBlocks == this.blockDataList.size()) {
                this.deadline = 0;
                
                this.sendWinMessage();
                
                for (TileEntityBuildingController buildingController : this.connectedBuildingControllers) {
                    buildingController.deadline = 0;
                    buildingController.sendLoseMessage();
                }
            } else if (this.deadline <= this.getRealWorldTime()) {
                this.deadline = 0;
                
                if (this.connectedBuildingControllers.size() == 0) {
                    this.sendLoseMessage();
                } else {
                    TileEntityBuildingController bestTeam = this;
                    
                    for (TileEntityBuildingController buildingController : this.connectedBuildingControllers) {
                        if (buildingController.getFinishedBlocks() > bestTeam.getFinishedBlocks()) {
                            bestTeam = buildingController;
                        }
                    }
                    
                    bestTeam.sendWinMessage();
                    
                    if (this != bestTeam) {
                        this.sendLoseMessage();
                    }
                    
                    for (TileEntityBuildingController buildingController : this.connectedBuildingControllers) {
                        if (buildingController != bestTeam) {
                            buildingController.deadline = 0;
                            buildingController.sendLoseMessage();
                        }
                    }
                }
            }
        }
    }
    
    private void sendWinMessage() {
        if (this.getConnectedAndOnlinePlayers().size() > 1) {
            this.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> Your team has won."), false);
        } else {
            this.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> You have won."), false);
        }
    }
    
    private void sendLoseMessage() {
        if (this.connectedBuildingControllers.size() > 0) {
            if (this.getConnectedAndOnlinePlayers().size() > 1) {
                this.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> Your team reached the " + this.getRanking() + ". place."), false);
            } else {
                this.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> You have reached the " + this.getRanking() + ". place."), false);
            }
        } else {
            if (this.getConnectedAndOnlinePlayers().size() > 1) {
                this.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> Your team has lost."), false);
            } else {
                this.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> You have lost."), false);
            }
        }
    }
    
    public void updateBlockData() {
        Iterator<BlockData> iter = this.blockDataList.iterator();
        this.finishedBlocks = 0;
        
        while (iter.hasNext()) {
            BlockData blockData = iter.next();
            int realBlockId = this.worldObj.getBlockId(blockData.x, blockData.y, blockData.z);
            
            if (realBlockId == blockData.id) {
                ++this.finishedBlocks;
            }
            
            if (realBlockId != BuildToWin.getBlueprint().blockID && realBlockId != blockData.id) {
                this.refreshBlueprint(blockData, false);
            }
        }
    }
    
    public void connectPlayer(EntityPlayer entityPlayer) {
        if (this.getDeadline() != 0) {
            BuildToWin.printChatMessage(this.worldObj, "Could not connect, because the game is running.");
        } else {
            if (!this.worldObj.isRemote) {
                if (!this.isPlayerConnected(entityPlayer)) {
                    TileEntityBuildingController buildingController = BuildToWin.buildingControllerListServer.getBuildingController(entityPlayer);
                    
                    if (buildingController != null) {
                        buildingController.disconnectPlayer(entityPlayer);
                    }
                    
                    this.connectedPlayers.add(entityPlayer.username);
                    this.connectedAndOnlinePlayers.add(entityPlayer);
                }
            } else if (!this.isPlayerConnectedAndOnline(entityPlayer)) {
                TileEntityBuildingController buildingController = BuildToWin.buildingControllerListClient.getBuildingController(entityPlayer);
                
                if (buildingController != null) {
                    buildingController.disconnectPlayer(entityPlayer);
                }
                
                this.connectedAndOnlinePlayers.add(entityPlayer);
                
                this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                BuildToWin.printChatMessage(this.worldObj, "Connected to the Building Controller.");
            }
        }
    }
    
    public void disconnectPlayer(EntityPlayer entityPlayer) {
        if (this.getDeadline() != 0) {
            BuildToWin.printChatMessage(this.worldObj, "Could not disconnect, because the game is running.");
        } else {
            if (!this.worldObj.isRemote) {
                if (this.isPlayerConnected(entityPlayer)) {
                    this.connectedPlayers.remove(entityPlayer.username);
                    this.connectedAndOnlinePlayers.remove(entityPlayer);
                }
            } else if (this.isPlayerConnectedAndOnline(entityPlayer)) {
                this.connectedAndOnlinePlayers.remove(entityPlayer);
                
                this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                BuildToWin.printChatMessage(this.worldObj, "Disconnected from the Building Controller.");
            }
        }
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
    
    public void sendPacketToConnectedPlayers(Packet packet, boolean synchronize) {
        if (this.mode == 1 && synchronize) {
            for (TileEntityBuildingController buildingController : this.connectedBuildingControllers) {
                buildingController.sendPacketToConnectedPlayers(packet, false);
            }
        }
        
        for (EntityPlayer player : this.connectedAndOnlinePlayers) {
            PacketDispatcher.sendPacketToPlayer(packet, (Player) player);
        }
    }
    
    public void startGame(EntityPlayer entityPlayer, boolean synchronize) {
        if (this.mode == 1 && synchronize) {
            for (TileEntityBuildingController buildingController : this.connectedBuildingControllers) {
                buildingController.startGame(entityPlayer, false);
            }
        }
        
        this.refreshConnectedAndOnlinePlayers();
        
        if (this.getConnectedAndOnlinePlayers().isEmpty()) {
            PacketDispatcher.sendPacketToPlayer(new Packet3Chat("<BuildToWin> Could not start the game, because no players are connected."), (Player) entityPlayer);
        } else if (this.getBlockDataList().size() == 0) {
            PacketDispatcher.sendPacketToPlayer(new Packet3Chat("<BuildToWin> Could not start the game, because no blueprints exist."), (Player) entityPlayer);
        } else {
            this.resetAllBlocks();
            this.deadline = this.getRealWorldTime() + this.getPlannedTimespan();
            
            PacketDispatcher.sendPacketToPlayer(new Packet3Chat("<BuildToWin> Started the game successfully."), (Player) entityPlayer);
            this.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> The game has started."), true);
        }
    }
    
    public void stopGame(EntityPlayer entityPlayer, boolean synchronize) {
        if (this.mode == 1 && synchronize) {
            for (TileEntityBuildingController buildingController : this.connectedBuildingControllers) {
                buildingController.stopGame(entityPlayer, false);
            }
        }
        
        this.deadline = 0;
        
        PacketDispatcher.sendPacketToPlayer(new Packet3Chat("<BuildToWin> Stopped the game successfully."), (Player) entityPlayer);
        this.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> The game has been stopped."), true);
    }
    
    public void addBuildingController(TileEntityBuildingController buildingControllerToConnect) {
        if (!this.connectedBuildingControllers.contains(buildingControllerToConnect)) {
            this.connectedBuildingControllers.add(buildingControllerToConnect);
        }
    }
    
    public void removeBuildingController(TileEntityBuildingController buildingControllerToRemove) {
        this.connectedBuildingControllers.remove(buildingControllerToRemove);
    }
    
    public void disconnectFromBuildingControllers() {
        for (TileEntityBuildingController buildingController : this.connectedBuildingControllers) {
            if (this.mode == 1) {
                buildingController.getConnectedBuildingControllers().clear();
                buildingController.mode = 0;
            } else {
                buildingController.removeBuildingController(this);
            }
        }
    }
    
    public void refreshBuildingControllers() {
        if (this.rawConnectedBuildingControllers != null) {
            if (this.rawConnectedBuildingControllers.length != 0 && this.connectedBuildingControllers.size() == 0) {
                for (int i = 0; i < this.rawConnectedBuildingControllers.length / 3; ++i) {
                    this.connectedBuildingControllers.add((TileEntityBuildingController) this.worldObj.getBlockTileEntity(
                            this.rawConnectedBuildingControllers[i * 3],
                            this.rawConnectedBuildingControllers[i * 3 + 1],
                            this.rawConnectedBuildingControllers[i * 3 + 2]));
                }
            }
        }
        
        Iterator<TileEntityBuildingController> iter = this.connectedBuildingControllers.iterator();
        
        while (iter.hasNext()) {
            TileEntityBuildingController buildingController = iter.next();
            
            if (buildingController == null
                    || this.worldObj.getBlockTileEntity(buildingController.xCoord, buildingController.yCoord, buildingController.zCoord) != buildingController) {
                iter.remove();
            }
        }
    }
    
    public void addBlockData(BlockData blockData, boolean synchronize) {
        if (this.mode == 1 && synchronize) {
            for (TileEntityBuildingController buildingController : this.connectedBuildingControllers) {
                buildingController.addBlockData(buildingController.makeBlockDataAbsolute(this.makeBlockDataRelative(blockData)), false);
            }
        }
        
        this.blockDataList.add(blockData);
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
    
    public BlockData makeBlockDataAbsolute(BlockData blockData) {
        return new BlockData(this.xCoord + blockData.x, this.yCoord + blockData.y, this.zCoord + blockData.z, blockData.id, blockData.metadata);
    }
    
    public BlockData makeBlockDataRelative(BlockData blockData) {
        return new BlockData(blockData.x - this.xCoord, blockData.y - this.yCoord, blockData.z - this.zCoord, blockData.id, blockData.metadata);
    }
    
    public void refreshBlueprint(BlockData blockData, boolean synchronize) {
        if (this.mode == 1 && synchronize) {
            for (TileEntityBuildingController buildingController : this.connectedBuildingControllers) {
                buildingController.refreshBlueprint(buildingController.makeBlockDataAbsolute(this.makeBlockDataRelative(blockData)), false);
            }
        }
        
        this.worldObj.setBlock(blockData.x, blockData.y, blockData.z, BuildToWin.getBlueprint().blockID, 0, 2);
        
        TileEntityBlueprint blueprint = (TileEntityBlueprint) this.worldObj.getBlockTileEntity(blockData.x, blockData.y, blockData.z);
        
        blueprint.setBlockId(blockData.id);
        blueprint.setMetadata(blockData.metadata);
        blueprint.setColor(this.color);
    }
    
    public void placeBlueprint(BlockData blockData, boolean synchronize) {
        if (blockData.id != BuildToWin.getBuildingController().blockID && blockData.id != BuildToWin.getBlueprint().blockID) {
            if (blockData.id == Block.doorWood.blockID || blockData.id == Block.doorSteel.blockID) {
                this.placeBlueprintDoor(blockData, synchronize);
            } else if (blockData.id == Block.bed.blockID) {
                this.placeBlueprintBed(blockData, synchronize);
            } else {
                this.refreshBlueprint(blockData, synchronize);
                this.addBlockData(blockData, synchronize);
            }
        }
    }
    
    private void placeBlueprintDoor(BlockData blockData, boolean synchronize) {
        if (this.worldObj.getBlockId(blockData.x, blockData.y + 1, blockData.z) == blockData.id) {
            int metadata = this.worldObj.getBlockMetadata(blockData.x, blockData.y + 1, blockData.z);
            BlockData overCurrentData = new BlockData(blockData.x, blockData.y + 1, blockData.z, blockData.id, metadata);
            
            this.refreshBlueprint(overCurrentData, synchronize);
            this.addBlockData(overCurrentData, synchronize);
        } else if (this.worldObj.getBlockId(blockData.x, blockData.y - 1, blockData.z) == blockData.id) {
            int metadata = this.worldObj.getBlockMetadata(blockData.x, blockData.y - 1, blockData.z);
            BlockData underCurrentData = new BlockData(blockData.x, blockData.y - 1, blockData.z, blockData.id, metadata);
            
            this.refreshBlueprint(underCurrentData, synchronize);
            this.addBlockData(underCurrentData, synchronize);
        }
        
        this.refreshBlueprint(blockData, synchronize);
        this.addBlockData(blockData, synchronize);
    }
    
    private void placeBlueprintBed(BlockData blockData, boolean synchronize) {
        if (this.worldObj.getBlockId(blockData.x + 1, blockData.y, blockData.z) == blockData.id) {
            int metadata = this.worldObj.getBlockMetadata(blockData.x + 1, blockData.y, blockData.z);
            BlockData blockDataSecondPart = new BlockData(blockData.x + 1, blockData.y, blockData.z, blockData.id, metadata);
            
            this.refreshBlueprint(blockDataSecondPart, synchronize);
            this.addBlockData(blockDataSecondPart, synchronize);
        } else if (this.worldObj.getBlockId(blockData.x, blockData.y, blockData.z + 1) == blockData.id) {
            int metadata = this.worldObj.getBlockMetadata(blockData.x, blockData.y, blockData.z + 1);
            BlockData blockDataSecondPart = new BlockData(blockData.x, blockData.y, blockData.z + 1, blockData.id, metadata);
            
            this.refreshBlueprint(blockDataSecondPart, synchronize);
            this.addBlockData(blockDataSecondPart, synchronize);
        } else if (this.worldObj.getBlockId(blockData.x - 1, blockData.y, blockData.z) == blockData.id) {
            int metadata = this.worldObj.getBlockMetadata(blockData.x - 1, blockData.y, blockData.z);
            BlockData blockDataSecondPart = new BlockData(blockData.x - 1, blockData.y, blockData.z, blockData.id, metadata);
            
            this.refreshBlueprint(blockDataSecondPart, synchronize);
            this.addBlockData(blockDataSecondPart, synchronize);
        } else if (this.worldObj.getBlockId(blockData.x, blockData.y, blockData.z - 1) == blockData.id) {
            int metadata = this.worldObj.getBlockMetadata(blockData.x, blockData.y, blockData.z - 1);
            BlockData blockDataSecondPart = new BlockData(blockData.x, blockData.y, blockData.z - 1, blockData.id, metadata);
            
            this.refreshBlueprint(blockDataSecondPart, synchronize);
            this.addBlockData(blockDataSecondPart, synchronize);
        }
        
        this.refreshBlueprint(blockData, synchronize);
        this.addBlockData(blockData, synchronize);
    }
    
    public void loadBlueprintRelative(ArrayList<BlockData> blockDataList, boolean synchronize) {
        if (this.mode == 1 && synchronize) {
            for (TileEntityBuildingController buildingController : this.connectedBuildingControllers) {
                buildingController.loadBlueprintRelative(blockDataList, false);
            }
        }
        
        this.removeAllBlocks();
        
        for (BlockData blockData : blockDataList) {
            BlockData absolute = this.makeBlockDataAbsolute(blockData);
            this.refreshBlueprint(absolute, false);
            this.addBlockData(absolute, false);
        }
    }
    
    public void removeBlueprint(BlockData blockData, boolean synchronize) {
        if (this.mode == 1 && synchronize) {
            for (TileEntityBuildingController buildingController : this.connectedBuildingControllers) {
                buildingController.removeBlueprint(buildingController.makeBlockDataAbsolute(this.makeBlockDataRelative(blockData)), false);
            }
        }
        
        if (blockData != null) {
            if (blockData.id == Block.doorWood.blockID || blockData.id == Block.doorSteel.blockID) {
                this.removeBlueprintDoor(blockData);
            } else if (blockData.id == Block.bed.blockID) {
                this.removeBlueprintBed(blockData);
            } else {
                this.worldObj.setBlock(blockData.x, blockData.y, blockData.z, blockData.id, blockData.metadata, 3);
            }
        }
        
        this.blockDataList.remove(blockData);
    }
    
    public void removeBlueprintDoor(BlockData blockData) {
        BlockData secondPart = null;
        
        if ((secondPart = this.getBlockData(blockData.x, blockData.y + 1, blockData.z)) != null && secondPart.id == blockData.id) {
        } else if ((secondPart = this.getBlockData(blockData.x, blockData.y - 1, blockData.z)) != null && secondPart.id == blockData.id) {
        }
        
        this.blockDataList.remove(blockData);
        this.blockDataList.remove(secondPart);
        
        this.worldObj.setBlock(blockData.x, blockData.y, blockData.z, blockData.id, blockData.metadata, 3);
        this.worldObj.setBlock(secondPart.x, secondPart.y, secondPart.z, secondPart.id, secondPart.metadata, 3);
    }
    
    private void removeBlueprintBed(BlockData blockData) {
        BlockData secondPart = null;
        
        if ((secondPart = this.getBlockData(blockData.x + 1, blockData.y, blockData.z)) != null && secondPart.id == blockData.id) {
        } else if ((secondPart = this.getBlockData(blockData.x, blockData.y, blockData.z + 1)) != null && secondPart.id == blockData.id) {
        } else if ((secondPart = this.getBlockData(blockData.x - 1, blockData.y, blockData.z)) != null && secondPart.id == blockData.id) {
        } else if ((secondPart = this.getBlockData(blockData.x, blockData.y, blockData.z - 1)) != null && secondPart.id == blockData.id) {
        }
        
        this.blockDataList.remove(blockData);
        this.blockDataList.remove(secondPart);
        
        this.worldObj.setBlock(blockData.x, blockData.y, blockData.z, blockData.id, blockData.metadata, 3);
        this.worldObj.setBlock(secondPart.x, secondPart.y, secondPart.z, secondPart.id, secondPart.metadata, 3);
    }
    
    public void removeAllBlocks() {
        Iterator<BlockData> iter = this.blockDataList.iterator();
        this.finishedBlocks = 0;
        
        while (iter.hasNext()) {
            BlockData blockData = iter.next();
            
            if (this.worldObj.getBlockId(blockData.x, blockData.y, blockData.z) != BuildToWin.getBlueprint().blockID) {
                this.worldObj.setBlock(blockData.x, blockData.y, blockData.z, blockData.id, blockData.metadata, 3);
            } else {
                this.worldObj.setBlock(blockData.x, blockData.y, blockData.z, 0, 0, 3);
            }
            
            iter.remove();
        }
    }
    
    public void resetAllBlocks() {
        Iterator<BlockData> iter = this.blockDataList.iterator();
        this.finishedBlocks = 0;
        
        while (iter.hasNext()) {
            BlockData blockData = iter.next();
            this.refreshBlueprint(blockData, false);
        }
    }
    
    public ArrayList<BlockData> getBlockDataListRelative() {
        ArrayList<BlockData> blockDataListRelative = new ArrayList<BlockData>();
        
        for (BlockData blockData : this.blockDataList) {
            blockDataListRelative.add(this.makeBlockDataRelative(blockData));
        }
        
        return blockDataListRelative;
    }
    
    public void refreshTimespan(long newTimespan, boolean synchronize) {
        if (this.mode == 1 && synchronize) {
            for (TileEntityBuildingController buildingController : this.connectedBuildingControllers) {
                buildingController.refreshTimespan(newTimespan, false);
            }
        }
        
        if (this.deadline != 0) {
            this.deadline = this.getRealWorldTime() + newTimespan;
        }
        
        this.plannedTimespan = newTimespan;
    }
    
    public void refreshColor(byte color) {
        this.color = color;
        
        for (BlockData blockData : this.blockDataList) {
            TileEntityBlueprint blueprint = (TileEntityBlueprint) this.worldObj.getBlockTileEntity(blockData.x, blockData.y, blockData.z);
            blueprint.setColor(this.color);
        }
    }
    
    public byte getColor() {
        return this.color;
    }
    
    public byte getMode() {
        return this.mode;
    }
    
    public void setMode(byte mode) {
        this.mode = mode;
    }
    
    public ArrayList<TileEntityBuildingController> getConnectedBuildingControllers() {
        return this.connectedBuildingControllers;
    }
    
    public ArrayList<String> getConnectedPlayers() {
        return this.connectedPlayers;
    }
    
    public ArrayList<EntityPlayer> getConnectedAndOnlinePlayers() {
        return this.connectedAndOnlinePlayers;
    }
    
    public long getPlannedTimespan() {
        return this.plannedTimespan;
    }
    
    public long getDeadline() {
        return this.deadline;
    }
    
    public long getSleptTime() {
        return this.sleptTime;
    }
    
    public long getRealWorldTime() {
        return this.worldObj.getTotalWorldTime() + this.sleptTime;
    }
    
    public void setSleptTime(long sleptTime) {
        this.sleptTime = sleptTime;
    }
    
    public int getFinishedBlocks() {
        return this.finishedBlocks;
    }
    
    public ArrayList<BlockData> getBlockDataList() {
        return this.blockDataList;
    }
    
    public int getProgress() {
        if (this.blockDataList.size() != 0) {
            return 100 * this.finishedBlocks / this.blockDataList.size();
        }
        
        return 100;
    }
    
    public int getRanking() {
        if (this.connectedBuildingControllers.size() == 0) {
            return 0;
        }
        
        ArrayList<Integer> unsortedProgresses = new ArrayList<Integer>();
        Integer progress = this.getProgress();
        unsortedProgresses.add(progress);
        
        for (TileEntityBuildingController buildingController : this.connectedBuildingControllers) {
            unsortedProgresses.add(buildingController.getProgress());
        }
        
        Collections.sort(unsortedProgresses, Collections.reverseOrder());
        
        return unsortedProgresses.indexOf(progress);
    }
    
}
