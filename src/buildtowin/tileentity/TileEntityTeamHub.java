package buildtowin.tileentity;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.tileentity.TileEntity;
import buildtowin.BuildToWin;
import buildtowin.blueprint.Blueprint;
import buildtowin.blueprint.IBlueprintProvider;
import buildtowin.network.PacketIds;
import buildtowin.util.IPlayerListProvider;
import buildtowin.util.ItemStackList;
import buildtowin.util.PlayerList;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class TileEntityTeamHub extends TileEntityConnectionHub implements IPlayerListProvider, IBlueprintProvider {
    
    private TileEntityGameHub gameHub;
    
    private TileEntityPenalizer penalizer;
    
    private TileEntityProtector protector;
    
    private ItemStackList teamChestContents;
    
    private PlayerList playerList;
    
    private Blueprint blueprint;
    
    private int energy;
    
    private int finishedBlockCount;
    
    private int totalBlockCount;
    
    public TileEntityTeamHub() {
        super(new Class[] { TileEntityGameHub.class, TileEntityPenalizer.class, TileEntityProtector.class, TileEntityShop.class });
        
        this.playerList = new PlayerList(this);
        this.blueprint = new Blueprint();
        this.teamChestContents = new ItemStackList();
        this.energy = 0;
        this.finishedBlockCount = 0;
        this.totalBlockCount = 0;
    }
    
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setTag("players", this.playerList.getTagList());
        
        par1NBTTagCompound.setIntArray("blueprint", this.blueprint.encode());
        
        par1NBTTagCompound.setInteger("energy", this.energy);
        
        par1NBTTagCompound.setTag("teamChestContents", this.teamChestContents.getTagList());
    }
    
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        
        NBTTagList connectedPlayersNbt = (NBTTagList) par1NBTTagCompound.getTag("players");
        this.playerList.readTagList(connectedPlayersNbt);
        
        this.blueprint.decode(par1NBTTagCompound.getIntArray("blueprint"));
        
        this.energy = par1NBTTagCompound.getInteger("energy");
        
        this.teamChestContents.readTagList(par1NBTTagCompound.getTagList("teamChestContents"));
    }
    
    @Override
    public boolean writeDescriptionPacket(DataOutputStream dataOutputStream) throws IOException {
        if (this.gameHub != null) {
            dataOutputStream.writeInt(this.finishedBlockCount);
            dataOutputStream.writeInt(this.totalBlockCount);
            dataOutputStream.writeInt(this.energy);
            
            dataOutputStream.writeInt(this.gameHub.xCoord);
            dataOutputStream.writeInt(this.gameHub.yCoord);
            dataOutputStream.writeInt(this.gameHub.zCoord);
            
            this.playerList.writeDescriptionPacket(dataOutputStream);
            
            return true;
        }
        
        return false;
    }
    
    @Override
    public void readDescriptionPacket(DataInputStream dataInputStream) throws IOException {
        this.finishedBlockCount = dataInputStream.readInt();
        this.totalBlockCount = dataInputStream.readInt();
        this.energy = dataInputStream.readInt();
        
        TileEntity tileEntity = this.worldObj.getBlockTileEntity(
                dataInputStream.readInt(),
                dataInputStream.readInt(),
                dataInputStream.readInt());
        
        if (tileEntity instanceof TileEntityGameHub) {
            this.gameHub = (TileEntityGameHub) tileEntity;
        }
        
        this.playerList.readDescriptionPacket(dataInputStream);
    }
    
    @Override
    public void updateEntity() {
        if (!this.worldObj.isRemote) {
            this.finishedBlockCount = this.blueprint.refresh();
            this.totalBlockCount = this.blueprint.getBlocks().size();
        }
        
        super.updateEntity();
    }
    
    @Override
    public boolean isValid() {
        return !this.isInvalid();
    }

    @Override
    public void onPlayerConnected(EntityPlayer entityPlayer) {
        this.worldObj.markBlockForRenderUpdate(this.xCoord, this.yCoord, this.zCoord);
        
        BuildToWin.printChatMessage(entityPlayer, "Connected to Team Hub.");
    }

    @Override
    public void onPlayerDisconnect(EntityPlayer entityPlayer) {
        this.worldObj.markBlockForRenderUpdate(this.xCoord, this.yCoord, this.zCoord);
        
        BuildToWin.printChatMessage(entityPlayer, "Disconnected from Team Hub.");
    }

    @Override
    protected void onSynchronization() {
        this.penalizer = null;
        this.protector = null;
        this.updateConnections();
        
        super.onSynchronization();
    }
    
    @Override
    public void onConnectionEstablished(TileEntity tileEntity) {
        if (tileEntity instanceof TileEntityPenalizer) {
            TileEntityPenalizer penalizer = (TileEntityPenalizer) tileEntity;
            
            penalizer.setTeamHub(this);
            this.penalizer = penalizer;
        } else if (tileEntity instanceof TileEntityProtector) {
            this.protector = (TileEntityProtector) tileEntity;
        } else if (tileEntity instanceof TileEntityShop) {
            ((TileEntityShop) tileEntity).setTeamHub(this);
        }
    }
    
    public int getFinishedBlockCount() {
        return finishedBlockCount;
    }
    
    public TileEntityGameHub getGameHub() {
        return gameHub;
    }
    
    public void setGameHub(TileEntityGameHub gameHub) {
        this.gameHub = gameHub;
    }
    
    public TileEntityPenalizer getPenalizer() {
        return penalizer;
    }
    
    public TileEntityProtector getProtector() {
        return protector;
    }
    
    @Override
    public PlayerList getPlayerList() {
        return this.playerList;
    }
    
    public Blueprint getBlueprint() {
        if (!this.worldObj.isRemote) {
            return this.blueprint;
        }
        
        return null;
    }
    
    public int getEnergy() {
        return energy;
    }
    
    public void setEnergy(int energy) {
        this.energy = energy;
    }
    
    public ItemStackList getTeamChestContents() {
        return teamChestContents;
    }
    
    @Override
    public void loadBlueprint(Blueprint blueprint) {
        this.getBlueprint().loadBlueprint(blueprint.getBlocks());
    }
    
    @Override
    public void validate() {
        super.validate();
        
        this.blueprint.setWorldObj(this.worldObj);
        this.blueprint.setOffset(this.xCoord, this.yCoord, this.zCoord);
    }
    
    public float getProgress() {
        if (this.totalBlockCount != 0) {
            return (float) this.finishedBlockCount / (float) this.totalBlockCount;
        }
        
        return 0.00F;
    }
    
    public void sendWinMessage() {
        if (this.playerList.getConnectedPlayers().size() > 1) {
            this.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> Your Team Won!"));
        } else {
            this.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> You Won!"));
        }
        
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        
        try {
            dataoutputstream.writeInt(PacketIds.TEAMHUB_WIN);
            
            this.sendPacketToConnectedPlayers(new Packet250CustomPayload("btw", bytearrayoutputstream.toByteArray()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    public void sendLoseMessage(int ranking) {
        if (this.playerList.getConnectedPlayers().size() > 1) {
            this.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> Your Team Reached The " + ranking + ". Place."));
        } else {
            this.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> You Reached The " + ranking + ". Place."));
        }
        
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        
        try {
            dataoutputstream.writeInt(PacketIds.TEAMHUB_LOSE);
            
            this.sendPacketToConnectedPlayers(new Packet250CustomPayload("btw", bytearrayoutputstream.toByteArray()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    public void sendPacketToConnectedPlayers(Packet packet) {
        for (String player : this.playerList.getConnectedPlayers()) {
            EntityPlayer entityPlayer = null;
            
            if ((entityPlayer = this.worldObj.getPlayerEntityByName(player)) != null) {
                PacketDispatcher.sendPacketToPlayer(packet, (Player) entityPlayer);
            }
        }
    }
}
