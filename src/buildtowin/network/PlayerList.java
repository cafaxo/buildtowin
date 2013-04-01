package buildtowin.network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;

public class PlayerList {
    
    public static HashMap<String, TileEntity> playerToTileEntityMapServer = new HashMap<String, TileEntity>();
    
    public static HashMap<String, TileEntity> playerToTileEntityMapClient = new HashMap<String, TileEntity>();
    
    private ArrayList<String> connectedPlayers;
    
    private TileEntity tileEntity;
    
    public PlayerList(TileEntity tileEntity) {
        this.tileEntity = tileEntity;
        this.connectedPlayers = new ArrayList<String>();
    }
    
    public boolean isPlayerConnected(EntityPlayer entityPlayer) {
        return this.getPlayerToTileEntityMap(!entityPlayer.worldObj.isRemote).get(entityPlayer.username) == this.tileEntity;
    }
    
    public void connectPlayer(EntityPlayer entityPlayer) {
        if (!this.connectedPlayers.contains(entityPlayer.username)) {
            this.connectedPlayers.add(entityPlayer.username);
            this.getPlayerToTileEntityMap(!entityPlayer.worldObj.isRemote).put(entityPlayer.username, this.tileEntity);
        }
    }
    
    public void disconnectPlayer(EntityPlayer entityPlayer) {
        if (this.connectedPlayers.contains(entityPlayer.username)) {
            this.connectedPlayers.remove(entityPlayer.username);
            this.getPlayerToTileEntityMap(!entityPlayer.worldObj.isRemote).remove(entityPlayer.username);
        }
    }
    
    public NBTTagList getTagList() {
        NBTTagList connectedPlayersNbt = new NBTTagList();
        
        for (String player : this.connectedPlayers) {
            connectedPlayersNbt.appendTag(new NBTTagString("", player));
        }
        
        return connectedPlayersNbt;
    }
    
    public void readTagList(NBTTagList tagList) {
        for (int i = 0; i < tagList.tagCount(); ++i) {
            this.connectedPlayers.add(((NBTTagString) tagList.tagAt(i)).data);
            this.playerToTileEntityMapServer.put(((NBTTagString) tagList.tagAt(i)).data, this.tileEntity);
        }
    }
    
    public Packet getUpdatePacket(int tileEntityX, int tileEntityY, int tileEntityZ) {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        
        try {
            dataoutputstream.writeInt(PacketIds.PLAYERLIST_UPDATE);
            
            dataoutputstream.writeInt(tileEntityX);
            dataoutputstream.writeInt(tileEntityY);
            dataoutputstream.writeInt(tileEntityZ);
            
            dataoutputstream.writeInt(this.connectedPlayers.size());
            
            for (String player : this.connectedPlayers) {
                dataoutputstream.writeUTF(player);
            }
            
            return new Packet250CustomPayload("btw", bytearrayoutputstream.toByteArray());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        
        return null;
    }
    
    public void onUpdatePacket(DataInputStream dataInputStream) throws IOException {
        int size = dataInputStream.readInt();
        
        for (int i = 0; i < size; ++i) {
            String player = dataInputStream.readUTF();
            this.playerToTileEntityMapClient.put(player, this.tileEntity);
            this.connectedPlayers.add(player);
        }
    }
    
    public static TileEntity getTileEntity(EntityPlayer entityPlayer) {
        TileEntity tileEntity = PlayerList.getPlayerToTileEntityMap(!entityPlayer.worldObj.isRemote).get(entityPlayer.username);
        
        if (tileEntity != null) {
            if (tileEntity == entityPlayer.worldObj.getBlockTileEntity(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord)) {
                return tileEntity;
            } else {
                PlayerList.getPlayerToTileEntityMap(!entityPlayer.worldObj.isRemote).remove(entityPlayer);
            }
        }
        
        return null;
    }
    
    public ArrayList<String> getConnectedPlayers() {
        return connectedPlayers;
    }
    
    public static HashMap<String, TileEntity> getPlayerToTileEntityMap(boolean onServer) {
        if (!onServer) {
            return PlayerList.playerToTileEntityMapClient;
        } else {
            return PlayerList.playerToTileEntityMapServer;
        }
    }
}
