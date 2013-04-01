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
import buildtowin.network.IPlayerListProvider;
import buildtowin.network.PacketIds;
import buildtowin.network.PlayerList;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class TileEntityTeamHub extends TileEntity implements IPlayerListProvider, IBlueprintProvider {
    
    private TileEntityGameHub gameHub;
    
    private PlayerList playerList;
    
    private Blueprint blueprint;
    
    private int syncTimer;
    
    private int finishedBlockCount;
    
    private int totalBlockCount;
    
    public TileEntityTeamHub() {
        this.blueprint = new Blueprint();
        this.playerList = new PlayerList(this);
        this.syncTimer = 0;
        this.finishedBlockCount = 0;
        this.totalBlockCount = 0;
    }
    
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setTag("players", this.playerList.getTagList());
        
        par1NBTTagCompound.setIntArray("blueprint", this.blueprint.encode());
    }
    
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        
        NBTTagList connectedPlayersNbt = (NBTTagList) par1NBTTagCompound.getTag("players");
        this.playerList.readTagList(connectedPlayersNbt);
        
        this.blueprint.decode(par1NBTTagCompound.getIntArray("blueprint"));
    }
    
    public Packet getUpdatePacket() {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        
        try {
            dataoutputstream.writeInt(PacketIds.TEAMHUB_UPDATE);
            
            dataoutputstream.writeInt(this.xCoord);
            dataoutputstream.writeInt(this.yCoord);
            dataoutputstream.writeInt(this.zCoord);
            
            dataoutputstream.writeInt(this.finishedBlockCount);
            dataoutputstream.writeInt(this.totalBlockCount);
            
            dataoutputstream.writeBoolean(this.gameHub != null);
            
            if (this.gameHub != null) {
                dataoutputstream.writeInt(this.gameHub.xCoord);
                dataoutputstream.writeInt(this.gameHub.yCoord);
                dataoutputstream.writeInt(this.gameHub.zCoord);
            }
            
            return new Packet250CustomPayload("btw", bytearrayoutputstream.toByteArray());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        
        return null;
    }
    
    public void onUpdatePacket(DataInputStream dataInputStream) throws IOException {
        this.finishedBlockCount = dataInputStream.readInt();
        this.totalBlockCount = dataInputStream.readInt();
        
        if (dataInputStream.readBoolean()) {
            this.gameHub = (TileEntityGameHub) this.worldObj.getBlockTileEntity(
                    dataInputStream.readInt(),
                    dataInputStream.readInt(),
                    dataInputStream.readInt());
        }
    }
    
    @Override
    public void updateEntity() {
        if (!this.worldObj.isRemote) {
            this.finishedBlockCount = this.blueprint.refresh();
            this.totalBlockCount = this.blueprint.getBlocks().size();
            
            if (this.syncTimer == 30) {
                PacketDispatcher.sendPacketToAllPlayers(this.playerList.getUpdatePacket(this.xCoord, this.yCoord, this.zCoord));
                PacketDispatcher.sendPacketToAllPlayers(this.getUpdatePacket());
                this.syncTimer = 0;
            }
            
            ++this.syncTimer;
        }
    }
    
    public void connectPlayer(EntityPlayer entityPlayer) {
        this.playerList.connectPlayer(entityPlayer);
        this.worldObj.markBlockForRenderUpdate(this.xCoord, this.yCoord, this.zCoord);
        
        BuildToWin.printChatMessage(entityPlayer, "Connected to Team Hub.");
    }
    
    public void disconnectPlayer(EntityPlayer entityPlayer) {
        this.playerList.disconnectPlayer(entityPlayer);
        this.worldObj.markBlockForRenderUpdate(this.xCoord, this.yCoord, this.zCoord);
        
        BuildToWin.printChatMessage(entityPlayer, "Disconnected from Team Hub.");
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
    
    public static TileEntityTeamHub getTeamHub(EntityPlayer entityPlayer) {
        TileEntity tileEntity = PlayerList.getTileEntity(entityPlayer);
        
        if (tileEntity instanceof TileEntityTeamHub) {
            return (TileEntityTeamHub) tileEntity;
        }
        
        return null;
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
            this.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> Your team has won."));
        } else {
            this.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> You have won."));
        }
    }
    
    public void sendLoseMessage(int ranking) {
        if (ranking != -1) {
            if (this.playerList.getConnectedPlayers().size() > 1) {
                this.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> Your team reached the " + ranking + ". place."));
            } else {
                this.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> You have reached the " + ranking + ". place."));
            }
        } else {
            if (this.playerList.getConnectedPlayers().size() > 1) {
                this.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> Your team has lost."));
            } else {
                this.sendPacketToConnectedPlayers(new Packet3Chat("<BuildToWin> You have lost."));
            }
        }
    }
    
    public void sendPacketToConnectedPlayers(Packet packet) {
        for (String player : this.playerList.getConnectedPlayers()) {
            EntityPlayer entityPlayer = null;
            
            if ((entityPlayer = this.worldObj.getPlayerEntityByName(player)) != null)
                PacketDispatcher.sendPacketToPlayer(packet, (Player) entityPlayer);
        }
    }
}
